package com.danyazero.executorservice.controller;

import com.danyazero.executorservice.model.SubmissionCreatedEvent;
import com.danyazero.executorservice.model.SubmissionStatus;
import com.danyazero.executorservice.model.SubmissionUpdatedEvent;
import com.danyazero.executorservice.model.SubmissionUpdatedEventDto;
import com.danyazero.executorservice.utils.JavaCodeCompiler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionConsumer {
    private final KafkaTemplate<String, SubmissionUpdatedEvent> submissionKafkaTemplate;

    @PostConstruct
    public void init() {
        log.info("init submission consumer...");
    }

    @KafkaListener(topics = "submissions", groupId = "executor-service", containerFactory = "submissionListenerContainerFactory")
    public void listen(SubmissionCreatedEvent message) {
        log.info("Receive submission event with id -> {}, data -> {}", message.getEventId(), message.getData().toString());

        sendSubmissionStatusUpdate(message.getData().submissionId(), SubmissionStatus.QUEUED);

        if (message.getData().language().equalsIgnoreCase("java")) {
            try {
                var codeExecutor = new JavaCodeCompiler();
                var compilationError = codeExecutor.compile(message.getData().solution());
                log.info(compilationError);

                sendSubmissionStatusUpdate(message.getData().submissionId(), SubmissionStatus.COMPILED);


                sendSubmissionStatusUpdate(message.getData().submissionId(), SubmissionStatus.RUNNING);
                codeExecutor.executeWithParams(List.of("12,12,1,25"), 2);
                sendSubmissionStatusUpdate(message.getData().submissionId(), SubmissionStatus.ACCEPTED);

            } catch (IOException e) {
                sendSubmissionStatusUpdate(message.getData().submissionId(), SubmissionStatus.INTERNAL_ERROR);
                log.error("An I/O error occurred during compilation or execution", e);
            } catch (InterruptedException e) {
                sendSubmissionStatusUpdate(message.getData().submissionId(), SubmissionStatus.TIME_LIMIT_EXCEEDED);
                log.error("Sandbox process achieved timeout", e);
            }
        }

    }

    private void sendSubmissionStatusUpdate(Integer submissionId, SubmissionStatus submissionStatus) {
        var submissionQueuedEvent = new SubmissionUpdatedEvent(1, new SubmissionUpdatedEventDto(
                submissionId,
                submissionStatus
        ));

        submissionKafkaTemplate.sendDefault(submissionQueuedEvent).thenAccept(res ->
                log.info(
                        "Submission {} event {} has been produced. (topic={})",
                        submissionStatus.toString(),
                        submissionQueuedEvent.getEventId(),
                        res.getRecordMetadata().topic()
                ));

    }
}

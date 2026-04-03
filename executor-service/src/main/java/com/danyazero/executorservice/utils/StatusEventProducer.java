package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.model.SubmissionStatus;
import com.danyazero.executorservice.model.SubmissionUpdated;
import com.danyazero.executorservice.model.SubmissionUpdatedEvent;
import com.danyazero.executorservice.model.SubmissionUpdatedEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusEventProducer {

    private final KafkaTemplate<
        String,
        SubmissionUpdatedEvent
    > submissionKafkaTemplate;
    
    public void internalError(SubmissionUpdatedEventPayload payload) {
        this.accept(payload, SubmissionStatus.INTERNAL_ERROR);
    }

    public void cancelled(SubmissionUpdatedEventPayload payload) {
        this.accept(payload, SubmissionStatus.CANCELLED);
    }

    public void running(SubmissionUpdatedEventPayload payload) {
        this.accept(payload, SubmissionStatus.RUNNING);
    }
    
    public void wrongAnswer(SubmissionUpdatedEventPayload payload) {
        this.accept(payload, SubmissionStatus.WRONG_ANSWER);
    }
    
    public void accepted(SubmissionUpdatedEventPayload payload) {
        this.accept(payload, SubmissionStatus.ACCEPTED);
    }
    
    public void compiled(SubmissionUpdatedEventPayload payload) {
        this.accept(payload, SubmissionStatus.COMPILED);
    }
    
    public void compilationError(SubmissionUpdatedEventPayload payload) {
        this.accept(payload, SubmissionStatus.COMPILATION_ERROR);
    }

    public void unsupportedLanguage(SubmissionUpdatedEventPayload payload) {
        this.accept(payload, SubmissionStatus.UNSUPPORTED_LANGUAGE);
    }

    public void timeLimitExceeded(SubmissionUpdatedEventPayload payload) {
        this.accept(payload, SubmissionStatus.TIME_LIMIT_EXCEEDED);
    }

    public void accept(
        SubmissionUpdatedEventPayload payload,
        SubmissionStatus submissionStatus
    ) {
        var event = new SubmissionUpdatedEvent(
            1,
            new SubmissionUpdated(payload.userId(), payload.problemId(), payload.submissionId(), submissionStatus)
        );

        submissionKafkaTemplate
            .sendDefault("submission-" + payload.submissionId(), event)
            .thenAccept(result ->
                log.info(
                    "Submission {} event {} produced (topic={})",
                    submissionStatus,
                    event.getEventId(),
                    result.getRecordMetadata().topic()
                )
            )
            .exceptionally(ex -> {
                log.error(
                    "Failed to produce submission {} event {}",
                    submissionStatus,
                    event.getEventId(),
                    ex
                );
                return null;
            });
    }
}

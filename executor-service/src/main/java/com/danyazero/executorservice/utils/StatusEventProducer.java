package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.model.SubmissionStatus;
import com.danyazero.executorservice.model.SubmissionUpdatedEvent;
import com.danyazero.executorservice.model.SubmissionUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusEventProducer implements BiConsumer<Integer, SubmissionStatus> {
    private final KafkaTemplate<String, SubmissionUpdatedEvent> submissionKafkaTemplate;

    @Override
    public void accept(Integer submissionId, SubmissionStatus submissionStatus) {
        var event = new SubmissionUpdatedEvent(
                1,
                new SubmissionUpdated(submissionId, submissionStatus)
        );

        submissionKafkaTemplate.sendDefault(event)
                .thenAccept(result -> log.info(
                        "Submission {} event {} produced (topic={})",
                        submissionStatus,
                        event.getEventId(),
                        result.getRecordMetadata().topic()
                ))
                .exceptionally(ex -> {
                    log.error("Failed to produce submission {} event {}",
                            submissionStatus, event.getEventId(), ex);
                    return null;
                });
    }
}

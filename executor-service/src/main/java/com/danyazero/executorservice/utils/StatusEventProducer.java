package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.model.SubmissionStatus;
import com.danyazero.executorservice.model.SubmissionUpdated;
import com.danyazero.executorservice.model.SubmissionUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusEventProducer {

    private final KafkaTemplate<
        String,
        SubmissionUpdatedEvent
    > submissionKafkaTemplate;
    
    public void internalError(int problemId, int submissionId) {
        this.accept(problemId, submissionId, SubmissionStatus.INTERNAL_ERROR);
    }
    
    public void wrongAnswer(int problemId, int submissionId) {
        this.accept(problemId, submissionId, SubmissionStatus.WRONG_ANSWER);
    }
    
    public void accepted(int problemId, int submissionId) {
        this.accept(problemId, submissionId, SubmissionStatus.ACCEPTED);
    }
    
    public void compiled(int problemId, int submissionId) {
        this.accept(problemId, submissionId, SubmissionStatus.COMPILED);
    }
    
    public void compilationError(int problemId, int submissionId) {
        this.accept(problemId, submissionId, SubmissionStatus.COMPILATION_ERROR);
    }

    public void unsupportedLanguage(int problemId, int submissionId) {
        this.accept(problemId, submissionId, SubmissionStatus.UNSUPPORTED_LANGUAGE);
    }

    public void timeLimitExceeded(int problemId, int submissionId) {
        this.accept(problemId, submissionId, SubmissionStatus.TIME_LIMIT_EXCEEDED);
    }

    public void accept(
        Integer problemId,
        Integer submissionId,
        SubmissionStatus submissionStatus
    ) {
        var event = new SubmissionUpdatedEvent(
            1,
            new SubmissionUpdated(problemId, submissionId, submissionStatus)
        );

        submissionKafkaTemplate
            .sendDefault("submission-" + submissionId, event)
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

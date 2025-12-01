package com.danyazero.submissionservice.controller;

import com.danyazero.submissionservice.model.SubmissionUpdatedEvent;
import com.danyazero.submissionservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionConsumer {
    private final SubmissionService submissionService;

    @KafkaListener(topics = "submissions-execution", groupId = "submission-service", containerFactory = "submissionExecutionListenerContainerFactory")
    public void listen(SubmissionUpdatedEvent message) {
        log.info("Received submission execution event with id -> {}, data -> {}", message.getEventId(), message.getData());
        submissionService.updateSubmissionStatus(message.getData().submissionId(), message.getData().submissionStatus());
    }
}

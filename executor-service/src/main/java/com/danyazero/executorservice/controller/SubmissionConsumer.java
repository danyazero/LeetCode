package com.danyazero.executorservice.controller;

import com.danyazero.executorservice.model.SubmissionCreatedEvent;
import com.danyazero.executorservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionConsumer {
    private final SubmissionService submissionService;

    @KafkaListener(topics = "submissions", groupId = "executor-service", containerFactory = "submissionListenerContainerFactory")
    public void listen(SubmissionCreatedEvent message) {
        log.info("Received submission event id={}, data={}", message.getEventId(), message.getData());
        submissionService.processSubmission(message.getData());
    }

}

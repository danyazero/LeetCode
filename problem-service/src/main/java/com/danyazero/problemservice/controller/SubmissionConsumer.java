package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.model.SubmissionUpdatedEvent;
import com.danyazero.problemservice.service.ProblemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionConsumer {
    private final ProblemService problemService;

    @Transactional
    @KafkaListener(topics = "submissions-execution", groupId = "problem-service", containerFactory = "submissionExecutionListenerContainerFactory")
    public void listen(SubmissionUpdatedEvent message) {
        log.info("Received submission execution event with id -> {}, data -> {}", message.getEventId(), message.getData());
        var problemId = message.getData().problemId();

        switch (message.getData().submissionStatus()) {
            case COMPILED -> problemService.increaseSentSubmissions(problemId);
            case ACCEPTED -> problemService.increaseAcceptedSubmissions(problemId);
            default -> {}
        }
    }
}

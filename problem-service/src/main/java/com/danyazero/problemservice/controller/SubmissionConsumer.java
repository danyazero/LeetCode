package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.model.SubmissionUpdatedEvent;
import com.danyazero.problemservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionConsumer {
    private final ProblemRepository problemRepository;

    @Transactional
    @KafkaListener(topics = "submissions-execution", groupId = "problem-service", containerFactory = "submissionExecutionListenerContainerFactory")
    public void listen(SubmissionUpdatedEvent message) {
        log.info("Received submission execution event with id -> {}, data -> {}", message.getEventId(), message.getData());
        var problemId = message.getData().problemId();

        switch (message.getData().submissionStatus()) {
            case COMPILED -> increaseSentSubmissions(problemId);
            case ACCEPTED -> increaseAcceptedSubmissions(problemId);
            default -> {}
        }
    }

    public void increaseSentSubmissions(int problemId) {
        problemRepository.findById(problemId).ifPresentOrElse(
                problem -> {
                    problem.setSentSubmissions(problem.getSentSubmissions() + 1);
                    problemRepository.save(problem);
                },
                () -> log.warn("Can't increase 'sent submissions' counter, problem with id -> {} has not been found.", problemId)
        );
    }

    public void increaseAcceptedSubmissions(int problemId) {
        problemRepository.findById(problemId).ifPresentOrElse(
                problem -> {
                    problem.setAcceptedSubmissions(problem.getAcceptedSubmissions() + 1);
                    problemRepository.save(problem);
                },
                () -> log.warn("Can't increase 'accepted submissions' counter, problem with id -> {} has not been found.", problemId)
        );
    }
}

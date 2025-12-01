package com.danyazero.submissionservice.service;

import com.danyazero.submissionservice.client.ProblemClient;
import com.danyazero.submissionservice.entity.Event;
import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.exception.RequestException;
import com.danyazero.submissionservice.model.SubmissionCreatedEvent;
import com.danyazero.submissionservice.model.SubmissionDto;
import com.danyazero.submissionservice.model.SubmissionCreatedEventDto;
import com.danyazero.submissionservice.model.SubmissionStatus;
import com.danyazero.submissionservice.repository.EventRepository;
import com.danyazero.submissionservice.repository.LanguageRepository;
import com.danyazero.submissionservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final KafkaTemplate<String, SubmissionCreatedEvent> submissionKafkaTemplate;
    private final SubmissionRepository submissionRepository;
    private final LanguageRepository languageRepository;
    private final EventRepository eventRepository;
    private final ProblemClient problemClient;

    public Submission findBySubmissionId(int id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RequestException("Submission with id " + id + " not found."));
    }

    public Page<Submission> findByProblemId(int problemId, UUID userId, Pageable pageable) {
        return submissionRepository.findAllByProblemIdAndUserId(problemId, userId, pageable);
    }

    public void updateSubmissionStatus(
            Integer submissionId,
            SubmissionStatus submissionStatus
    ) {
        var submission = findBySubmissionId(submissionId);
        submission.setStatus(submissionStatus);
        submissionRepository.save(submission);

        var submissionEvent = Event.builder()
                .status(submissionStatus)
                .createdAt(Instant.now())
                .submission(submission)
                .build();
        eventRepository.save(submissionEvent);
    }

    @Transactional
    public Submission createSubmission(
            UUID userId,
            SubmissionDto submissionDto
    ) {
        var language = languageRepository.findById(submissionDto.languageId())
                .orElseThrow(() -> new RequestException("Language with id " + submissionDto.languageId() + " not found."));

        var submission = Submission.builder()
                .problemId(submissionDto.problemId())
                .solution(submissionDto.solution())
                .status(SubmissionStatus.CREATED)
                .createdAt(Instant.now())
                .language(language)
                .userId(userId)
                .build();

        var createdSubmission = submissionRepository.save(submission);
        log.info("Submission with id {} has been created.", createdSubmission.getId());

        var submissionEvent = Event.builder()
                .status(SubmissionStatus.CREATED)
                .submission(createdSubmission)
                .createdAt(Instant.now())
                .build();

        var problem = problemClient.getProblemById(submissionDto.problemId());

        var eventData = SubmissionCreatedEventDto.builder()
                .submissionId(createdSubmission.getId())
                .solution(submissionDto.solution())
                .language(language.getLanguage())
                .problemId(problem.id())
                .build();

        produceSubmissionCreatedEvent(eventData);

        createdSubmission.setEvents(
                Set.of(eventRepository.save(submissionEvent))
        );

        log.info("Submission CREATED event has been saved.");

        return createdSubmission;
    }

    private void produceSubmissionCreatedEvent(SubmissionCreatedEventDto eventData) {

        var submissionCreatedEvent = new SubmissionCreatedEvent(
                SubmissionStatus.CREATED.getValue(),
                1,
                eventData
        );
        submissionKafkaTemplate.sendDefault(
                submissionCreatedEvent
        ).thenAccept(res ->
                log.info(
                        "Submission CREATED event {} has been produced. (topic={})",
                        submissionCreatedEvent.getEventId(),
                        res.getRecordMetadata().topic()
                ));
    }
}

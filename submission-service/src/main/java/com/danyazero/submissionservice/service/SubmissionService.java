package com.danyazero.submissionservice.service;

import com.danyazero.submissionservice.entity.Event;
import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.exception.IllegalRequstArgumentException;
import com.danyazero.submissionservice.exception.RequestException;
import com.danyazero.submissionservice.mapper.SubmissionsPageMapper;
import com.danyazero.submissionservice.model.*;
import com.danyazero.submissionservice.repository.EventRepository;
import com.danyazero.submissionservice.repository.SubmissionRepository;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final KafkaTemplate<
        String,
        SubmissionCreatedEvent
    > submissionKafkaTemplate;
    private final SubmissionRepository submissionRepository;
    private final LanguageService languageService;
    private final EventRepository eventRepository;

    @Transactional
    public Submission createSubmission(
        UUID userId,
        SubmissionDto submissionPayload
    ) {
        log.info("Create submission request from user {}", userId);
        if (userId == null) {
            throw new IllegalRequstArgumentException(
                "Cannot create submission: provided user ID is null"
            );
        } else if (submissionPayload == null) {
            throw new IllegalRequstArgumentException(
                "Cannot create submission: provided payload is null"
            );
        }

        var submission = saveSubmission(userId, submissionPayload);
        var event = saveSubmissionEvent(submission);

        submission.setEvents(Set.of(event));

        produceEvent(createSubmissionEvent(submission, submissionPayload));

        log.info(
            "Submission with id {} has been created successfully.",
            submission.getId()
        );

        return submission;
    }

    public ProblemStatus getProblemStatus(UUID userId, Integer problemId) {
        if (userId == null) {
            throw new IllegalRequstArgumentException("Cannot provide problem status: User ID is null");
        } else if (problemId == null) {
            throw new IllegalRequstArgumentException("Cannot provide problem status: Problem ID is null");
        }

        var isSolved = submissionRepository
            .findFirstByUserIdIsAndProblemIdAndStatus(userId, problemId, SubmissionStatus.ACCEPTED)
            .isPresent();

        return new ProblemStatus(isSolved);
    }

    public Submission getSubmission(Integer submissionId) {
        if (submissionId == null) throw new IllegalRequstArgumentException(
            "Cannot provide submission: submission ID is null"
        );
        return submissionRepository
            .findById(submissionId)
            .orElseThrow(() ->
                new RequestException(
                    "Cannot provide submission: submission with id " +
                        submissionId +
                        " not found."
                )
            );
    }

    public PageDto<SubmissionResponseDto> getSubmissions(
        int problemId,
        UUID userId,
        Pageable pageable
    ) {
        if (userId == null) {
            throw new IllegalRequstArgumentException("Cannot provide submissions list: user ID is null.");
        } else if (pageable == null) {
            throw new IllegalRequstArgumentException("Cannot provide submissions list: page parameters are null.");
        }
        
        return SubmissionsPageMapper.map(
            submissionRepository.findAllByProblemIdAndUserIdOrderByIdDesc(
                problemId,
                userId,
                pageable
            )
        );
    }

    @Transactional
    public void updateSubmissionStatus(
        Integer submissionId,
        SubmissionStatus submissionStatus
    ) {
        if (submissionStatus == null) {
            log.warn("Cannot update submission status: submission status is null (submissionId: {})", submissionId);
            throw new RuntimeException("Cannot update submission status: submission status is null");
        }
        
        var submission = getSubmission(submissionId);
        submission.setStatus(submissionStatus);
        submissionRepository.save(submission);

        var submissionEvent = Event.builder()
            .status(submissionStatus)
            .createdAt(Instant.now())
            .submission(submission)
            .build();

        eventRepository.save(submissionEvent);
    }

    private Event saveSubmissionEvent(Submission submission) {
        var event = Event.builder()
            .status(SubmissionStatus.CREATED)
            .submission(submission)
            .createdAt(Instant.now())
            .build();

        return eventRepository.save(event);
    }

    private SubmissionCreatedEvent createSubmissionEvent(
        Submission submission,
        SubmissionDto payload
    ) {
        var data = SubmissionCreatedEventDto.builder()
            .language(submission.getLanguage().getLanguage())
            .submissionId(submission.getId())
            .problemId(payload.problemId())
            .solution(payload.solution())
            .build();

        return new SubmissionCreatedEvent(
            SubmissionStatus.CREATED.getValue(),
            1,
            data
        );
    }

    private void produceEvent(SubmissionCreatedEvent event) {
        try {
            var res = submissionKafkaTemplate.sendDefault(event).get();
            log.info(
                "Submission {} event {} has been produced. (topic={})",
                event.getEventType(),
                event.getEventId(),
                res.getRecordMetadata().topic()
            );
        } catch (Exception e) {
            log.error("Failed to produce event", e);
            throw new RequestException("Failed to produce submission event: " + e.getMessage());
        }
    }

    private Submission saveSubmission(
        UUID userId,
        SubmissionDto submissionPayload
    ) {
        var language = languageService.getLanguage(
            submissionPayload.languageId()
        );

        var submission = Submission.builder()
            .problemId(submissionPayload.problemId())
            .solution(submissionPayload.solution())
            .status(SubmissionStatus.CREATED)
            .createdAt(Instant.now())
            .language(language)
            .userId(userId)
            .build();

        return submissionRepository.save(submission);
    }
}

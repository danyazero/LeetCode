package com.danyazero.submissionservice.service;

import com.danyazero.submissionservice.entity.Event;
import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.exception.RequestException;
import com.danyazero.submissionservice.model.PageDto;
import com.danyazero.submissionservice.model.SubmissionDto;
import com.danyazero.submissionservice.model.SubmissionStatus;
import com.danyazero.submissionservice.repository.EventRepository;
import com.danyazero.submissionservice.repository.LanguageRepository;
import com.danyazero.submissionservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final LanguageRepository languageRepository;
    private final EventRepository eventRepository;

    public Submission findBySubmissionId(int id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RequestException("Submission with id " + id + " not found."));
    }

    public Page<Submission> findByProblemId(int problemId, UUID userId, Pageable pageable) {

        return submissionRepository.findAllByProblemIdAndUserId(problemId, userId, pageable);
    }

    @Transactional
    public Submission createSubmission(UUID userId, SubmissionDto submissionDto) {
        var language = languageRepository.findById(submissionDto.languageId())
                .orElseThrow(() -> new RequestException("Language with id " + submissionDto.languageId() + " not found."));

        var submission = Submission.builder()
                .solutionPath("/some/generated/solution.txt")
                .problemId(submissionDto.problemId())
                .status(SubmissionStatus.CREATED)
                .createdAt(Instant.now())
                .language(language)
                .userId(userId)
                .build();



        var createdSubmission = submissionRepository.save(submission);

        var submissionEvent = Event.builder()
                .submission(createdSubmission)
                .status(SubmissionStatus.CREATED)
                .createdAt(Instant.now())
                .build();

        createdSubmission.setEvents(
                Set.of(eventRepository.save(submissionEvent))
        );

        return createdSubmission;
    }
}

package com.danyazero.submissionservice.service;

import com.danyazero.submissionservice.entity.Event;
import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.exception.RequestException;
import com.danyazero.submissionservice.model.SubmissionDto;
import com.danyazero.submissionservice.model.SubmissionStatus;
import com.danyazero.submissionservice.repository.EventRepository;
import com.danyazero.submissionservice.repository.LanguageRepository;
import com.danyazero.submissionservice.repository.SubmissionRepository;
import io.minio.GetObjectResponse;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SolutionStorageService solutionStorageService;
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
    public Submission createSubmission(
            UUID idempotencyKey,
            UUID userId,
            SubmissionDto submissionDto
    ) {
        var language = languageRepository.findById(submissionDto.languageId())
                .orElseThrow(() -> new RequestException("Language with id " + submissionDto.languageId() + " not found."));

        var submissionExist = submissionRepository.findFirstByUserIdAndIdempotencyKey(userId, idempotencyKey);
        if (submissionExist.isPresent()) {
            log.info("Submission with id {} already exist.", submissionExist.get().getId());
            return submissionExist.get();
        }

        var submission = Submission.builder()
                .problemId(submissionDto.problemId())
                .status(SubmissionStatus.CREATED)
                .idempotencyKey(idempotencyKey)
                .createdAt(Instant.now())
                .language(language)
                .userId(userId)
                .build();

        var createdSubmission = submissionRepository.save(submission);

        var solutionPath = saveUserSolution(
                createdSubmission.getId(),
                userId,
                submissionDto.solution()
        );
        createdSubmission.setSolutionPath(solutionPath);
        submissionRepository.save(createdSubmission);

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

    public GetObjectResponse getSolutionByFilename(String filename) {
        try {
            return solutionStorageService.getSolution(filename);
        } catch (Exception e) {
            throw new RequestException("An error occurred while trying to get solution by filename");
        }
    }

    private String saveUserSolution(int id, UUID userId, String solution) {
        var solutionStream = new ByteArrayInputStream(solution.getBytes());
        var filename = getSolutionFilename(id, userId);
        try {
            solutionStorageService.uploadSolution(filename + ".txt", solutionStream);

            return filename;
        } catch (Exception e) {
            throw new RequestException("An error occurred, while saving user solution.");
        }
    }

    private static String getSolutionFilename(Integer id, UUID userId) {
        var content = UUID.nameUUIDFromBytes(id.toString().getBytes(StandardCharsets.UTF_8)) + "_" + userId.toString();

        return Base64.getUrlEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }
}

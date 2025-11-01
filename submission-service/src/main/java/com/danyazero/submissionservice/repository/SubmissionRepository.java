package com.danyazero.submissionservice.repository;

import com.danyazero.submissionservice.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    Page<Submission> findAllByProblemIdAndUserId(Integer problemId, UUID userId, Pageable pageable);
}

package com.danyazero.submissionservice.repository;

import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.model.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    Page<Submission> findAllByProblemIdAndUserIdOrderByIdDesc(Integer problemId, UUID userId, Pageable pageable);

    @Query("select count(s.id) from Submission s where s.problemId = ?1")
    int getSubmissionsCount(Integer problemId);

    @Query("select count(s.id) from Submission s where s.problemId = ?1 and s.status = ?2")
    int getSubmissionsCountByCurrentType(Integer problemId, SubmissionStatus submissionStatus);

    boolean existsSubmissionByUserIdAndStatus(UUID userId, SubmissionStatus status);
}

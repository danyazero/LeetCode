package com.danyazero.submissionservice.model;

import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.entity.Submission;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SubmissionDto(
        Integer problemId,
        Integer languageId,
        String solution
) {
}

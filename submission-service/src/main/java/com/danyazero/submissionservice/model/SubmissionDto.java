package com.danyazero.submissionservice.model;

import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.entity.Submission;

import java.util.UUID;

public record SubmissionDto(
        Integer problemId,
        Integer languageId,
        String solution
) {
}

package com.danyazero.submissionservice.model;

import lombok.Builder;

@Builder
public record SubmissionDto(
    Integer problemId,
    Integer languageId,
    String solution
) {}

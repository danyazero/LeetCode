package com.danyazero.submissionservice.model;

import java.time.Instant;
import lombok.Builder;

@Builder
public record SubmissionResponseDto(
    Integer submissionId,
    String language,
    SubmissionStatus status,
    Instant createdAt
) {}

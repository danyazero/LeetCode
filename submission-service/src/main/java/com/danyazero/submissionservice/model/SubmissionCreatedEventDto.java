package com.danyazero.submissionservice.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SubmissionCreatedEventDto(
        UUID userId,
        String language,
        Integer problemId,
        Integer submissionId,
        String solution
) {
}

package com.danyazero.submissionservice.model;

import lombok.Builder;

@Builder
public record SubmissionCreatedEventDto(
        String language,
        Integer problemId,
        Integer submissionId,
        String solution
) {
}

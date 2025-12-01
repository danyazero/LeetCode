package com.danyazero.executorservice.model;

import lombok.Builder;

@Builder
public record SubmissionCreatedEventDto(
        String language,
        Integer problemId,
        Integer submissionId,
        String solution
) {
}

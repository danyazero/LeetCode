package com.danyazero.executorservice.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SubmissionUpdatedEventPayload(
        UUID userId,
        Integer problemId,
        Integer submissionId
) { }

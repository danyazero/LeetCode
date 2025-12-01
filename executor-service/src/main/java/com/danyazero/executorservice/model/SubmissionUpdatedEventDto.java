package com.danyazero.executorservice.model;

import java.time.Instant;

public record SubmissionUpdatedEventDto(
        Integer submissionId,
        SubmissionStatus submissionStatus
) {
}

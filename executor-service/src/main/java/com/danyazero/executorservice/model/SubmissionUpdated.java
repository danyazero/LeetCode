package com.danyazero.executorservice.model;

public record SubmissionUpdated(
        Integer submissionId,
        SubmissionStatus submissionStatus
) {
}

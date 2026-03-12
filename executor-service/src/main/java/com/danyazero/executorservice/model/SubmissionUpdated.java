package com.danyazero.executorservice.model;

public record SubmissionUpdated(
        Integer problemId,
        Integer submissionId,
        SubmissionStatus submissionStatus
) {
}

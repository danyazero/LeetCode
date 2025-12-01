package com.danyazero.submissionservice.model;

public record SubmissionUpdatedEventDto(
        Integer submissionId,
        SubmissionStatus submissionStatus
) { }

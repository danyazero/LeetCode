package com.danyazero.problemservice.model;

public record SubmissionUpdatedEventDto(
        Integer problemId,
        Integer submissionId,
        SubmissionStatus submissionStatus
) { }

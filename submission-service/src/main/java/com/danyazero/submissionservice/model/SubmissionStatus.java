package com.danyazero.submissionservice.model;

public enum SubmissionStatus {
    CREATED,
    QUEUED,
    RUNNING,
    COMPILATION_ERROR,
    COMPILED,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    WRONG_ANSWER,
    PARTIALLY_CORRECT,
    ACCEPTED,
    INTERNAL_ERROR,
    CANCELED
}

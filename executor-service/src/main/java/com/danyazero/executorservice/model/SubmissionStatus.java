package com.danyazero.executorservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmissionStatus {
    CREATED("created"),
    QUEUED("queued"),
    RUNNING("running"),
    UNSUPPORTED_LANGUAGE("running"),
    COMPILATION_ERROR("compilationError"),
    COMPILED("compiled"),
    TIME_LIMIT_EXCEEDED("timeLimitExceeded"),
    MEMORY_LIMIT_EXCEEDED("memoryLimitExceeded"),
    WRONG_ANSWER("wrongAnswer"),
    PARTIALLY_CORRECT("partiallyCorrect"),
    ACCEPTED("accepted"),
    INTERNAL_ERROR("internalError"),
    CANCELLED("cancelled"),;

    private final String value;
}

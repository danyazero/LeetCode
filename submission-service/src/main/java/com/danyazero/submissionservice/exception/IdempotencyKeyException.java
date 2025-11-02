package com.danyazero.submissionservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IdempotencyKeyException extends RequestException {
    public IdempotencyKeyException(String message) {
        super(message);
    }
}

package com.danyazero.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RequestException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

package com.danyazero.userservice.exception;

public class CorruptedTokenException extends RequestException {
    public CorruptedTokenException(String message) {
        super(message);
    }
}

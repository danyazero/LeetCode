package com.danyazero.executorservice.error;

public class ProcessExecutionException extends RuntimeException {
    public ProcessExecutionException(String message) {
        super(message);
    }
}

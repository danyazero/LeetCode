package com.danyazero.executorservice.model;

public sealed interface ExecutionResult {
    record Success(String output) implements ExecutionResult {}
    record Failure(String message) implements ExecutionResult {}
    record Timeout() implements ExecutionResult {}
}

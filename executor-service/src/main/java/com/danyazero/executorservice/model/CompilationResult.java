package com.danyazero.executorservice.model;

public sealed interface CompilationResult {
    record Success(CompiledProgram program) implements CompilationResult {}
    record Failure(String message) implements CompilationResult {}
}

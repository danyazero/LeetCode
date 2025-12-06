package com.danyazero.executorservice.model;

import com.danyazero.executorservice.utils.ProcessExecutor;

public sealed interface CompilationResult {
    record Success(CompiledProgram program) implements CompilationResult {}
    record Failure(String message) implements CompilationResult {}
}

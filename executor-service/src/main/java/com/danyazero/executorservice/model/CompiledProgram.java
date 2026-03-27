package com.danyazero.executorservice.model;

import java.util.List;

public interface CompiledProgram {
    ExecutionResult execute(List<String> params);
    ExecutionResult execute(List<String> params, int timeLimit);
    void cleanup();
}

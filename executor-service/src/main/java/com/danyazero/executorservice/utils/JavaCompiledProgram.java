package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.model.CompiledProgram;
import com.danyazero.executorservice.model.ExecutionResult;

import java.util.ArrayList;
import java.util.List;

public record JavaCompiledProgram(ProcessExecutor processExecutor) implements CompiledProgram {
    @Override
    public ExecutionResult execute(List<String> params) {
        return this.execute(params, 5);
    }

    @Override
    public ExecutionResult execute(List<String> params, int timeLimit) {
        var processCommand = new ArrayList<>(List.of("/usr/local/openjdk/bin/java", "Main"));
        processCommand.addAll(params);

        return processExecutor.execute(processCommand, timeLimit);
    }

    @Override
    public void cleanup() {
        SandboxManager.cleanup(processExecutor.getWorkingDirectory());
    }
}

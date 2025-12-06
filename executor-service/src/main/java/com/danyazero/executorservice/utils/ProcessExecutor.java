package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.error.ProcessExecutionException;
import com.danyazero.executorservice.model.ExecutionResult;
import com.danyazero.executorservice.model.ProcessConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public record ProcessExecutor(ProcessConfig processConfig) {
    public ExecutionResult execute(List<String> command, int timeLimit) {
        try {
            final var compilationProcess = this.executeProcess(command, timeLimit);
            final var isFinished = compilationProcess.waitFor(5, TimeUnit.SECONDS);

            if (!isFinished) {
                return new ExecutionResult.Timeout();
            }
            final var stdout = read(compilationProcess.getInputStream());

            if (compilationProcess.exitValue() != 0) {
                final var stderr = read(compilationProcess.getErrorStream());

                return new ExecutionResult.Failure(stderr.isBlank() ? stdout : stderr);
            }

            return new ExecutionResult.Success(stdout);
        } catch (Exception e) {
            return new ExecutionResult.Failure("Exception: " + e.getMessage());
        }
    }

    private Process executeProcess(List<String> command, int timeLimit) {
        var process = new ProcessBuilder(
                "nsjail",
                "--user", "nobody",
                "--group", "nogroup"
        );

        process.command().addAll(List.of("--chroot", processConfig.getWorkingDirectory().toString()));

        process.command().addAll(processConfig.getConfig());

        process.command().add("--time_limit");
        process.command().add(timeLimit + "");

        process.command().add("--");
        process.command().addAll(command);

        try {
            return process.start();
        } catch (IOException e) {
            throw new ProcessExecutionException("An error occurred while starting process execution.");
        }
    }

    private String read(InputStream is) throws IOException {
        return new String(is.readAllBytes());
    }

    public Path getWorkingDirectory() {
        return processConfig.getWorkingDirectory();
    }
}

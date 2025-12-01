package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.error.ProcessExecutionException;
import com.danyazero.executorservice.model.ProcessConfig;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class ProcessExecutor {
    private final ProcessConfig processConfig;

    public Process execute(Path processRoot, List<String> command, int timeLimit) {
        var process = new ProcessBuilder(
                "nsjail",
                "--user", "nobody",
                "--group", "nogroup"
        );

        if (processRoot != null) process.command().addAll(List.of("--chroot", processRoot.toString()));

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
}

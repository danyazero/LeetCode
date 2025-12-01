package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.model.ProcessConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JavaCodeCompiler {
    private final Path sandbox;
    private final ProcessExecutor processExecutor;

    private static final String JDK_PATH = "/usr/local/openjdk-26";

    public JavaCodeCompiler() throws IOException {
        this.sandbox = Files.createTempDirectory("sandbox_");

        var processConfig = javaDefaultProcessConfig();
        this.processExecutor = new ProcessExecutor(processConfig);
    }

    public String compile(String code) throws IOException, InterruptedException {
        var solution = "Main.java";
        Path solutionFile = sandbox.resolve(solution);
        Files.writeString(solutionFile, code);

        var compilationProcess = processExecutor.execute(sandbox, List.of("/usr/local/openjdk/bin/javac", solution), 5);
        compilationProcess.waitFor(10, TimeUnit.SECONDS);

        return new String(compilationProcess.getErrorStream().readAllBytes()).trim();
    }

    public String executeWithParams(List<String> params, int timeLimit) throws InterruptedException, IOException {

        var processCommand = new ArrayList<>(List.of("/usr/local/openjdk/bin/java", "Main"));
        processCommand.addAll(params);

        var executeProcess = processExecutor.execute(sandbox, processCommand, timeLimit);
        executeProcess.waitFor(10, TimeUnit.SECONDS);

        return new String(executeProcess.getInputStream().readAllBytes()).trim();
    }

    private ProcessConfig javaDefaultProcessConfig() {
        var roJavaMounts = List.of(
                "/lib:/lib",
                JavaCodeCompiler.JDK_PATH + ":/usr/local/openjdk",
                JavaCodeCompiler.JDK_PATH + "/lib/libjli.so:/lib/libjli.so"
        );

        var rwJavaMounts = List.of(
                sandbox + ":/"
        );

        var javaEnv = List.of(
                "JAVA_TOOL_OPTIONS=-Xmx64m",
                "-XX:ReservedCodeCacheSize=16m",
                "-XX:CompressedClassSpaceSize=16m"
        );

        return ProcessConfig.builder()
                .mounts_ro(roJavaMounts)
                .mounts_rw(rwJavaMounts)
                .asLimit(2048)
                .env(javaEnv)
                .cpuLimit(2)
                .build();
    }
}

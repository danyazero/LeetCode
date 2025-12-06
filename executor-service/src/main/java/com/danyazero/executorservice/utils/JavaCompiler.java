package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.model.CompilationResult;
import com.danyazero.executorservice.model.ExecutionResult;
import com.danyazero.executorservice.model.Compiler;
import com.danyazero.executorservice.model.ProcessConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
public class JavaCompiler implements Compiler {

    private static final String JDK_PATH = "/usr/local/openjdk-26";

    @Override
    public String getLanguage() {
        return "java";
    }

    @Override
    public CompilationResult compile(String code) {
        try {
            final var workingDirectory = SandboxManager.createSandbox();
            final var processConfig = javaDefaultProcessConfig(workingDirectory);
            final var processExecutor = new ProcessExecutor(processConfig);

            final var solution = "Main.java";
            Files.writeString(workingDirectory.resolve(solution), code);

            final var compilationResult = processExecutor.execute(List.of("/usr/local/openjdk/bin/javac", solution), 5);

            return switch (compilationResult) {
                case ExecutionResult.Timeout ignored -> new CompilationResult.Failure("Compilation timeout");
                case ExecutionResult.Success ignored -> new CompilationResult.Success(new JavaCompiledProgram(processExecutor));
                case ExecutionResult.Failure ignored ->  new CompilationResult.Failure(compilationResult.toString());
            };
        } catch (Exception e) {
            return new CompilationResult.Failure("Exception: " + e.getMessage());
        }
    }

    private ProcessConfig javaDefaultProcessConfig(Path workingDirectory) {
        var roJavaMounts = List.of(
                "/lib:/lib",
                JavaCompiler.JDK_PATH + ":/usr/local/openjdk",
                JavaCompiler.JDK_PATH + "/lib/libjli.so:/lib/libjli.so"
        );

        var rwJavaMounts = List.of(
                workingDirectory.toString() + ":/"
        );

        var javaEnv = List.of(
                "JAVA_TOOL_OPTIONS=-Xmx64m",
                "-XX:ReservedCodeCacheSize=16m",
                "-XX:CompressedClassSpaceSize=16m"
        );

        return ProcessConfig.builder()
                .workingDirectory(workingDirectory)
                .mounts_ro(roJavaMounts)
                .mounts_rw(rwJavaMounts)
                .asLimit(2048)
                .env(javaEnv)
                .cpuLimit(2)
                .build();
    }

}

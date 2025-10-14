package com.danyazero.executorservice.utils;

import com.danyazero.executorservice.generator.JavaSyntaxGenerator;
import com.danyazero.executorservice.model.MethodSchema;
import com.danyazero.executorservice.model.ProcessConfig;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class JavaCodeExecutor implements BiFunction<MethodSchema, String, String> {
    @SneakyThrows
    @Override
    public String apply(MethodSchema methodSchema, String code) {
        Path sandbox = Files.createTempDirectory("sandbox_");

        var startPointGenerator = new StartPointGenerator();
        var syntaxGenerator = new JavaSyntaxGenerator();
        var generatedStartPoint = startPointGenerator.apply(methodSchema, syntaxGenerator);

        var main = "Main.java";
        Path mainFile = sandbox.resolve(main);
        Files.writeString(mainFile, generatedStartPoint);

        var solution = "Solution.java";
        Path solutionFile = sandbox.resolve(solution);
        Files.writeString(solutionFile, code);

        var roJavaMounts = List.of(
                "/lib:/lib",
                "/usr/local/openjdk-24:/usr/local/openjdk-24",
                "/usr/local/openjdk-24/lib/libjli.so:/lib/libjli.so"
        );

        var rwJavaMounts = List.of(
                sandbox + ":/"
        );

        var javaEnv = List.of(
                "JAVA_TOOL_OPTIONS=-Xmx64m",
                "-XX:ReservedCodeCacheSize=16m",
                "-XX:CompressedClassSpaceSize=16m"
        );

        var processConfig = ProcessConfig.builder()
                .mounts_ro(roJavaMounts)
                .mounts_rw(rwJavaMounts)
                .asLimit(2048)
                .timeLimit(5)
                .env(javaEnv)
                .cpuLimit(2)
                .build();

        var processExecutor = new ProcessExecutor(processConfig);

        var compilationProcess = processExecutor.apply(sandbox, List.of("/usr/local/openjdk-24/bin/javac", main, solution));
        printProcessOutput(compilationProcess);
        compilationProcess.waitFor(10, TimeUnit.SECONDS);


        var executeProcess = processExecutor.apply(sandbox, List.of("/usr/local/openjdk-24/bin/java", "Main", "12,12,1,25"));
        executeProcess.waitFor(10, TimeUnit.SECONDS);

        return new String(executeProcess.getInputStream().readAllBytes()).trim();
    }

    private static void printProcessOutput(Process process) throws IOException {
        String output = new String(process.getInputStream().readAllBytes()).trim();
        System.out.println(output);

        System.out.println();

        String outputErr = new String(process.getErrorStream().readAllBytes()).trim();
        System.out.println(outputErr);
    }
}

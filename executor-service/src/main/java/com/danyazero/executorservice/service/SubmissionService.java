package com.danyazero.executorservice.service;

import com.danyazero.executorservice.client.ProblemClient;
import com.danyazero.executorservice.model.*;
import com.danyazero.executorservice.utils.StatusEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final ProblemClient problemClient;
    private final CompilerService compilerService;
    private final StatusEventProducer statusEventProducer;

    public void processSubmission(SubmissionCreated submission) {
        compilerService.getCompiler(submission.language()).ifPresentOrElse(
                compiler -> processSubmission(submission, compiler),
                () -> {
                    log.info("Compiler for language - '{}' not found", submission.language());
                    statusEventProducer.accept(submission.id(), SubmissionStatus.UNSUPPORTED_LANGUAGE);
                }
        );
    }

    public void processSubmission(SubmissionCreated submission, Compiler executor) {
        final var compiledProgram = compileSolution(executor, submission);
        if (compiledProgram.isEmpty()) return;

        final var testcases = problemClient.getProblemTestcases(submission.problemId());
        for (var testcase : testcases) {
            final var output = runTestcase(compiledProgram.get(), testcase.input(), submission.id());
            if (isNotEqualsExpected(output, testcase.expected())) {
                log.info("Wrong answer for submission {} -> {}, expected -> {}",
                        submission.id(), output, testcase.expected());

                statusEventProducer.accept(submission.id(), SubmissionStatus.WRONG_ANSWER);
                compiledProgram.get().cleanup();
                return;
            }
        }
        compiledProgram.get().cleanup();
        statusEventProducer.accept(submission.id(), SubmissionStatus.ACCEPTED);
    }

    private Optional<CompiledProgram> compileSolution(Compiler compiler, SubmissionCreated submission) {
        return switch (compiler.compile(submission.solution())) {
            case CompilationResult.Failure error -> {
                statusEventProducer.accept(submission.id(), SubmissionStatus.COMPILATION_ERROR);
                log.info("Compilation failed for submission - {}, with error: {}", submission.id(), error.message());

                yield Optional.empty();
            }
            case CompilationResult.Success result -> {
                statusEventProducer.accept(submission.id(), SubmissionStatus.COMPILED);
                log.info("Compilation completed successfully");

                yield Optional.of(result.program());
            }
        };
    }

    private Optional<String> runTestcase(CompiledProgram compiledProgram, String input, int submissionId) {

        return switch (compiledProgram.execute(List.of(input.split(", ")), 2)) {
            case ExecutionResult.Failure error -> {
                log.info("Execution error for submission {}: {}", submissionId, error.message());
                statusEventProducer.accept(submissionId, SubmissionStatus.INTERNAL_ERROR);

                yield Optional.empty();
            }
            case ExecutionResult.Timeout ignored -> {
                log.info("Time limit exceeded for submission {}", submissionId);
                statusEventProducer.accept(submissionId, SubmissionStatus.TIME_LIMIT_EXCEEDED);

                yield Optional.empty();
            }
            case ExecutionResult.Success result -> {
                final var output = formatExecutionResult(result.output());
                log.info("Submission {} testcase result: {}", submissionId, result.output());

                yield Optional.of(output);
            }
        };
    }

    private String formatExecutionResult(String output) {
        return String.join(", ",
                output
                        .lines()
                        .map(String::trim)
                        .toList());
    }

    private static boolean isNotEqualsExpected(Optional<String> output, String expected) {
        return output.isPresent() && !output.get().equals(expected);
    }
}

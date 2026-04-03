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
                    statusEventProducer.unsupportedLanguage(new SubmissionUpdatedEventPayload(submission.userId(), submission.problemId(), submission.id()));
                }
        );
    }

    public void processSubmission(SubmissionCreated submission, Compiler executor) {
        if (submission == null) {
            log.warn("Received null submission");
            return;
        }

        var eventPayload = SubmissionUpdatedEventPayload.builder()
                .submissionId(submission.id())
                .problemId(submission.problemId())
                .userId(submission.userId())
                .build();

        final var compiledProgramOpt = compileSolution(executor, submission.solution(), eventPayload);
        if (compiledProgramOpt.isEmpty()) return;

        CompiledProgram compiledProgram = compiledProgramOpt.get();

        try {
            final var testcases = problemClient.getProblemTestcases(submission.problemId());
            if (testcases == null || testcases.isEmpty()) {
                log.warn("No testcases found for problem {}", submission.problemId());
                statusEventProducer.cancelled(eventPayload);
                return;
            }

            statusEventProducer.running(eventPayload);

            for (var testcase : testcases) {
                final var outputOpt = runTestcase(compiledProgram, testcase.input(), eventPayload);

                if (outputOpt.isEmpty()) {
                    return;
                }

                if (isNotEqualsExpected(outputOpt.get(), testcase.expected())) {
                    log.info("Wrong answer for submission {} -> {}, expected -> {}",
                            submission.id(), outputOpt.get(), testcase.expected());

                    statusEventProducer.wrongAnswer(eventPayload);
                    return;
                }
            }
            statusEventProducer.accepted(eventPayload);
        } catch (Exception e) {
            log.error("Error processing testcases for submission {}", submission.id(), e);
            statusEventProducer.internalError(eventPayload);
        } finally {
            compiledProgram.cleanup();
        }
    }

    private Optional<CompiledProgram> compileSolution(Compiler compiler, String solution, SubmissionUpdatedEventPayload eventPayload) {
        return switch (compiler.compile(solution)) {
            case CompilationResult.Failure error -> {
                statusEventProducer.compilationError(eventPayload);
                log.info("Compilation failed for submission - {}, with error: {}", eventPayload.submissionId(), error.message());

                yield Optional.empty();
            }
            case CompilationResult.Success result -> {
                statusEventProducer.compiled(eventPayload);
                log.info("Compilation completed successfully");

                yield Optional.of(result.program());
            }
        };
    }

    private Optional<String> runTestcase(CompiledProgram compiledProgram, String input, SubmissionUpdatedEventPayload eventPayload) {
        List<String> params = input != null && !input.isBlank() ? List.of(input.split(", ")) : List.of();

        return switch (compiledProgram.execute(params, 2)) {
            case ExecutionResult.Failure error -> {
                log.info("Execution error for submission {}: {}", eventPayload.submissionId(), error.message());
                statusEventProducer.internalError(eventPayload);

                yield Optional.empty();
            }
            case ExecutionResult.Timeout ignored -> {
                log.info("Time limit exceeded for submission {}", eventPayload.submissionId());
                statusEventProducer.timeLimitExceeded(eventPayload);

                yield Optional.empty();
            }
            case ExecutionResult.Success result -> {
                final var output = result.output() != null ? formatExecutionResult(result.output()) : "";
                log.info("Submission {} testcase result: {}", eventPayload.submissionId(), output);

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

    private static boolean isNotEqualsExpected(String output, String expected) {
        if (output == null) return expected != null;
        return !output.equals(expected);
    }
}

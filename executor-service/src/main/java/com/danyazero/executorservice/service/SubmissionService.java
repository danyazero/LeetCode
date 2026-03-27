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
                    statusEventProducer.unsupportedLanguage(submission.problemId(), submission.id());
                }
        );
    }

    public void processSubmission(SubmissionCreated submission, Compiler executor) {
        if (submission == null) {
            log.warn("Received null submission");
            return;
        }

        final var compiledProgramOpt = compileSolution(executor, submission);
        if (compiledProgramOpt.isEmpty()) return;

        CompiledProgram compiledProgram = compiledProgramOpt.get();

        try {
            final var testcases = problemClient.getProblemTestcases(submission.problemId());
            if (testcases == null || testcases.isEmpty()) {
                log.warn("No testcases found for problem {}", submission.problemId());
                statusEventProducer.internalError(submission.problemId(), submission.id());
                return;
            }

            for (var testcase : testcases) {
                final var outputOpt = runTestcase(compiledProgram, testcase.input(), submission.problemId(), submission.id());
                
                if (outputOpt.isEmpty()) {
                    return; 
                }

                if (isNotEqualsExpected(outputOpt.get(), testcase.expected())) {
                    log.info("Wrong answer for submission {} -> {}, expected -> {}",
                            submission.id(), outputOpt.get(), testcase.expected());

                    statusEventProducer.wrongAnswer(submission.problemId(), submission.id());
                    return;
                }
            }
            statusEventProducer.accepted(submission.problemId(), submission.id());
        } catch (Exception e) {
            log.error("Error processing testcases for submission {}", submission.id(), e);
            statusEventProducer.internalError(submission.problemId(), submission.id());
        } finally {
            compiledProgram.cleanup();
        }
    }

    private Optional<CompiledProgram> compileSolution(Compiler compiler, SubmissionCreated submission) {
        return switch (compiler.compile(submission.solution())) {
            case CompilationResult.Failure error -> {
                statusEventProducer.compilationError(submission.problemId(), submission.id());
                log.info("Compilation failed for submission - {}, with error: {}", submission.id(), error.message());

                yield Optional.empty();
            }
            case CompilationResult.Success result -> {
                statusEventProducer.compiled(submission.problemId(), submission.id());
                log.info("Compilation completed successfully");

                yield Optional.of(result.program());
            }
        };
    }

    private Optional<String> runTestcase(CompiledProgram compiledProgram, String input, int problemId, int submissionId) {
        List<String> params = input != null && !input.isBlank() ? List.of(input.split(", ")) : List.of();
        
        return switch (compiledProgram.execute(params, 2)) {
            case ExecutionResult.Failure error -> {
                log.info("Execution error for submission {}: {}", submissionId, error.message());
                statusEventProducer.internalError(problemId, submissionId);

                yield Optional.empty();
            }
            case ExecutionResult.Timeout ignored -> {
                log.info("Time limit exceeded for submission {}", submissionId);
                statusEventProducer.timeLimitExceeded(problemId, submissionId);

                yield Optional.empty();
            }
            case ExecutionResult.Success result -> {
                final var output = result.output() != null ? formatExecutionResult(result.output()) : "";
                log.info("Submission {} testcase result: {}", submissionId, output);

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

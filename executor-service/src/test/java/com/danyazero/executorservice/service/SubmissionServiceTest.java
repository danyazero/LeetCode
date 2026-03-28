package com.danyazero.executorservice.service;

import com.danyazero.executorservice.client.ProblemClient;
import com.danyazero.executorservice.model.*;
import com.danyazero.executorservice.model.Compiler;
import com.danyazero.executorservice.utils.StatusEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SubmissionServiceTest {

    private ProblemClient problemClient;
    private CompilerService compilerService;
    private StatusEventProducer statusEventProducer;
    private SubmissionService submissionService;

    @BeforeEach
    void setUp() {
        problemClient = mock(ProblemClient.class);
        compilerService = mock(CompilerService.class);
        statusEventProducer = mock(StatusEventProducer.class);
        submissionService = new SubmissionService(problemClient, compilerService, statusEventProducer);
    }

    private SubmissionCreated defaultSubmission() {
        return SubmissionCreated.builder()
                .id(100)
                .problemId(10)
                .language("java")
                .solution("print('hello')")
                .build();
    }

    @Nested
    @DisplayName("processSubmission(SubmissionCreated)")
    class ProcessSubmissionPublicTests {

        @Test
        @DisplayName("should emit UNSUPPORTED_LANGUAGE when compiler is not found")
        void shouldEmitUnsupportedLanguage() {
            when(compilerService.getCompiler("java")).thenReturn(Optional.empty());

            submissionService.processSubmission(defaultSubmission());

            verify(statusEventProducer).unsupportedLanguage(10, 100);
            verifyNoInteractions(problemClient);
        }

        @Test
        @DisplayName("should delegate to processSubmission(SubmissionCreated, Compiler) when compiler is found")
        void shouldDelegateWhenCompilerFound() {
            Compiler compiler = mock(Compiler.class);
            when(compiler.compile(anyString())).thenReturn(new CompilationResult.Failure("error"));
            when(compilerService.getCompiler("java")).thenReturn(Optional.of(compiler));

            submissionService.processSubmission(defaultSubmission());

            verify(compiler).compile(anyString());
            verify(statusEventProducer).compilationError(10, 100);
        }
    }

    @Nested
    @DisplayName("processSubmission(SubmissionCreated, Compiler)")
    class ProcessSubmissionInternalTests {

        private Compiler compiler;
        private CompiledProgram compiledProgram;
        private final int PROBLEM_ID = 10;
        private final int SUBMISSION_ID = 100;

        @BeforeEach
        void setUp() {
            compiler = mock(Compiler.class);
            compiledProgram = mock(CompiledProgram.class);
            when(compiler.compile(anyString())).thenReturn(new CompilationResult.Success(compiledProgram));
        }

        @Test
        @DisplayName("should handle null submission gracefully without throwing")
        void shouldHandleNullSubmission() {
            assertDoesNotThrow(() -> submissionService.processSubmission(null, compiler));

            verifyNoInteractions(compiler, problemClient, statusEventProducer);
        }

        @Test
        @DisplayName("should emit COMPILATION_ERROR and return early if compilation fails")
        void shouldEmitCompilationError() {
            when(compiler.compile(anyString())).thenReturn(new CompilationResult.Failure("Syntax error"));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compilationError(PROBLEM_ID, SUBMISSION_ID);
            verifyNoInteractions(problemClient);
            verifyNoInteractions(compiledProgram);
        }

        @Test
        @DisplayName("should emit INTERNAL_ERROR when testcases are null")
        void shouldEmitInternalErrorWhenTestcasesNull() {
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(null);

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).internalError(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram).cleanup();
        }

        @Test
        @DisplayName("should emit INTERNAL_ERROR when testcases list is empty")
        void shouldEmitInternalErrorWhenTestcasesEmpty() {
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(Collections.emptyList());

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).internalError(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram).cleanup();
        }

        @Test
        @DisplayName("should emit ACCEPTED when all testcases pass")
        void shouldEmitAcceptedWhenAllTestcasesPass() {
            List<TestcaseDto> testcases = List.of(
                    new TestcaseDto("1, 2", "3", 1),
                    new TestcaseDto("2, 3", "5", 2)
            );
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(testcases);
            
            when(compiledProgram.execute(List.of("1", "2"), 2)).thenReturn(new ExecutionResult.Success("3"));
            when(compiledProgram.execute(List.of("2", "3"), 2)).thenReturn(new ExecutionResult.Success("5"));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).accepted(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram).cleanup();
        }

        @Test
        @DisplayName("should emit WRONG_ANSWER and stop on first failure")
        void shouldEmitWrongAnswerAndStop() {
            List<TestcaseDto> testcases = List.of(
                    new TestcaseDto("1, 2", "3", 1), // passing
                    new TestcaseDto("2, 3", "5", 2), // failing
                    new TestcaseDto("3, 4", "7", 3)  // shouldn't run
            );
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(testcases);
            
            when(compiledProgram.execute(List.of("1", "2"), 2)).thenReturn(new ExecutionResult.Success("3"));
            when(compiledProgram.execute(List.of("2", "3"), 2)).thenReturn(new ExecutionResult.Success("WrongOutput"));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).wrongAnswer(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram, times(2)).execute(any(), anyInt());
            verify(compiledProgram).cleanup();
        }

        @Test
        @DisplayName("should handle ExecutionResult.Timeout by emitting TIME_LIMIT_EXCEEDED and stopping")
        void shouldHandleTimeoutStatus() {
            List<TestcaseDto> testcases = List.of(new TestcaseDto("input", "expected", 1));
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(testcases);
            
            when(compiledProgram.execute(List.of("input"), 2)).thenReturn(new ExecutionResult.Timeout());

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).timeLimitExceeded(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram).cleanup();
        }

        @Test
        @DisplayName("should handle ExecutionResult.Failure by emitting INTERNAL_ERROR and stopping subsequent testcases")
        void shouldStopAfterInternalError() {
            List<TestcaseDto> testcases = List.of(
                    new TestcaseDto("1, 2", "3", 1),
                    new TestcaseDto("2, 3", "5", 2)
            );
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(testcases);
            
            when(compiledProgram.execute(anyList(), anyInt())).thenReturn(new ExecutionResult.Failure("Runtime error"));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).internalError(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram, times(1)).execute(anyList(), anyInt());
            verify(statusEventProducer, never()).accepted(anyInt(), anyInt());
            verify(compiledProgram).cleanup();
        }

        @Test
        @DisplayName("should handle ExecutionResult.Failure by emitting INTERNAL_ERROR and stopping")
        void shouldHandleExecutionFailure() {
            List<TestcaseDto> testcases = List.of(new TestcaseDto("input", "expected", 1));
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(testcases);
            
            when(compiledProgram.execute(List.of("input"), 2)).thenReturn(new ExecutionResult.Failure("Runtime error"));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).internalError(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram).cleanup();
        }

        @Test
        @DisplayName("should call cleanup() even if problemClient throws an exception")
        void shouldCleanupWhenProblemClientThrows() {
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenThrow(new RuntimeException("Feign client down"));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).internalError(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram).cleanup();
        }

        @Test
        @DisplayName("should call cleanup() even if compiledProgram.execute throws an exception")
        void shouldCleanupWhenProgramExecuteThrows() {
            List<TestcaseDto> testcases = List.of(new TestcaseDto("input", "expected", 1));
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(testcases);
            
            when(compiledProgram.execute(anyList(), anyInt())).thenThrow(new RuntimeException("Sandbox crash"));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).internalError(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram).cleanup();
        }

        @Test
        @DisplayName("should correctly format multiline success output")
        void shouldFormatMultilineOutput() {
            List<TestcaseDto> testcases = List.of(new TestcaseDto("input", "a, b", 1));
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(testcases);
            
            when(compiledProgram.execute(List.of("input"), 2)).thenReturn(new ExecutionResult.Success(" a \n b \n"));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).accepted(PROBLEM_ID, SUBMISSION_ID);
        }

        @Test
        @DisplayName("should handle null execution output as empty string instead of NPE")
        void shouldHandleNullExecutionOutput() {
            List<TestcaseDto> testcases = List.of(new TestcaseDto("input", "", 1));
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(testcases);
            
            when(compiledProgram.execute(List.of("input"), 2)).thenReturn(new ExecutionResult.Success(null));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).accepted(PROBLEM_ID, SUBMISSION_ID);
        }

        @Test
        @DisplayName("should handle null inputs in testcases gracefully")
        void shouldHandleNullInputsGracefully() {
            List<TestcaseDto> testcases = List.of(new TestcaseDto(null, "expected", 1));
            when(problemClient.getProblemTestcases(PROBLEM_ID)).thenReturn(testcases);
            
            when(compiledProgram.execute(List.of(), 2)).thenReturn(new ExecutionResult.Success("expected"));

            submissionService.processSubmission(defaultSubmission(), compiler);

            verify(statusEventProducer).compiled(PROBLEM_ID, SUBMISSION_ID);
            verify(statusEventProducer).accepted(PROBLEM_ID, SUBMISSION_ID);
            verify(compiledProgram).execute(List.of(), 2);
        }
    }
}

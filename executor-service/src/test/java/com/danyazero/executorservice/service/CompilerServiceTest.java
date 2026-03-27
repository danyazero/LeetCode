package com.danyazero.executorservice.service;

import com.danyazero.executorservice.model.Compiler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompilerServiceTest {

    private static Compiler mockCompiler(String language) {
        Compiler compiler = mock(Compiler.class);
        when(compiler.getLanguage()).thenReturn(language);
        return compiler;
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("should initialize with empty compiler collection")
        void shouldInitializeWithEmptyCollection() {
            CompilerService service = new CompilerService(Collections.emptyList());

            assertTrue(service.getCompiler("java").isEmpty());
        }

        @Test
        @DisplayName("should initialize with a single compiler")
        void shouldInitializeWithSingleCompiler() {
            Compiler javaCompiler = mockCompiler("java");
            CompilerService service = new CompilerService(List.of(javaCompiler));

            assertTrue(service.getCompiler("java").isPresent());
        }

        @Test
        @DisplayName("should initialize with multiple distinct compilers")
        void shouldInitializeWithMultipleCompilers() {
            Compiler javaCompiler = mockCompiler("java");
            Compiler pythonCompiler = mockCompiler("python");
            CompilerService service = new CompilerService(List.of(javaCompiler, pythonCompiler));

            assertTrue(service.getCompiler("java").isPresent());
            assertTrue(service.getCompiler("python").isPresent());
        }

        @Test
        @DisplayName("should skip compilers with null language without throwing")
        void shouldSkipCompilersWithNullLanguage() {
            Compiler nullLangCompiler = mockCompiler(null);
            Compiler javaCompiler = mockCompiler("java");

            CompilerService service = new CompilerService(List.of(nullLangCompiler, javaCompiler));

            assertTrue(service.getCompiler("java").isPresent());
        }

        @Test
        @DisplayName("should handle all compilers having null language")
        void shouldHandleAllNullLanguageCompilers() {
            Compiler nullLang1 = mockCompiler(null);
            Compiler nullLang2 = mockCompiler(null);

            CompilerService service = new CompilerService(List.of(nullLang1, nullLang2));

            assertTrue(service.getCompiler("java").isEmpty());
        }

        @Test
        @DisplayName("should keep first compiler on duplicate language keys")
        void shouldKeepFirstOnDuplicateLanguage() {
            Compiler firstJava = mockCompiler("java");
            Compiler secondJava = mockCompiler("java");

            CompilerService service = new CompilerService(List.of(firstJava, secondJava));

            Optional<Compiler> result = service.getCompiler("java");
            assertTrue(result.isPresent());
            assertSame(firstJava, result.get());
        }

        @Test
        @DisplayName("should handle duplicate languages with different casing")
        void shouldHandleDuplicateLanguagesWithDifferentCasing() {
            Compiler upperJava = mockCompiler("JAVA");
            Compiler lowerJava = mockCompiler("java");

            CompilerService service = new CompilerService(List.of(upperJava, lowerJava));

            Optional<Compiler> result = service.getCompiler("java");
            assertTrue(result.isPresent());
            assertSame(upperJava, result.get(), "Should have kept the first registered compiler ('JAVA')");
        }

        @Test
        @DisplayName("should normalize registered compiler language to lowercase")
        void shouldNormalizeRegisteredCompilerLanguageToLowercase() {
            Compiler upperPython = mockCompiler("PYTHON");

            CompilerService service = new CompilerService(List.of(upperPython));

            assertTrue(service.getCompiler("python").isPresent(), "Compiler registered with uppercase should be retrievable by lowercase");
            assertSame(upperPython, service.getCompiler("python").get());
        }

        @Test
        @DisplayName("should handle mix of null and duplicate compilers")
        void shouldHandleMixOfNullAndDuplicateCompilers() {
            Compiler nullLang = mockCompiler(null);
            Compiler firstJava = mockCompiler("java");
            Compiler secondJava = mockCompiler("java");
            Compiler python = mockCompiler("python");

            CompilerService service = new CompilerService(List.of(nullLang, firstJava, secondJava, python));

            assertSame(firstJava, service.getCompiler("java").orElse(null));
            assertSame(python, service.getCompiler("python").orElse(null));
        }

        @Test
        @DisplayName("should treat null collection as empty")
        void shouldTreatNullCollectionAsEmpty() {
            CompilerService service = new CompilerService(null);

            assertTrue(service.getCompiler("java").isEmpty());
        }

        @Test
        @DisplayName("should skip null elements in collection")
        void shouldSkipNullElementsInCollection() {
            List<Compiler> compilers = Arrays.asList(null, mockCompiler("java"));

            CompilerService service = new CompilerService(compilers);

            assertTrue(service.getCompiler("java").isPresent());
        }

        @Test
        @DisplayName("should register compiler with empty-string language")
        void shouldRegisterCompilerWithEmptyStringLanguage() {
            Compiler emptyLang = mockCompiler("");

            CompilerService service = new CompilerService(List.of(emptyLang));

            assertTrue(service.getCompiler("").isPresent());
            assertSame(emptyLang, service.getCompiler("").get());
        }
    }

    @Nested
    @DisplayName("getCompiler")
    class GetCompilerTests {

        private final CompilerService service;
        private final Compiler javaCompiler;

        GetCompilerTests() {
            javaCompiler = mockCompiler("java");
            Compiler pythonCompiler = mockCompiler("python");
            service = new CompilerService(List.of(javaCompiler, pythonCompiler));
        }

        @Test
        @DisplayName("should return compiler for exact match")
        void shouldReturnCompilerForExactMatch() {
            Optional<Compiler> result = service.getCompiler("java");

            assertTrue(result.isPresent());
            assertSame(javaCompiler, result.get());
        }

        @Test
        @DisplayName("should return compiler case-insensitively (uppercase)")
        void shouldReturnCompilerCaseInsensitiveUppercase() {
            assertTrue(service.getCompiler("JAVA").isPresent());
        }

        @Test
        @DisplayName("should return compiler case-insensitively (mixed case)")
        void shouldReturnCompilerCaseInsensitiveMixed() {
            assertTrue(service.getCompiler("JaVa").isPresent());
        }

        @Test
        @DisplayName("should return empty for unknown language")
        void shouldReturnEmptyForUnknownLanguage() {
            assertTrue(service.getCompiler("rust").isEmpty());
        }

        @Test
        @DisplayName("should return empty for null language")
        void shouldReturnEmptyForNullLanguage() {
            Optional<Compiler> result = service.getCompiler(null);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should return empty for empty string")
        void shouldReturnEmptyForEmptyString() {
            assertTrue(service.getCompiler("").isEmpty());
        }

        @Test
        @DisplayName("should return empty for language with whitespace")
        void shouldReturnEmptyForLanguageWithWhitespace() {
            assertTrue(service.getCompiler(" java ").isEmpty());
        }

        @Test
        @DisplayName("should return empty when no compilers registered")
        void shouldReturnEmptyWhenNoCompilersRegistered() {
            CompilerService emptyService = new CompilerService(Collections.emptyList());

            assertTrue(emptyService.getCompiler("java").isEmpty());
        }
    }
}

package com.danyazero.problemservice.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProblemTest {

    @Test
    @DisplayName("Should return zero acceptance rate when sent submissions is zero")
    void getAcceptanceRate_shouldReturnZeroWhenSentSubmissionsIsZero() {
        Problem problem = Problem.builder()
            .sentSubmissions(0)
            .acceptedSubmissions(0)
            .build();

        assertEquals(0.0, problem.getAcceptanceRate());
    }

    @Test
    @DisplayName("Should calculate percentage when counters are valid")
    void getAcceptanceRate_shouldReturnCalculatedRateWhenCountersAreValid() {
        Problem problem = Problem.builder()
            .sentSubmissions(8)
            .acceptedSubmissions(3)
            .build();

        assertEquals(37.5, problem.getAcceptanceRate());
    }

    @Test
    @DisplayName("Should return zero acceptance rate when counters are null")
    void getAcceptanceRate_shouldReturnZeroWhenCountersAreNull() {
        Problem problem = Problem.builder()
            .sentSubmissions(null)
            .acceptedSubmissions(null)
            .build();

        assertEquals(0.0, problem.getAcceptanceRate());
    }

    @Test
    @DisplayName("Should return zero acceptance rate when counters are negative")
    void getAcceptanceRate_shouldReturnZeroWhenCountersAreNegative() {
        Problem problem = Problem.builder()
            .sentSubmissions(-4)
            .acceptedSubmissions(-1)
            .build();

        assertEquals(0.0, problem.getAcceptanceRate());
    }

    @Test
    @DisplayName("Should cap acceptance rate at one hundred percent when accepted exceeds sent")
    void getAcceptanceRate_shouldCapRateWhenAcceptedExceedsSent() {
        Problem problem = Problem.builder()
            .sentSubmissions(4)
            .acceptedSubmissions(6)
            .build();

        assertEquals(100.0, problem.getAcceptanceRate());
    }
}

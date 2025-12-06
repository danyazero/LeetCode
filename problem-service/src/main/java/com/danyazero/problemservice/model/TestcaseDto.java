package com.danyazero.problemservice.model;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.entity.Testcase;

import java.time.Instant;

public record TestcaseDto(
        String input,
        String expected,
        Integer problemId
) {
    public Testcase toEntity(Problem problem) {
        return Testcase.builder()
                .createdAt(Instant.now())
                .expected(this.expected)
                .input(this.input)
                .problem(problem)
                .build();
    }
}
package com.danyazero.problemservice.model;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.entity.Testcase;
import java.time.Instant;

public record TestcaseDto(
    String input,
    String expected,
    Integer problemId,
    boolean isPublic
) {
    public Testcase toEntity(Problem problem) {
        return Testcase.builder()
            .createdAt(Instant.now())
            .expected(this.expected)
            .isPublic(this.isPublic)
            .input(this.input)
            .problem(problem)
            .build();
    }

    public static TestcaseDto toDto(Testcase testcase) {
        return new TestcaseDto(
            testcase.getInput(),
            testcase.getExpected(),
            testcase.getProblemId(),
            testcase.isPublic()
        );
    }
}

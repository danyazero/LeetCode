package com.danyazero.problemservice.model;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.entity.Testcase;

import java.time.Instant;
import java.util.Arrays;

public record TestcaseDto(
        String[] input,
        String[] output,
        Integer problemId
) {
    public Testcase toEntity(Problem problem) {
        return Testcase.builder()
                .input(Arrays.toString(this.input))
                .output(Arrays.toString(this.output))
                .createdAt(Instant.now())
                .problem(problem)
                .build();
    }
}
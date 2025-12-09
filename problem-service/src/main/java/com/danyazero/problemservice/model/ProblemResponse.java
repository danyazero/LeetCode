package com.danyazero.problemservice.model;

import com.danyazero.problemservice.entity.Difficulty;
import lombok.Builder;

import java.util.List;

@Builder
public record ProblemResponse(
        int id,
        String title,
        String description,
        Difficulty difficulty,
        List<TestcaseDto> testcases
) { }

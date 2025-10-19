package com.danyazero.problemservice.model;

import com.danyazero.problemservice.entity.Difficulty;

public record ProblemDto(
        String title,
        Difficulty difficulty,
        Double acceptanceRate
) {
}

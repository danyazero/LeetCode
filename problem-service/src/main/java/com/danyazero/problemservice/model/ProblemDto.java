package com.danyazero.problemservice.model;

import com.danyazero.problemservice.entity.Difficulty;
import com.danyazero.problemservice.entity.Problem;
import lombok.Builder;

@Builder
public record ProblemDto(
    Integer id,
    String title,
    Difficulty difficulty,
    Double acceptanceRate,
    Integer submissions
) {
    public static ProblemDto map(Problem problem) {
        return ProblemDto.builder()
            .acceptanceRate(problem.getAcceptanceRate())
            .submissions(problem.getSentSubmissions())
            .difficulty(problem.getDifficulty())
            .title(problem.getTitle())
            .id(problem.getId())
            .build();
    }
}

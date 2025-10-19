package com.danyazero.problemservice.model;

import com.danyazero.problemservice.entity.Difficulty;
import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.entity.Tag;

import java.time.Instant;
import java.util.Set;

public record ProblemDto(
        String title,
        String description,
        Difficulty difficulty,
        Set<Tag> tags
) {
    public Problem toEntity() {
        return Problem.builder()
                .description(this.description)
                .difficulty(this.difficulty)
                .createdAt(Instant.now())
                .createdAt(Instant.now())
                .title(this.title)
                .tags(this.tags)
                .build();
    }
}

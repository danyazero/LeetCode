package com.danyazero.problemservice.model;

import com.danyazero.problemservice.entity.Difficulty;
import com.danyazero.problemservice.entity.Tag;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record ProblemResponse(
        int id,
        String title,
        Set<Tag> tags,
        String description,
        Difficulty difficulty,
        List<TestDto> testcases
) { }

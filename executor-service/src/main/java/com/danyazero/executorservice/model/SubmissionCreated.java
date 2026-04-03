package com.danyazero.executorservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SubmissionCreated(
        UUID userId,
        @JsonProperty("submissionId") Integer id,
        String language,
        Integer problemId,
        String solution
) {
}

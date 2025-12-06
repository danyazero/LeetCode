package com.danyazero.executorservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SubmissionCreated(
        @JsonProperty("submissionId") Integer id,
        String language,
        Integer problemId,
        String solution
) {
}

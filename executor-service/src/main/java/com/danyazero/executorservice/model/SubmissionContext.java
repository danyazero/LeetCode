package com.danyazero.executorservice.model;

import com.danyazero.executorservice.utils.ProcessExecutor;
import lombok.Builder;

@Builder
public record SubmissionContext(
        Compiler executor,
        Integer submissionId,
        ProcessExecutor processExecutor
) {
}

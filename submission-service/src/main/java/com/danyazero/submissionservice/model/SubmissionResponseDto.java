package com.danyazero.submissionservice.model;

import com.danyazero.submissionservice.entity.Submission;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SubmissionResponseDto(
        Integer submissionId,
        String language,
        SubmissionStatus status,
        Instant createdAt
) {
    public static SubmissionResponseDto from(Submission submission) {
        return SubmissionResponseDto.builder()
                .language(submission.getLanguage().getLanguage())
                .createdAt(submission.getCreatedAt())
                .submissionId(submission.getId())
                .status(submission.getStatus())
                .build();
    }
}

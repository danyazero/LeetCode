package com.danyazero.submissionservice.mapper;

import com.danyazero.submissionservice.model.SubmissionResponseDto;
import com.danyazero.submissionservice.entity.Submission;

public class SubmissionMapper {
    public static SubmissionResponseDto map(Submission submission) {
        return SubmissionResponseDto.builder()
                .language(submission.getLanguage().getLanguage())
                .createdAt(submission.getCreatedAt())
                .submissionId(submission.getId())
                .status(submission.getStatus())
                .build();
    }

}
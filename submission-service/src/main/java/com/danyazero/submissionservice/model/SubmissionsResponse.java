package com.danyazero.submissionservice.model;

import lombok.Builder;

@Builder
public record SubmissionsResponse(
        PageDto<SubmissionResponseDto> submissions,
        boolean isAccepted
) { }

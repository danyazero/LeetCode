package com.danyazero.submissionservice.mapper;

import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.model.PageDto;
import com.danyazero.submissionservice.model.SubmissionResponseDto;
import org.springframework.data.domain.Page;

public class SubmissionsPageMapper {

    public static PageDto<SubmissionResponseDto> map(
        Page<Submission> submissions
    ) {
        var content = submissions
            .getContent()
            .stream()
            .map(SubmissionMapper::map)
            .toList();

        return PageDto.<SubmissionResponseDto>builder()
            .pageNumber(submissions.getPageable().getPageNumber())
            .pageSize(submissions.getPageable().getPageSize())
            .totalPages(submissions.getTotalPages())
            .isFirst(submissions.isFirst())
            .isLast(submissions.isLast())
            .content(content)
            .build();
    }
}

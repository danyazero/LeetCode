package com.danyazero.submissionservice.model;

import java.util.List;
import lombok.Builder;

@Builder
public record PageDto<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    int totalPages,
    boolean isLast,
    boolean isFirst
) {}

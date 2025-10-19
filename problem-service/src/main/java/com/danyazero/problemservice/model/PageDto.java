package com.danyazero.problemservice.model;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PageDto<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        int totalPages,
        boolean isLast,
        boolean isFirst
) {
    public static <T> PageDto<T> of(Page<T> page) {
        return PageDto.<T>builder()
                .pageNumber(page.getPageable().getPageNumber())
                .pageSize(page.getPageable().getPageSize())
                .totalPages(page.getTotalPages())
                .content(page.getContent())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}

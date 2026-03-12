package com.danyazero.problemservice.model;

import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PageDto<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    int totalPages,
    boolean isLast,
    boolean isFirst
) {
    public static <T, K> PageDto<T> of(Page<K> page, Function<K, T> mapper) {
        var mappedContent = page.getContent().stream().map(mapper).toList();

        return PageDto.<T>builder()
            .pageNumber(page.getPageable().getPageNumber())
            .pageSize(page.getPageable().getPageSize())
            .totalPages(page.getTotalPages())
            .isFirst(page.isFirst())
            .content(mappedContent)
            .isLast(page.isLast())
            .build();
    }

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

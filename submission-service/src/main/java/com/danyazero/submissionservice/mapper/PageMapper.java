package com.danyazero.submissionservice.mapper;

import org.springframework.data.domain.Page;
import com.danyazero.submissionservice.model.PageDto;

public class PageMapper {
    public static <T> PageDto<T> map(Page<T> page) {
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
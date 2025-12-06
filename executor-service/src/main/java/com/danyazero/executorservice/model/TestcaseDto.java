package com.danyazero.executorservice.model;

public record TestcaseDto(
        String input,
        String expected,
        Integer id
) {
}

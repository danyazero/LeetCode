package com.danyazero.problemservice.model;

public record SubmissionsDto(
        int submissions,
        double acceptanceRate,
        boolean isAccepted
) {
}

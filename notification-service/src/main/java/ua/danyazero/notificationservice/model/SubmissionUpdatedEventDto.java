package ua.danyazero.notificationservice.model;

import java.util.UUID;

public record SubmissionUpdatedEventDto(
        UUID userId,
        Integer problemId,
        Integer submissionId,
        SubmissionStatus submissionStatus
) { }

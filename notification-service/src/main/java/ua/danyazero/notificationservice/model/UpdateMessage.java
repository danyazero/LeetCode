package ua.danyazero.notificationservice.model;

import lombok.Builder;

@Builder
public record UpdateMessage(
        Integer submission_id,
        SubmissionStatus status
) {
}

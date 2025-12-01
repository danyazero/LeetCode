package com.danyazero.submissionservice.model;

import lombok.Getter;

@Getter
public class SubmissionCreatedEvent extends Event {
    private final SubmissionCreatedEventDto data;

    public SubmissionCreatedEvent(String eventType, Integer eventVersion, SubmissionCreatedEventDto data) {
        super(eventType, eventVersion);
        this.data = data;
    }
}

package com.danyazero.submissionservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SubmissionUpdatedEvent extends Event{
    private SubmissionUpdatedEventDto data;

    public SubmissionUpdatedEvent(String eventType, Integer eventVersion, SubmissionUpdatedEventDto data) {
        super(eventType, eventVersion);
        this.data = data;
    }
}

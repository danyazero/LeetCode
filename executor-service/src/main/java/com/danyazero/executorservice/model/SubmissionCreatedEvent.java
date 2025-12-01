package com.danyazero.executorservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubmissionCreatedEvent extends Event {
    private SubmissionCreatedEventDto data;

    public SubmissionCreatedEvent(Integer eventVersion, SubmissionCreatedEventDto data) {
        super("submissionCreated", eventVersion);
        this.data = data;
    }
}

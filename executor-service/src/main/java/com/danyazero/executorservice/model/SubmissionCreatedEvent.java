package com.danyazero.executorservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubmissionCreatedEvent extends Event {
    private SubmissionCreated data;

    public SubmissionCreatedEvent(Integer eventVersion, SubmissionCreated data) {
        super("submissionCreated", eventVersion);
        this.data = data;
    }
}

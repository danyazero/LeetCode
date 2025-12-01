package com.danyazero.executorservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SubmissionUpdatedEvent extends Event{
    private SubmissionUpdatedEventDto data;

    public SubmissionUpdatedEvent(Integer eventVersion, SubmissionUpdatedEventDto data) {
        super("submissionUpdated", eventVersion);
        this.data = data;
    }

}

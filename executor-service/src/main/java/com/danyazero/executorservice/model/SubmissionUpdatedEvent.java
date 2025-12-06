package com.danyazero.executorservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SubmissionUpdatedEvent extends Event{
    private SubmissionUpdated data;

    public SubmissionUpdatedEvent(Integer eventVersion, SubmissionUpdated data) {
        super("submissionUpdated", eventVersion);
        this.data = data;
    }

}

package com.danyazero.submissionservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Event {
    private final String eventType;
    private String eventId = UUID.randomUUID().toString();
    private Instant eventTime = Instant.now();
    private final Integer eventVersion;
}

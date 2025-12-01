package com.danyazero.submissionservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Problem(
        Integer id,
        String methodSchema
) { }
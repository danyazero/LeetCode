package com.danyazero.executorservice.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExecutionMode {
    STANDALONE_ONCE("o"),
    STANDALONE_EXECVE("e"),
    STANDALONE_RERUN("r"),
    LISTEN_TCP("l");

    private final String mode;
}

package com.danyazero.problemservice.model;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthenticatedUser {

    private final UUID id;
    private final String username;

    public AuthenticatedUser(String userId, String username) {
        this(UUID.fromString(userId), username);
    }
}

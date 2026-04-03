package ua.danyazero.notificationservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AuthenticatedUser {

    private final UUID id;
    private final String username;

    public AuthenticatedUser(String userId, String username) {
        this(UUID.fromString(userId), username);
    }
}

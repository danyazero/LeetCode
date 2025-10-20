package com.danyazero.userservice.model;

import com.danyazero.userservice.entity.User;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.function.Function;

@Builder
public record CreateUserDto(
        String name,
        String username,
        String password
) {
    public User toEntity(PasswordUtility passwordEncoder) {

        return User.builder()
                .password(passwordEncoder.encode(password))
                .createdAt(Instant.now())
                .username(this.username)
                .name(this.name)
                .build();
    }
}

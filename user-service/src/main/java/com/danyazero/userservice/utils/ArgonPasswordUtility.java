package com.danyazero.userservice.utils;

import com.danyazero.userservice.model.PasswordUtility;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ArgonPasswordUtility implements PasswordUtility {
    private final Argon2PasswordEncoder passwordEncoder;

    public ArgonPasswordUtility() {
        passwordEncoder = new Argon2PasswordEncoder(32, 64, 1, 60000, 5);
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

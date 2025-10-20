package com.danyazero.userservice.model;

public interface PasswordUtility {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}

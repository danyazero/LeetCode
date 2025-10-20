package com.danyazero.userservice.controller;

import com.danyazero.userservice.entity.User;
import com.danyazero.userservice.model.UserCredentialsDto;
import com.danyazero.userservice.model.CreateUserDto;
import com.danyazero.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PutMapping
    public User createUser(@RequestBody CreateUserDto user) {
        return userService.createUser(user);
    }

    @PostMapping
    public String authenticateUser(@RequestBody UserCredentialsDto user) {
        return userService.authenticateUser(user);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }
}

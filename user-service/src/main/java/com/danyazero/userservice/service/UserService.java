package com.danyazero.userservice.service;

import com.danyazero.userservice.entity.User;
import com.danyazero.userservice.exception.InvalidCredentialsException;
import com.danyazero.userservice.exception.UserNotFoundException;
import com.danyazero.userservice.exception.UsernameAlreadyTakenException;
import com.danyazero.userservice.model.UserCredentialsDto;
import com.danyazero.userservice.model.CreateUserDto;
import com.danyazero.userservice.repository.UserRepository;
import com.danyazero.userservice.utils.ArgonPasswordUtility;
import com.danyazero.userservice.utils.TokenUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ArgonPasswordUtility passwordUtility;

    public User createUser(CreateUserDto user) {
        var isUsernameUsed = userRepository.existsUserByUsernameEquals(user.username());
        if (isUsernameUsed)
            throw new UsernameAlreadyTakenException(String.format("Username '%s' is already taken", user.username()));

        return userRepository.save(user.toEntity(passwordUtility));
    }

    public String authenticateUser(UserCredentialsDto credentials) {
        var foundedUser = userRepository.getFirstByUsername(credentials.username())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials."));

        if (passwordUtility.matches(credentials.password(), foundedUser.getPassword())) {
            return TokenUtility.generateToken(foundedUser.getId());
        }

        throw new InvalidCredentialsException("Invalid credentials.");
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id '%s' not found", userId)));
    }
}

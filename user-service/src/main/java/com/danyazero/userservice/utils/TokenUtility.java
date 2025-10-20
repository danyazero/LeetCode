package com.danyazero.userservice.utils;

import com.danyazero.userservice.exception.CorruptedTokenException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class TokenUtility {
    private static final String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E";

    public static String generateToken(UUID userId) {
        var key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .expiration(Date.from(Instant.now().plus(4, ChronoUnit.DAYS)))
                .issuedAt(Date.from(Instant.now()))
                .subject(userId.toString())
                .issuer("user-service")
                .signWith(key)
                .compact();
    }

    public static Map<String, String> getTokenPayload(String token) {
        log.debug("TokenUtility.getTokenPayload(token={})", token);
        var tokenParts = token.split("\\.");
        if (tokenParts.length != 3) throw new CorruptedTokenException("Invalid token format.");

        var tokenBody = new String(Base64.getDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);
        log.info("Decoded token body is '{}'", tokenBody);
        try {
            return new ObjectMapper().readValue(tokenBody, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new CorruptedTokenException("An error occurred while parsing the token body.");
        }
    }
}

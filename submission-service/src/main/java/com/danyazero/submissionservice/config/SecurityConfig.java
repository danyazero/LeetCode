package com.danyazero.submissionservice.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@SecurityScheme(
        name = "bearerAuthorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@RequiredArgsConstructor
public class SecurityConfig {
    private final KeycloakJwtConverter jwtConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/problems/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/languages").hasAuthority("submission.edit_languages")
                        .requestMatchers(HttpMethod.POST, "/api/v1/submissions").hasAuthority("submission.send_submissions")
                        .requestMatchers(HttpMethod.GET, "/api/v1/submissions/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/submissions/file/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/submissions/problems/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtConverter)
                        )
                )
                .build();
    }
}

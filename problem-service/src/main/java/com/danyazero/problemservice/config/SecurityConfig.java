package com.danyazero.problemservice.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SecurityScheme(
        name = "bearerAuthorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SecurityConfig {
    private final KeycloakJwtConverter jwtConverter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/difficulties").hasAuthority("problem.edit_difficulties")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/difficulties/*").hasAuthority("problem.edit_difficulties")

                        .requestMatchers(HttpMethod.POST, "/api/v1/problems").hasAuthority("problem.edit_problems")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/problems/*").hasAuthority("problem.edit_problems")

                        .requestMatchers(HttpMethod.POST, "/api/v1/tags").hasAuthority("problem.edit_tags")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tags/*").hasAuthority("problem.edit_tags")

                        .requestMatchers(HttpMethod.POST, "/api/v1/testcases").hasAuthority("problem.edit_testcases")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/testcases").hasAuthority("problem.edit_testcases")
                        .requestMatchers(HttpMethod.GET, "/api/v1/testcases/*").hasAuthority("problem.view_testcases")

                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtConverter)
                        )
                )
                .build();

    }
}

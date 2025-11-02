package com.danyazero.submissionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/languages").hasAuthority("submission.edit_languages")
                        .requestMatchers(HttpMethod.POST, "/api/v1/submissions").hasAuthority("submission.send_submissions")
                        .requestMatchers(HttpMethod.GET, "/api/v1/submissions/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/submissions/file/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/submissions/problem/*").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtConverter());
        converter.setPrincipalClaimName("sub");

        return converter;
    }
}

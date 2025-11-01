package com.danyazero.problemservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.lang.invoke.MethodType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
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

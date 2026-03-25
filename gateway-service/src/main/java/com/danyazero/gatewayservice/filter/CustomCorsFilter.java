package com.danyazero.gatewayservice.filter;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CustomCorsFilter implements WebFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(CustomCorsFilter.class);

    private static final List<String> CORS_ENABLED_ROUTES = Arrays.asList(
        "problem.localhost",
        "submission.localhost"
    );

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "http://localhost",
        "http://swagger.localhost"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("New request -> {}", request.getId());
        String origin = request.getHeaders().getOrigin();
        if (origin == null) {
            log.info("Request {}: Request origin is null, skipping filter.", request.getId());
            return chain.filter(exchange);
        }

        log.info("Request {}: From origin: {}", request.getId(), origin);

        var host = exchange.getRequest().getHeaders().getHost();
        if (host == null) {
            log.info("Request {}: Request hostname is null, skipping filter.", request.getId());
            return chain.filter(exchange);
        }
        log.info("Request {}: Request has next hostname -> {}", request.getId(), host.getHostName());

        if (
            CORS_ENABLED_ROUTES.contains(host.getHostName()) &&
            ALLOWED_ORIGINS.contains(origin)
        ) {
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();

            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");

            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

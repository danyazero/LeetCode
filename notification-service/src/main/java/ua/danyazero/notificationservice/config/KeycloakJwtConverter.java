package ua.danyazero.notificationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import ua.danyazero.notificationservice.model.AuthenticatedUser;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KeycloakJwtConverter
    implements Converter<Jwt, AbstractAuthenticationToken>
{

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        var username = jwt.getClaimAsString("preferred_username");
        var userId = jwt.getClaimAsString("uid");
        AuthenticatedUser principal = new AuthenticatedUser(
            userId,
            username
        );
        log.info("Authorizing user with id -> {}", userId);

        return new JwtAuthenticationToken(
            jwt,
            mapGrantedAuthorities(jwt),
            userId
        ) {
            @Override
            public Object getPrincipal() {
                return principal;
            }
        };
    }

    private static Collection<GrantedAuthority> mapGrantedAuthorities(Jwt jwt) {
        var roles = jwt.getClaimAsStringList("roles");
        if (roles == null || roles.isEmpty()) return Collections.emptyList();

        return roles
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}

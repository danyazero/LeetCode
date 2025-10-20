package com.danyazero.userservice.service;

import com.danyazero.userservice.entity.Link;
import com.danyazero.userservice.exception.CorruptedTokenException;
import com.danyazero.userservice.exception.UserNotFoundException;
import com.danyazero.userservice.model.LinkDto;
import com.danyazero.userservice.repository.LinkRepository;
import com.danyazero.userservice.repository.UserRepository;
import com.danyazero.userservice.utils.TokenUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public Link saveLink(LinkDto link, String authorizationHeader) {
        var userId = getUserId(authorizationHeader);
        var foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) throw new UserNotFoundException("User not found");

        var linkEntity = Link.builder()
                .title(link.title())
                .link(link.link())
                .user(foundUser.get())
                .createdAt(Instant.now())
                .build();

        return linkRepository.save(linkEntity);
    }

    public void deleteLink(Integer linkId, String authorizationHeader) {
        var userId = getUserId(authorizationHeader);
        linkRepository.deleteLinkByIdAndUser_Id(linkId, userId);
    }

    private UUID getUserId(String authorizationHeader) {
        var authorizationToken = authorizationHeader.substring(7);
        var tokenPayload = TokenUtility.getTokenPayload(authorizationToken);

        var tokenSubject = tokenPayload.get("sub");
        if (tokenSubject == null) throw new CorruptedTokenException("Token should include 'sub' field.");
        log.info("Extracted userId from token is -> '{}'", tokenSubject);

        return UUID.fromString(tokenSubject);
    }
}

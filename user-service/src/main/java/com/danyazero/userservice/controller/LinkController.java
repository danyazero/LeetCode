package com.danyazero.userservice.controller;

import com.danyazero.userservice.entity.Link;
import com.danyazero.userservice.model.LinkDto;
import com.danyazero.userservice.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/links")
public class LinkController {
    private final LinkService linkService;

    @PostMapping
    public Link saveLink(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestBody LinkDto link
    ) {

        return linkService.saveLink(link, authorizationHeader);
    }

    @DeleteMapping("/{linkId}")
    public void deleteLink(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable Integer linkId
    ) {
        linkService.deleteLink(linkId, authorizationHeader);
    }

}

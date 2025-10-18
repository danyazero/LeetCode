package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Tag;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.model.TagDto;
import com.danyazero.problemservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagRepository tagRepository;

    @PostMapping
    public Tag createTopicTag(@RequestBody TagDto tagDto) {
        var tagEntity = Tag.builder()
                .tag(tagDto.title())
                .build();

        return tagRepository.save(tagEntity);
    }

    @GetMapping("/{query}")
    public PageDto<Tag> getTagsByQuery(
            @PathVariable String query,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        var foundedTagsPage = tagRepository.findAllByTagIsContainingIgnoreCase(query, PageRequest.of(page, size));

        return PageDto.map(foundedTagsPage);
    }

    @GetMapping
    public PageDto<Tag> findAll(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        var tagPage = tagRepository.findAll(PageRequest.of(page, size));

        return PageDto.map(tagPage);
    }
}

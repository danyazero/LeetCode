package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Tag;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.model.TagDto;
import com.danyazero.problemservice.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    @PreAuthorize("hasAuthority('problem.edit_tags')")
    public Tag createTopicTag(@RequestBody TagDto tagDto) {
        return tagService.createTag(tagDto);
    }

    @GetMapping("/{query}")
    public PageDto<Tag> getTagsByQuery(
        @PathVariable String query,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return tagService.getTagsByQuery(query, page, size);
    }

    @GetMapping
    public PageDto<Tag> findAll(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return tagService.getTags(page, size);
    }

    @DeleteMapping("/{tagId}")
    @PreAuthorize("hasAuthority('problem.edit_tags')")
    public void deleteById(@PathVariable int tagId) {
        tagService.deleteTag(tagId);
    }
}

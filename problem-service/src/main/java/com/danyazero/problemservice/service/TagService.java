package com.danyazero.problemservice.service;

import com.danyazero.problemservice.entity.Tag;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.model.TagDto;
import com.danyazero.problemservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag createTag(TagDto tag) {
        log.info("Creating new tag -> {}", tag.tag());
        var tagEntity = Tag.builder().value(tag.tag()).build();

        return tagRepository.save(tagEntity);
    }

    public PageDto<Tag> getTags(int page, int size) {
        var tagPage = tagRepository.findAll(PageRequest.of(page, size));

        return PageDto.of(tagPage);
    }

    public PageDto<Tag> getTagsByQuery(String query, int page, int size) {
        var foundedTagsPage =
            tagRepository.findAllByValueIsContainingIgnoreCase(
                query,
                PageRequest.of(page, size)
            );

        return PageDto.of(foundedTagsPage);
    }

    public void deleteTag(int tagId) {
        tagRepository.deleteById(tagId);
    }
}

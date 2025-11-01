package com.danyazero.submissionservice.controller;

import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.model.LanguageDto;
import com.danyazero.submissionservice.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/languages")
public class LanguageController {
    private final LanguageRepository languageRepository;

    @GetMapping
    public List<Language> findAll() {
        return languageRepository.findAll();
    }

    @PostMapping
    public Language save(@RequestBody LanguageDto language) {
        return languageRepository.save(language.toEntity());
    }
}

package com.danyazero.submissionservice.controller;

import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.model.LanguageDto;
import com.danyazero.submissionservice.service.LanguageService;
import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/languages")
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping
    public List<Language> findAll() {
        return languageService.getLanguages();
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuthorization")
    public Language save(@RequestBody LanguageDto language) {
        return languageService.createLanguage(language);
    }
}

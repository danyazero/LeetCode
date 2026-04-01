package com.danyazero.submissionservice.controller;

import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.model.AuthenticatedUser;
import com.danyazero.submissionservice.model.LanguageRequestDto;
import com.danyazero.submissionservice.model.LanguageResponseDto;
import com.danyazero.submissionservice.service.LanguageService;
import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/languages")
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuthorization")
    public LanguageResponseDto findAll(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return languageService.getLanguages(user.getId());
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuthorization")
    public Language save(@RequestBody LanguageRequestDto language) {
        return languageService.createLanguage(language);
    }
}

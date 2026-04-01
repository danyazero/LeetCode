package com.danyazero.submissionservice.model;

import com.danyazero.submissionservice.entity.Language;

import java.util.List;

public record LanguageResponseDto(
        Integer lastUsed,
        List<Language> languages
) {
}

package com.danyazero.submissionservice.model;

import com.danyazero.submissionservice.entity.Language;

public record LanguageDto(
        String language
) {
    public Language toEntity() {

        return Language.builder()
                .language(this.language())
                .build();
    }
}

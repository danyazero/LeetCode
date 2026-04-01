package com.danyazero.submissionservice.mapper;

import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.model.LanguageRequestDto;;

public class LanguageMapper {
    public static Language map(LanguageRequestDto languageDto) {
        
        return Language.builder()
                .language(languageDto.language())
                .build();
    }
}
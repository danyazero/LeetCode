package com.danyazero.submissionservice.mapper;

import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.model.LanguageDto;;

public class LanguageMapper {
    public static Language map(LanguageDto languageDto) {
        
        return Language.builder()
                .language(languageDto.language())
                .build();
    }
}
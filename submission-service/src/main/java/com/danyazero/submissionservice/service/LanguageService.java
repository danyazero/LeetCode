package com.danyazero.submissionservice.service;

import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.exception.IllegalRequstArgumentException;
import com.danyazero.submissionservice.exception.RequestException;
import com.danyazero.submissionservice.mapper.LanguageMapper;
import com.danyazero.submissionservice.model.LanguageRequestDto;
import com.danyazero.submissionservice.model.LanguageResponseDto;
import com.danyazero.submissionservice.repository.LanguageRepository;
import java.util.List;
import java.util.UUID;

import com.danyazero.submissionservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final SubmissionRepository submissionRepository;

    public LanguageResponseDto getLanguages(UUID userId) {
        var languages = languageRepository.findAll();

        if (userId == null) {
            return new LanguageResponseDto(
                    null,
                    languages
            );
        }
        var lastUsedLanguage = submissionRepository.findFirstByUserIdIsOrderByCreatedAtDesc(userId);

        return lastUsedLanguage.map(submission -> new LanguageResponseDto(
                submission.getLanguage().getId(),
                languages
        )).orElseGet(() -> new LanguageResponseDto(
                null,
                languages
        ));

    }

    public Language getLanguage(Integer languageId) {
        if (languageId == null) throw new IllegalRequstArgumentException(
            "Cannot provide language entity, because provided language id is null"
        );

        return languageRepository
            .findById(languageId)
            .orElseThrow(() -> languageNotFound(languageId));
    }

    public Language createLanguage(LanguageRequestDto language) {
        if (language == null) throw new IllegalRequstArgumentException(
            "Cannot create new language entity: provided payload is null"
        );
        return languageRepository.save(LanguageMapper.map(language));
    }

    private RequestException languageNotFound(int languageId) {
        return new RequestException(
            "Cannot provide language entity, because language with id " +
                languageId +
                " not found"
        );
    }
}

package com.danyazero.submissionservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.danyazero.submissionservice.entity.Language;
import com.danyazero.submissionservice.exception.IllegalRequstArgumentException;
import com.danyazero.submissionservice.exception.RequestException;
import com.danyazero.submissionservice.model.LanguageDto;
import com.danyazero.submissionservice.repository.LanguageRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LanguageServiceTest {

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private LanguageService languageService;

    private Language language;
    private LanguageDto languageDto;

    @BeforeEach
    void setUp() {
        language = Language.builder()
                .id(1)
                .language("java")
                .build();
        
        languageDto = new LanguageDto("java");
    }

    @Nested
    @DisplayName("Get Languages Tests")
    class GetLanguagesTests {
        @Test
        @DisplayName("Should return a list of all languages when they exist")
        void getLanguages_Success() {
            when(languageRepository.findAll()).thenReturn(List.of(language));

            List<Language> result = languageService.getLanguages();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("java", result.get(0).getLanguage());
            verify(languageRepository).findAll();
        }

        @Test
        @DisplayName("Should return an empty list when no languages exist")
        void getLanguages_Empty() {
            when(languageRepository.findAll()).thenReturn(List.of());

            List<Language> result = languageService.getLanguages();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(languageRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Get Language Tests")
    class GetLanguageTests {
        @Test
        @DisplayName("Should return the language when a valid ID is provided")
        void getLanguage_Success() {
            when(languageRepository.findById(1)).thenReturn(Optional.of(language));

            Language result = languageService.getLanguage(1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("java", result.getLanguage());
            verify(languageRepository).findById(1);
        }

        @Test
        @DisplayName("Should throw IllegalRequstArgumentException when language ID is null")
        void getLanguage_NullId() {
            assertThrows(IllegalRequstArgumentException.class, () -> languageService.getLanguage(null));
            verifyNoInteractions(languageRepository);
        }

        @Test
        @DisplayName("Should throw RequestException when language is not found")
        void getLanguage_NotFound() {
            when(languageRepository.findById(999)).thenReturn(Optional.empty());

            assertThrows(RequestException.class, () -> languageService.getLanguage(999));
            verify(languageRepository).findById(999);
        }
    }

    @Nested
    @DisplayName("Create Language Tests")
    class CreateLanguageTests {
        @Test
        @DisplayName("Should successfully create and return a language when valid DTO is provided")
        void createLanguage_Success() {
            when(languageRepository.save(any(Language.class))).thenReturn(language);

            Language result = languageService.createLanguage(languageDto);

            assertNotNull(result);
            assertEquals("java", result.getLanguage());
            verify(languageRepository).save(any(Language.class));
        }

        @Test
        @DisplayName("Should throw NullPointerException when DTO is null (current behavior)")
        void createLanguage_NullDto() {
            assertThrows(IllegalRequstArgumentException.class, () -> languageService.createLanguage(null));
            verifyNoInteractions(languageRepository);
        }
    }
}

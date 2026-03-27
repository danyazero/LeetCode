package com.danyazero.executorservice.service;

import com.danyazero.executorservice.model.Compiler;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CompilerService {

    private final Map<String, Compiler> submissionExecutor;

    public CompilerService(Collection<Compiler> submissionExecutors) {
        var executors =
            submissionExecutors != null
                ? submissionExecutors
                : Collections.<Compiler>emptyList();
        log.info("Found {} submission executors.", executors.size());

        this.submissionExecutor = executors
            .stream()
            .filter(Objects::nonNull)
            .filter(this::hasLanguage)
            .collect(
                Collectors.toMap(
                    executor -> executor.getLanguage().toLowerCase(),
                    executor -> executor,
                    this::resolveLanguageDuplicate
                )
            );
    }

    public Optional<Compiler> getCompiler(String language) {
        if (language == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(
            submissionExecutor.get(language.toLowerCase())
        );
    }

    private boolean hasLanguage(Compiler executor) {
        if (executor.getLanguage() == null) {
            log.warn(
                "Skipping compiler with null language: {}",
                executor.getClass().getSimpleName()
            );
            return false;
        }
        return true;
    }

    private Compiler resolveLanguageDuplicate(
        Compiler existing,
        Compiler duplicate
    ) {
        log.warn(
            "Duplicate compiler for language '{}', keeping first registered: {}",
            existing.getLanguage(),
            existing.getClass().getSimpleName()
        );
        return existing;
    }
}

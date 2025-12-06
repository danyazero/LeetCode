package com.danyazero.executorservice.service;

import com.danyazero.executorservice.model.Compiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CompilerService {
    private final Map<String, Compiler> submissionExecutor;

    public CompilerService(Collection<Compiler> submissionExecutors) {
        log.info("Found {} submission executors.", submissionExecutors.size());

        this.submissionExecutor = submissionExecutors.stream().collect(
                Collectors.toMap(
                        executor -> executor.getLanguage().toLowerCase(),
                        executor -> executor
                )
        );
    }

    public Optional<Compiler> getCompiler(String language) {
        return Optional.ofNullable(
                submissionExecutor.get(language.toLowerCase())
        );
    }
}

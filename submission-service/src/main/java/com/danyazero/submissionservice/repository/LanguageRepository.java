package com.danyazero.submissionservice.repository;

import com.danyazero.submissionservice.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
}

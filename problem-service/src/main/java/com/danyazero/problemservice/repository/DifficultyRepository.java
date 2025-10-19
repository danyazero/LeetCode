package com.danyazero.problemservice.repository;

import com.danyazero.problemservice.entity.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DifficultyRepository extends JpaRepository<Difficulty, Integer> {
}

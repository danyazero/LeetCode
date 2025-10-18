package com.danyazero.problemservice.repository;

import com.danyazero.problemservice.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
}

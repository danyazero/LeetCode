package com.danyazero.problemservice.repository;

import com.danyazero.problemservice.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    Page<Problem> findAll(Specification<Problem> problemSpecification, Pageable pageable);
}

package com.danyazero.problemservice.repository;

import com.danyazero.problemservice.entity.Testcase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestcaseRepository extends JpaRepository<Testcase, Integer> {
    List<Testcase> getAllByProblem_Id(Integer problemId, Pageable pageable);
}

package com.danyazero.problemservice.repository;

import com.danyazero.problemservice.entity.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Testcase, Integer> {
    List<Testcase> getAllByProblem_Id(Integer problemId);
    void deleteAllByProblem_Id(Integer problemId);

    List<Testcase> findByProblem_IdAndIsPublic(Integer problemId, boolean isPublic);
}

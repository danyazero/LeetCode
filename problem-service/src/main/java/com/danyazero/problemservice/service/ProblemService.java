package com.danyazero.problemservice.service;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.model.CreateProblemDto;
import com.danyazero.problemservice.repository.ProblemRepository;
import com.danyazero.problemservice.utils.ProblemSearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;

    @Transactional
    public Problem createProblem(CreateProblemDto problem) {

        return problemRepository.save(problem.toEntity());
    }

    public Page<Problem> findProblems(String query, Integer tag, Integer difficulty, int page, int size) {

        Specification<Problem> spec = Specification.allOf(
                ProblemSearchSpecification.hasDifficulty(difficulty),
                ProblemSearchSpecification.hasTag(tag),
                ProblemSearchSpecification.fullTextSearch(query)
        );

        return problemRepository.findAll(spec, PageRequest.of(page, size));
    }
}

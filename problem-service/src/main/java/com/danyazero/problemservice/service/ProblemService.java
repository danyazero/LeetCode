package com.danyazero.problemservice.service;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.exception.RequestException;
import com.danyazero.problemservice.model.CreateProblemDto;
import com.danyazero.problemservice.model.ProblemResponse;
import com.danyazero.problemservice.model.TestcaseDto;
import com.danyazero.problemservice.repository.ProblemRepository;
import com.danyazero.problemservice.repository.TestcaseRepository;
import com.danyazero.problemservice.utils.ProblemSearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final TestcaseRepository testcaseRepository;

    @Transactional
    public Problem createProblem(CreateProblemDto problem) {

        return problemRepository.save(problem.toEntity());
    }

    public ProblemResponse getProblemById(int problemId) {
        final var problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RequestException("Problem with id " + problemId + " not found."));

        final var publicTestcases = testcaseRepository.findByProblem_IdAndIsPublic(problemId, true)
                .stream()
                .map(TestcaseDto::toDto)
                .toList();

        return ProblemResponse.builder()
                .description(problem.getDescription())
                .difficulty(problem.getDifficulty())
                .testcases(publicTestcases)
                .title(problem.getTitle())
                .id(problem.getId())
                .build();
    }

    @Transactional
    public void deleteProblem(Integer problemId) {
        problemRepository.deleteById(problemId);
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

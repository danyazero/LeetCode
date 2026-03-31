package com.danyazero.problemservice.service;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.exception.IllegalRequstArgumentException;
import com.danyazero.problemservice.exception.RequestException;
import com.danyazero.problemservice.model.CreateProblemDto;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.model.ProblemDto;
import com.danyazero.problemservice.model.ProblemResponse;
import com.danyazero.problemservice.model.TestDto;
import com.danyazero.problemservice.repository.ProblemRepository;
import com.danyazero.problemservice.repository.TestRepository;
import com.danyazero.problemservice.utils.ProblemSearchSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final TestRepository testRepository;

    @Transactional
    public Problem createProblem(CreateProblemDto problem) {
        if (problem == null) {
            log.warn("Cannot create problem, problem payload cannot be null");
            throw new IllegalRequstArgumentException("Problem payload cannot be null");
        }
        return problemRepository.save(problem.toEntity());
    }

    public ProblemResponse getProblemById(int problemId) {
        final var problem = problemRepository
            .findById(problemId)
            .orElseThrow(() -> {
                log.warn("Problem with id {} not found.", problemId);
                return new RequestException(
                    "Problem with id " + problemId + " not found."
                );
            });

        final var publicTests = testRepository
            .findByProblem_IdAndIsPublic(problemId, true)
            .stream()
            .map(TestDto::toDto)
            .toList();

        return ProblemResponse.builder()
            .description(problem.getDescription())
            .difficulty(problem.getDifficulty())
            .title(problem.getTitle())
            .tags(problem.getTags())
            .testcases(publicTests)
            .id(problem.getId())
            .build();
    }

    @Transactional
    public void deleteProblem(Integer problemId) {
        if (problemId == null) {
            log.warn("Cannot delete problem, problem ID cannot be null");
            throw new IllegalRequstArgumentException("Problem ID cannot be null");
        }
        testRepository.deleteAllByProblem_Id(problemId);
        problemRepository.deleteById(problemId);
    }

    public PageDto<ProblemDto> findProblems(
        String query,
        Integer tag,
        Integer difficulty,
        int page,
        int size
    ) {
        if (page < 0) {
            log.warn("Cannot find problems, page index must not be less than zero");
            throw new IllegalRequstArgumentException("Page index must not be less than zero");
        }
        if (size < 1) {
            log.warn("Cannot find problems, page size must not be less than one");
            throw new IllegalRequstArgumentException("Page size must not be less than one");
        }

        Specification<Problem> spec = Specification.allOf(
            ProblemSearchSpecification.hasDifficulty(difficulty),
            ProblemSearchSpecification.hasTag(tag),
            ProblemSearchSpecification.fullTextSearch(query)
        );

        return PageDto.of(
            problemRepository.findAll(spec, PageRequest.of(page, size)),
                ProblemDto::map
        );
    }

    @Transactional
    public void increaseSentSubmissions(Integer problemId) {
        if (problemId == null) {
            log.warn("Can't increase 'sent submissions' counter, problemId  is null.");
            return;
        }
            
        problemRepository
            .findById(problemId)
            .ifPresentOrElse(
                problem -> {
                    problem.setSentSubmissions(
                        problem.getSentSubmissions() + 1
                    );
                    problemRepository.save(problem);
                },
                () ->
                    log.warn(
                        "Can't increase 'sent submissions' counter, problem with id -> {} has not been found.",
                        problemId
                    )
            );
    }

    @Transactional
    public void increaseAcceptedSubmissions(Integer problemId) {
        if (problemId == null) {
            log.warn("Can't increase 'accepted submissions' counter, problemId  is null.");
            return;
        }
        
        problemRepository
            .findById(problemId)
            .ifPresentOrElse(
                problem -> {
                    problem.setAcceptedSubmissions(
                        problem.getAcceptedSubmissions() + 1
                    );
                    problemRepository.save(problem);
                },
                () ->
                    log.warn(
                        "Can't increase 'accepted submissions' counter, problem with id -> {} has not been found.",
                        problemId
                    )
            );
    }
}

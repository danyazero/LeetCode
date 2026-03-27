package com.danyazero.problemservice.service;

import com.danyazero.problemservice.entity.Testcase;
import com.danyazero.problemservice.exception.RequestException;
import com.danyazero.problemservice.model.TestDto;
import com.danyazero.problemservice.repository.ProblemRepository;
import com.danyazero.problemservice.repository.TestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final ProblemRepository problemRepository;

    public Testcase createTest(TestDto test) {
        log.info(
            "Create testcase request for problem with id -> {}",
            test.problemId()
        );
        var problem = problemRepository
            .findById(test.problemId())
            .orElseThrow(() ->
                new RequestException(
                    "Problem row not found by provided problemId."
                )
            );

        return testRepository.save(test.toEntity(problem));
    }

    public List<Testcase> getTestByProblemId(int problemId) {
        return testRepository.getAllByProblem_Id(problemId);
    }

    public void deleteTest(int testId) {
        testRepository.deleteById(testId);
    }
}

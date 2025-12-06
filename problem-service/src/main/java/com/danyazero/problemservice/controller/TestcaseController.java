package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Testcase;
import com.danyazero.problemservice.exception.RequestException;
import com.danyazero.problemservice.model.TestcaseDto;
import com.danyazero.problemservice.repository.ProblemRepository;
import com.danyazero.problemservice.repository.TestcaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/testcases")
public class TestcaseController {
    private final TestcaseRepository testcaseRepository;
    private final ProblemRepository problemRepository;

    @PostMapping
    public Testcase createTestcase(@RequestBody TestcaseDto testcaseDto) {
        var problem = problemRepository.findById(testcaseDto.problemId())
                .orElseThrow(() -> new RequestException("Problem row not found by provided problemId."));

        return testcaseRepository.save(testcaseDto.toEntity(problem));
    }

    @DeleteMapping("/{testcaseId}")
    public void deleteTestcase(@PathVariable Integer testcaseId) {
        testcaseRepository.deleteById(testcaseId);
    }

    @GetMapping("/{problemId}")
    public List<Testcase> getTestcases(@PathVariable Integer problemId) {
        return testcaseRepository.getAllByProblem_Id(problemId);
    }
}
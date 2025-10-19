package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.model.ProblemDto;
import com.danyazero.problemservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {
    private final ProblemRepository repository;

    @GetMapping("/{problemId}")
    public Problem getProblemById(@PathVariable Integer problemId) {
        return repository.findById(problemId).orElseThrow();
    }

    @PostMapping
    public Problem createProblem(@RequestBody ProblemDto problem) {
        return repository.save(problem.toEntity());
    }
}

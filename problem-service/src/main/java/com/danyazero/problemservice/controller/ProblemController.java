package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {
    private final ProblemRepository repository;

    @GetMapping("/{problemId}")
    public Problem getProblemById(@PathVariable Integer problemId) {
        return repository.findById(problemId).orElse(null);
    }
}

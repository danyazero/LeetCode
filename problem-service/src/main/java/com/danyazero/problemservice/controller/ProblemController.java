package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.model.CreateProblemDto;
import com.danyazero.problemservice.repository.ProblemRepository;
import com.danyazero.problemservice.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {
    private final ProblemRepository repository;
    private final ProblemService service;

    @GetMapping("/{problemId}")
    public Problem getProblemById(@PathVariable Integer problemId) {
        return repository.findById(problemId).orElseThrow();
    }

    @GetMapping
    public PageDto<Problem> getProblems(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return PageDto.of(
                repository.findAll(PageRequest.of(page, size))
        );
    }

    @PostMapping
    public Problem createProblem(@RequestBody CreateProblemDto problem) {
        return service.createProblem(problem);
    }
}

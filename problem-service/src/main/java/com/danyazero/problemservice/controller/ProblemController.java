package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.model.CreateProblemDto;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.model.ProblemDto;
import com.danyazero.problemservice.model.ProblemResponse;
import com.danyazero.problemservice.service.ProblemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/{problemId}")
    public ProblemResponse getProblemById(@PathVariable Integer problemId) {
        return problemService.getProblemById(problemId);
    }

    @GetMapping
    public PageDto<ProblemDto> getProblems(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) Integer tag,
        @RequestParam(required = false) Integer difficulty,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return problemService.findProblems(query, tag, difficulty, page, size);
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuthorization")
    public Problem createProblem(@RequestBody CreateProblemDto problem) {
        return problemService.createProblem(problem);
    }

    @DeleteMapping("/{problemId}")
    @SecurityRequirement(name = "bearerAuthorization")
    public void deleteProblem(@PathVariable Integer problemId) {
        problemService.deleteProblem(problemId);
    }
}

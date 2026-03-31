package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Testcase;
import com.danyazero.problemservice.model.TestDto;
import com.danyazero.problemservice.service.TestService;
import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/testcases")
public class TestController {

    private final TestService testService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuthorization")
    public Testcase createTest(@RequestBody TestDto testDto) {
        return testService.createTest(testDto);
    }

    @DeleteMapping("/{testId}")
    @SecurityRequirement(name = "bearerAuthorization")
    public void deleteTest(@PathVariable Integer testId) {
        testService.deleteTest(testId);
    }

    @GetMapping("/{problemId}")
    @SecurityRequirement(name = "bearerAuthorization")
    public List<Testcase> getTests(@PathVariable Integer problemId) {
        return testService.getTestByProblemId(problemId);
    }
}

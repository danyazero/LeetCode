package com.danyazero.submissionservice.controller;

import com.danyazero.submissionservice.model.AuthenticatedUser;
import com.danyazero.submissionservice.model.PageDto;
import com.danyazero.submissionservice.model.ProblemStatus;
import com.danyazero.submissionservice.model.SubmissionResponseDto;
import com.danyazero.submissionservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/problems")
public class ProblemController {

    private final SubmissionService submissionService;

    @GetMapping("/{problemId}/status")
    public ProblemStatus getProblemStatus(
        @PathVariable Integer problemId,
        @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return submissionService.getProblemStatus(problemId);
    }

    @GetMapping("/{problemId}")
    public PageDto<SubmissionResponseDto> getProblemSubmissions(
        @PathVariable Integer problemId,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "5") Integer size,
        @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return submissionService.getSubmissions(
            problemId,
            user.getId(),
            PageRequest.of(page, size)
        );
    }
}

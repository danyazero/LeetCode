package com.danyazero.submissionservice.controller;

import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.model.AuthenticatedUser;
import com.danyazero.submissionservice.model.SubmissionDto;
import com.danyazero.submissionservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping("/{submissionId}")
    public Submission findById(@PathVariable Integer submissionId) {
        return submissionService.findBySubmissionId(submissionId);
    }

    @PostMapping
    public Submission createSubmission(
        @RequestBody SubmissionDto submissionDto,
        @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return submissionService.createSubmission(user.getId(), submissionDto);
    }
}

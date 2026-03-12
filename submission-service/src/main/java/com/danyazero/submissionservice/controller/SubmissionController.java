package com.danyazero.submissionservice.controller;

import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.model.PageDto;
import com.danyazero.submissionservice.model.SubmissionDto;
import com.danyazero.submissionservice.model.SubmissionsResponse;
import com.danyazero.submissionservice.service.SubmissionService;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

    @GetMapping("/problems/{problemId}")
    public SubmissionsResponse getProblemSubmissions(
        @PathVariable Integer problemId,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "5") Integer size,
        Principal principal
    ) {
        var userId = UUID.fromString(principal.getName());

        return submissionService.findByProblemId(
            problemId,
            userId,
            PageRequest.of(page, size)
        );
    }

    @PostMapping
    public Submission createSubmission(
        @RequestBody SubmissionDto submissionDto,
        JwtAuthenticationToken authenticationToken
    ) {
        log.info(
            "Create submission request from user {}",
            authenticationToken.getToken().getClaimAsString("uid")
        );
        var userId = UUID.fromString(
            authenticationToken.getToken().getClaimAsString("uid")
        );

        return submissionService.createSubmission(userId, submissionDto);
    }
}

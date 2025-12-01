package com.danyazero.submissionservice.controller;

import com.danyazero.submissionservice.entity.Submission;
import com.danyazero.submissionservice.exception.IdempotencyKeyException;
import com.danyazero.submissionservice.exception.RequestException;
import com.danyazero.submissionservice.model.PageDto;
import com.danyazero.submissionservice.model.SubmissionDto;
import com.danyazero.submissionservice.service.SubmissionService;
import jakarta.ws.rs.HeaderParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/submissions")
public class SubmissionController {
    private final SubmissionService submissionService;

    @GetMapping("/{submissionId}")
    public Submission findById(@PathVariable Integer submissionId) {
        return submissionService.findBySubmissionId(submissionId);
    }

    @GetMapping("/problem/{problemId}")
    public PageDto<Submission> getProblemSubmissions(
            @PathVariable Integer problemId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            Principal principal
    ) {
        var userId = UUID.fromString(principal.getName());

        return PageDto.of(
                submissionService.findByProblemId(problemId, userId, PageRequest.of(page, size))
        );
    }

    @PostMapping
    public Submission createSubmission(
            @RequestBody SubmissionDto submissionDto,
            Principal principal
    ) {
        var userId = UUID.fromString(principal.getName());

        return submissionService.createSubmission(userId, submissionDto);
    }
}

package com.danyazero.submissionservice.client;

import com.danyazero.submissionservice.model.Problem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "problem")
public interface ProblemClient {

    @GetMapping("/api/v1/problems/{problemId}")
    Problem getProblemById(@PathVariable Integer problemId);
}

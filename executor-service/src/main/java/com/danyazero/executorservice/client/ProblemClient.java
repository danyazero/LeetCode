package com.danyazero.executorservice.client;

import com.danyazero.executorservice.model.TestcaseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "problem")
public interface ProblemClient {

    @GetMapping("/api/v1/testcases/{problemId}")
    List<TestcaseDto> getProblemTestcases(@PathVariable Integer problemId);
}

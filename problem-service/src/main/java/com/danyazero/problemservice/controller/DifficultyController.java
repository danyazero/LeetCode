package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Difficulty;
import com.danyazero.problemservice.model.DifficultyDto;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.service.DifficultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/difficulties")
public class DifficultyController {

    private final DifficultyService difficultyService;

    @PostMapping
    public Difficulty createDifficulty(
        @RequestBody DifficultyDto difficultyDto
    ) {
        return difficultyService.createDifficulty(difficultyDto);
    }

    @GetMapping
    public PageDto<Difficulty> findAll(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return difficultyService.getDifficulties(page, size);
    }

    @DeleteMapping("/{difficultyId}")
    public void deleteById(@PathVariable int difficultyId) {
        difficultyService.deleteDifficulty(difficultyId);
    }
}

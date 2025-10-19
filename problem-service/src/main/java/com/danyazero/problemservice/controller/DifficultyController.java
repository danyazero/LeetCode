package com.danyazero.problemservice.controller;

import com.danyazero.problemservice.entity.Difficulty;
import com.danyazero.problemservice.model.DifficultyDto;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.repository.DifficultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/difficulties")
public class DifficultyController {
    private final DifficultyRepository difficultyRepository;

    @PostMapping
    public Difficulty createDifficulty(@RequestBody DifficultyDto difficultyDto) {
        var difficultyEntity = Difficulty.builder()
                .value(difficultyDto.difficulty())
                .build();

        return difficultyRepository.save(difficultyEntity);
    }

    @GetMapping
    public PageDto<Difficulty> findAll(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        var difficultyPage = difficultyRepository.findAll(PageRequest.of(page, size));

        return PageDto.of(difficultyPage);
    }

    @DeleteMapping("/{difficultyId}")
    public void deleteById(@PathVariable int difficultyId) {
        difficultyRepository.deleteById(difficultyId);
    }
}

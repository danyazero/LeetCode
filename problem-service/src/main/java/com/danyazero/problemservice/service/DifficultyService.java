package com.danyazero.problemservice.service;

import com.danyazero.problemservice.entity.Difficulty;
import com.danyazero.problemservice.model.DifficultyDto;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.repository.DifficultyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DifficultyService {

    private final DifficultyRepository difficultyRepository;

    public Difficulty createDifficulty(DifficultyDto difficulty) {
        var difficultyEntity = Difficulty.builder()
            .value(difficulty.difficulty())
            .build();

        return difficultyRepository.save(difficultyEntity);
    }

    public PageDto<Difficulty> getDifficulties(int page, int size) {
        var difficultyPage = difficultyRepository.findAll(
            PageRequest.of(page, size)
        );

        return PageDto.of(difficultyPage);
    }
    
    public void deleteDifficulty(int difficultyId) {
        difficultyRepository.deleteById(difficultyId);
    }
}

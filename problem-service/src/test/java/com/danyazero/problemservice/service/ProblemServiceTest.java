package com.danyazero.problemservice.service;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.exception.IllegalRequstArgumentException;
import com.danyazero.problemservice.exception.RequestException;
import com.danyazero.problemservice.model.CreateProblemDto;
import com.danyazero.problemservice.model.PageDto;
import com.danyazero.problemservice.model.ProblemDto;
import com.danyazero.problemservice.model.ProblemResponse;
import com.danyazero.problemservice.repository.ProblemRepository;
import com.danyazero.problemservice.repository.TestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private TestRepository testRepository;

    @InjectMocks
    private ProblemService problemService;

    @Test
    @DisplayName("Should throw IllegalRequstArgumentException when payload is null")
    void createProblem_shouldThrowWhenNull() {
        assertThrows(IllegalRequstArgumentException.class, () -> problemService.createProblem(null));
    }

    @Test
    @DisplayName("Should successfully save entity and return when payload is valid")
    void createProblem_shouldSaveAndReturnProblem() {
        CreateProblemDto dto = mock(CreateProblemDto.class);
        Problem entity = mock(Problem.class);
        when(dto.toEntity()).thenReturn(entity);
        when(problemRepository.save(entity)).thenReturn(entity);

        Problem result = problemService.createProblem(dto);
        assertEquals(entity, result);
        verify(problemRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw RequestException when problem is not found by ID")
    void getProblemById_shouldThrowWhenNotFound() {
        when(problemRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RequestException.class, () -> problemService.getProblemById(1));
    }

    @Test
    @DisplayName("Should return ProblemResponse with mapped data and public testcases")
    void getProblemById_shouldReturnResponse() {
        Problem problem = mock(Problem.class);
        when(problem.getDescription()).thenReturn("desc");
        when(problem.getDifficulty()).thenReturn(null);
        when(problem.getTitle()).thenReturn("title");
        when(problem.getTags()).thenReturn(Collections.emptySet());
        when(problem.getId()).thenReturn(1);

        when(problemRepository.findById(1)).thenReturn(Optional.of(problem));
        when(testRepository.findByProblem_IdAndIsPublic(1, true)).thenReturn(Collections.emptyList());

        ProblemResponse response = problemService.getProblemById(1);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should throw IllegalRequstArgumentException when problem ID is null that is to be deleted")
    void deleteProblem_shouldThrowWhenNull() {
        assertThrows(IllegalRequstArgumentException.class, () -> problemService.deleteProblem(null));
    }

    @Test
    @DisplayName("Should wipe child tests then optionally delete the problem entity successfully")
    void deleteProblem_shouldDelete() {
        problemService.deleteProblem(1);
        verify(testRepository).deleteAllByProblem_Id(1);
        verify(problemRepository).deleteById(1);
    }

    @Test
    @DisplayName("Should throw IllegalRequstArgumentException when pagination page index is negative")
    void findProblems_shouldThrowWhenPageIsNegative() {
        assertThrows(IllegalRequstArgumentException.class, () -> problemService.findProblems("query", 1, 1, -1, 10));
    }

    @Test
    @DisplayName("Should throw IllegalRequstArgumentException when pagination page size is less than 1")
    void findProblems_shouldThrowWhenSizeIsLessThanOne() {
        assertThrows(IllegalRequstArgumentException.class, () -> problemService.findProblems("query", 1, 1, 0, 0));
    }

    @Test
    @DisplayName("Should correctly pass PageRequest and successfully map non-empty Problem list to PageDto")
    void findProblems_shouldReturnPageDto() {
        Problem problem = mock(Problem.class);
        lenient().when(problem.getDifficulty()).thenReturn(null);
        lenient().when(problem.getTags()).thenReturn(Collections.emptySet());
        
        Page<Problem> page = new PageImpl<>(List.of(problem), PageRequest.of(0, 10), 1);
        
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(problemRepository.findAll(any(Specification.class), pageableCaptor.capture())).thenReturn(page);

        PageDto<ProblemDto> result = problemService.findProblems("query", 1, 1, 0, 10);
        
        assertNotNull(result);
        assertEquals(1, result.content().size()); 
        assertEquals(0, pageableCaptor.getValue().getPageNumber()); 
        assertEquals(10, pageableCaptor.getValue().getPageSize());
    }

    @Test
    @DisplayName("Should log a warning and return early when sent submissions problem ID is null")
    void increaseSentSubmissions_shouldLogWarnWhenProblemIdIsNull() {
        problemService.increaseSentSubmissions(null);
        verify(problemRepository, never()).findById(any());
        verify(problemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should log a warning and skip save when sent submissions problem is missing")
    void increaseSentSubmissions_shouldLogWarnWhenNotFound() {
        when(problemRepository.findById(1)).thenReturn(Optional.empty());
        problemService.increaseSentSubmissions(1);
        verify(problemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should increment sent submissions field and persist back to DB")
    void increaseSentSubmissions_shouldIncreaseAndSave() {
        Problem problem = mock(Problem.class);
        when(problem.getSentSubmissions()).thenReturn(5);
        when(problemRepository.findById(1)).thenReturn(Optional.of(problem));

        problemService.increaseSentSubmissions(1);
        verify(problem).setSentSubmissions(6);
        verify(problemRepository).save(problem);
    }

    @Test
    @DisplayName("Should log a warning and return early when accepted submissions problem ID is null")
    void increaseAcceptedSubmissions_shouldLogWarnWhenProblemIdIsNull() {
        problemService.increaseAcceptedSubmissions(null);
        verify(problemRepository, never()).findById(any());
        verify(problemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should log a warning and skip save when accepted submissions problem is missing")
    void increaseAcceptedSubmissions_shouldLogWarnWhenNotFound() {
        when(problemRepository.findById(1)).thenReturn(Optional.empty());
        problemService.increaseAcceptedSubmissions(1);
        verify(problemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should increment accepted submissions field and persist back to DB")
    void increaseAcceptedSubmissions_shouldIncreaseAndSave() {
        Problem problem = mock(Problem.class);
        when(problem.getAcceptedSubmissions()).thenReturn(5);
        when(problemRepository.findById(1)).thenReturn(Optional.of(problem));

        problemService.increaseAcceptedSubmissions(1);
        verify(problem).setAcceptedSubmissions(6);
        verify(problemRepository).save(problem);
    }
}

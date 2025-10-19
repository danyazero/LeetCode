package com.danyazero.problemservice.service;

import com.danyazero.problemservice.entity.Problem;
import com.danyazero.problemservice.model.CreateProblemDto;
import com.danyazero.problemservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;

    @Transactional
    public Problem createProblem(CreateProblemDto problem) {

        return problemRepository.save(problem.toEntity());
    }
}

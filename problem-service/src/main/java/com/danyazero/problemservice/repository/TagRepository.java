package com.danyazero.problemservice.repository;

import com.danyazero.problemservice.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Page<Tag> findAllByTagIsContainingIgnoreCase(String tag, Pageable pageable);
}

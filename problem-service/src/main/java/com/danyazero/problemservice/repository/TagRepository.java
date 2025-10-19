package com.danyazero.problemservice.repository;

import com.danyazero.problemservice.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Page<Tag> findAllByValueIsContainingIgnoreCase(String tag, Pageable pageable);
}

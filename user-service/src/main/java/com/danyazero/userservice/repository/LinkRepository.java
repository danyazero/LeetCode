package com.danyazero.userservice.repository;

import com.danyazero.userservice.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, Integer> {
    @Transactional
    void deleteLinkByIdAndUser_Id(Integer id, UUID userId);
}

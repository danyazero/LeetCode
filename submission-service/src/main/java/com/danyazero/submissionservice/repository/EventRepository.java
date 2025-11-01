package com.danyazero.submissionservice.repository;

import com.danyazero.submissionservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}
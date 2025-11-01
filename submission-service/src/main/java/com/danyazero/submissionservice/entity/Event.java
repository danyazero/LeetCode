package com.danyazero.submissionservice.entity;

import com.danyazero.submissionservice.model.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Column(name = "status", nullable = false, length = Integer.MAX_VALUE)
    private SubmissionStatus status;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
package com.danyazero.problemservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Builder
@Table(name = "problem")
@NoArgsConstructor
@AllArgsConstructor
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    private String title;

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "difficulty_id", nullable = false)
    private Difficulty difficulty;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "problem_tag",
        joinColumns = @JoinColumn(name = "problem_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @Column(name = "sent_submissions", nullable = false)
    private Integer sentSubmissions;

    @Column(name = "accepted_submissions", nullable = false)
    private Integer acceptedSubmissions;

    public Double getAcceptanceRate() {
        if (this.sentSubmissions == null || this.acceptedSubmissions == null) {
            return 0.0;
        }
        if (this.sentSubmissions <= 0 || this.acceptedSubmissions < 0) {
            return 0.0;
        }

        final double validAcceptedSubmissions = Math.min(
            this.acceptedSubmissions.doubleValue(),
            this.sentSubmissions.doubleValue()
        );

        return (
            (validAcceptedSubmissions / this.sentSubmissions.doubleValue()) *
            100.0
        );
    }
}

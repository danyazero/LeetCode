package com.danyazero.problemservice.utils;

import com.danyazero.problemservice.entity.Problem;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ProblemSearchSpecification {
    public static Specification<Problem> hasDifficulty(Integer difficultyId) {
        return (root, query, cb) -> {
            if (difficultyId == null) return null;
            return cb.equal(root.get("difficulty").get("id"), difficultyId);
        };
    }

    public static Specification<Problem> hasTag(Integer tagId) {
        return (root, query, cb) -> {
            if (tagId == null) return null;
            var tags = root.join("tags", JoinType.LEFT);
            return cb.equal(tags.get("id"), tagId);
        };
    }

    public static Specification<Problem> fullTextSearch(String queryText) {
        if (queryText == null || queryText.isBlank()) return null;

        return (root, query, cb) -> cb.isTrue(
                cb.function(
                        "tsvector_match",
                        Boolean.class,
                        cb.function(
                                "to_tsvector",
                                String.class,
                                cb.literal("english"),
                                cb.concat(root.get("title"), cb.concat(" ", root.get("description")))
                        ),
                        cb.function(
                                "plainto_tsquery",
                                String.class,
                                cb.literal("english"),
                                cb.literal(queryText)
                        )
                )
        );
    }
}

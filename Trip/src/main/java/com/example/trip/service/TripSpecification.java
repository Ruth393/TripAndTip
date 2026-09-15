package com.example.trip.service;

import com.example.trip.model.Difficulty;
import com.example.trip.model.Trip;
import org.springframework.data.jpa.domain.Specification;

public class TripSpecification {

    public static Specification<Trip> withFilters(
            Long categoryId,
            Difficulty difficulty,
            Boolean kidFriendly,
            Double minCost,
            Double maxCost,
            String keyword
    ) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (categoryId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("category").get("id"), categoryId));
            }
            if (difficulty != null) {
                predicates = cb.and(predicates, cb.equal(root.get("difficulty"), difficulty));
            }
            if (kidFriendly != null) {
                predicates = cb.and(predicates, cb.equal(root.get("kidFriendly"), kidFriendly));
            }
            if (minCost != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("costAmount"), minCost));
            }
            if (maxCost != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("costAmount"), maxCost));
            }
            if (keyword != null && !keyword.isBlank()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("name")), likePattern),
                        cb.like(cb.lower(root.get("description")), likePattern)
                ));
            }

            return predicates;
        };
    }
}
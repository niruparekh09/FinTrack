package com.fintrack.api.transaction;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> filterBy(
            Long userId,
            TransactionType type,      // can be null
            String category,           // can be null
            Integer month              // can be null
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ALWAYS added — security! User can only see their own data
            predicates.add(cb.equal(root.get("userId"), userId));
            // SQL: WHERE user_id = 5

            // Only added if user sent ?type=EXPENSE
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
                // SQL: AND type = 'EXPENSE'
            }

            // Only added if user sent ?category=Food
            if (category != null && category.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("category")),    // convert column to lowercase
                                "%" + category.toLowerCase() + "%" // partial match
                        )
                );
                // SQL: AND LOWER(category) LIKE '%food%'
                // This matches: "Food", "food", "Fast Food", "FOOD items"
            }

            // Only added if user sent ?month=5
            if (month != null && (12 >= month && month >= 1)) {

                LocalDate start = LocalDate.of(
                        LocalDate.now().getYear(),
                        month,
                        1
                );
                // start = 2025-05-01

                LocalDate end = start.plusMonths(1);
                // start = 2025-06-01

                predicates.add(cb.between(root.get("date"), start, end));
                // SQL: AND date BETWEEN '2025-05-01' AND '2025-06-01'
            }

            return cb.and(predicates); // Joins all predicates with AND
        };
    }
}

package com.iara.core.entity.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface BaseNamespacedSpecification<T> extends Specification<T> {

    default Specification<T> hasPermission(Set<String> namespaces, Set<String> environments) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(namespaces) && !namespaces.isEmpty()) {
                predicates.add(root.get("namespace").get("name").in(namespaces));
            }

            if (Objects.nonNull(environments) && !environments.isEmpty()) {
                predicates.add(root.get("environment").get("name").in(environments));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

}

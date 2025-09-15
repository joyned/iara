package com.iara.core.entity.specification;

import com.iara.core.entity.Secret;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
public class SecretSpecification implements BaseNamespacedSpecification<Secret> {

    private String id;
    private String name;
    private String namespace;
    private String environment;

    @Override
    public Predicate toPredicate(Root<Secret> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(id)) {
            predicates.add(criteriaBuilder.equal(root.get("id"), id));
        }

        if (StringUtils.isNotBlank(name)) {
            predicates.add(criteriaBuilder.equal(root.get("name"), name));
        }

        if (StringUtils.isNotBlank(namespace)) {
            predicates.add(criteriaBuilder.equal(root.get("namespace").get("id"), namespace));
        }

        if (StringUtils.isNotBlank(environment)) {
            predicates.add(criteriaBuilder.equal(root.get("environment").get("id"), environment));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    public Specification<Secret> hasNamespaceIn(Set<String> namespaces) {
        return (root, query, cb) -> {
            if (namespaces == null || namespaces.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("namespace").get("name").in(namespaces);
        };
    }

    public Specification<Secret> hasEnvironmentIn(Set<String> environments) {
        return (root, query, cb) -> {
            if (environments == null || environments.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("environment").get("name").in(environments);
        };
    }
}

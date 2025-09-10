package com.iara.core.entity.specification;

import com.iara.core.entity.Environment;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Builder
public class EnvironmentSpecification implements Specification<Environment> {

    private String id;
    private String name;
    private String namespace;

    @Override
    public Predicate toPredicate(Root<Environment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
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

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}

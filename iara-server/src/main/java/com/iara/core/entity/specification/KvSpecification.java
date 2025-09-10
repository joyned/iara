package com.iara.core.entity.specification;

import com.iara.core.entity.Kv;
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
public class KvSpecification implements Specification<Kv> {

    private String id;
    private String key;
    private String environment;
    private String namespace;

    @Override
    public Predicate toPredicate(Root<Kv> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(id)) {
            predicates.add(criteriaBuilder.equal(root.get("id"), id));
        }

        if (StringUtils.isNotBlank(key)) {
            predicates.add(criteriaBuilder.equal(root.get("key"), key));
        }

        if (StringUtils.isNotBlank(environment)) {
            predicates.add(criteriaBuilder.equal(root.get("environment").get("id"), environment));
        }

        if (StringUtils.isNotBlank(namespace)) {
            predicates.add(criteriaBuilder.equal(root.get("namespace").get("id"), namespace));
        }


        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));

    }
}

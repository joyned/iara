package com.iara.core.entity.specification;

import com.iara.core.entity.Kv;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class KvSpecification implements BaseNamespacedSpecification<Kv> {

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

        if (StringUtils.isNotBlank(namespace)) {
            predicates.add(criteriaBuilder.equal(root.get("namespace").get("id"), namespace));
        }

        if (StringUtils.isNotBlank(environment)) {
            predicates.add(criteriaBuilder.equal(root.get("environment").get("id"), environment));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

}

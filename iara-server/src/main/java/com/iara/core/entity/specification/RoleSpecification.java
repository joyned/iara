package com.iara.core.entity.specification;

import com.iara.core.entity.Role;
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
public class RoleSpecification implements Specification<Role> {

    private String id;
    private String name;


    @Override
    public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(id)) {
            predicates.add(criteriaBuilder.equal(root.get("id"), id));
        }

        if (StringUtils.isNotBlank(name)) {
            predicates.add(criteriaBuilder.equal(root.get("name"), name));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}

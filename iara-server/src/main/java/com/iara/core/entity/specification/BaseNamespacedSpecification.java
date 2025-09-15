package com.iara.core.entity.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public interface BaseNamespacedSpecification<T> extends Specification<T> {

    Specification<T> hasNamespaceIn(Set<String> namespaces);

    Specification<T> hasEnvironmentIn(Set<String> environment);

}

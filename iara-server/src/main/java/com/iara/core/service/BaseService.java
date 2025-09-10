package com.iara.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BaseService<T> {

    Page<T> search(Specification<T> spec, Pageable pageable);

    T persist(T entity);

    void delete(String id);
}

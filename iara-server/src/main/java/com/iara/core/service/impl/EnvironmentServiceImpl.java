package com.iara.core.service.impl;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Namespace;
import com.iara.core.exception.OperationNotPermittedException;
import com.iara.core.exception.RequiredParameterException;
import com.iara.core.repository.EnvironmentRepository;
import com.iara.core.service.EnvironmentService;
import com.iara.core.service.PolicyExecutorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EnvironmentServiceImpl implements EnvironmentService {

    private final EnvironmentRepository repository;
    private final PolicyExecutorService policyExecutorService;

    @Override
    public Page<Environment> search(Specification<Environment> spec, Pageable pageable) {
        spec = policyExecutorService.buildNamespacedSpec(spec);
        return repository.findAll(spec, pageable);
    }

    @Override
    public Environment persist(Environment entity) {
        if (StringUtils.isBlank(entity.getName())) {
            throw new RequiredParameterException("Environment name cannot be null/empty.");
        }

        if (Objects.isNull(entity.getNamespace())) {
            throw new RequiredParameterException("Environment should be related with a Namespace");
        }

        return repository.save(entity);
    }

    @Override
    public void delete(String id) {
        throw new OperationNotPermittedException("You cannot delete an Environment from here.");
    }

    @Override
    public List<Environment> findByNamespace(Namespace namespace) {
        return repository.findByNamespace(namespace);
    }

    @Override
    public void deleteAll(List<Environment> environments) {
        repository.deleteAll(environments);
    }
}

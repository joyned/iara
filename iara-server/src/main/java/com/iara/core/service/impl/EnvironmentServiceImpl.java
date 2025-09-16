package com.iara.core.service.impl;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Namespace;
import com.iara.core.exception.OperationNotPermittedException;
import com.iara.core.repository.EnvironmentRepository;
import com.iara.core.service.EnvironmentService;
import com.iara.core.service.PolicyExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnvironmentServiceImpl implements EnvironmentService {

    private final EnvironmentRepository repository;
    private final PolicyExecutorService policyExecutorService;

    @Override
    public Page<Environment> search(Specification<Environment> spec, Pageable pageable) {
        Page<Environment> result = repository.findAll(spec, pageable);
        List<Environment> resultFiltered = result.stream().filter(policyExecutorService::hasPermissionAtEnvironment).toList();
        return new PageImpl<>(resultFiltered, result.getPageable(), resultFiltered.size());
    }

    @Override
    public Environment persist(Environment entity) {
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

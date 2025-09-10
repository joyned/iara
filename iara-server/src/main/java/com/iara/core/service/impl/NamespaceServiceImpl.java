package com.iara.core.service.impl;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Namespace;
import com.iara.core.exception.NamespaceNotFoundException;
import com.iara.core.repository.NamespaceRepository;
import com.iara.core.service.EnvironmentService;
import com.iara.core.service.NamespaceService;
import com.iara.core.service.PolicyExecutorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NamespaceServiceImpl implements NamespaceService {

    private final NamespaceRepository repository;
    private final EnvironmentService environmentService;
    private final PolicyExecutorService policyExecutorService;

    @Override
    public Page<Namespace> search(Specification<Namespace> spec, Pageable pageable) {
        Page<Namespace> result = repository.findAll(spec, pageable);
        List<Namespace> resultFiltered = result.stream().filter(policyExecutorService::hasPermissionAtNamespace).toList();
        return new PageImpl<>(resultFiltered, result.getPageable(), resultFiltered.size());
    }

    @Override
    @Transactional
    public Namespace persist(Namespace entity) {
        if (StringUtils.isNotBlank(entity.getId())) {
            Optional<Namespace> actualOptional = repository.findById(entity.getId());
            if (actualOptional.isPresent()) {
                Namespace actual = actualOptional.get();
                actual.setEnvironments(environmentService.findByNamespace(actual));
                List<Environment> toDelete = actual.getEnvironments().stream().filter(environment ->
                                entity.getEnvironments().stream().filter(env ->
                                        env.getId().equals(environment.getId())).findFirst().isEmpty())
                        .toList();
                environmentService.deleteAll(toDelete);
            }
        }

        Namespace persisted = repository.save(entity);
        entity.getEnvironments().forEach(environment -> {
            environment.setNamespace(persisted);
            environmentService.persist(environment);
        });

        persisted.setEnvironments(environmentService.findByNamespace(persisted));
        return persisted;
    }

    @Override
    @Transactional
    public void delete(String id) {
        Optional<Namespace> actualOptional = repository.findById(id);
        if (actualOptional.isPresent()) {
            List<Environment> toDelete = environmentService.findByNamespace(actualOptional.get());
            environmentService.deleteAll(toDelete);
            repository.delete(actualOptional.get());
        } else {
            throw new NamespaceNotFoundException("Namespace with ID %s not found.", id);
        }
    }
}

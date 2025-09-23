package com.iara.core.service.impl;

import com.iara.core.entity.ApplicationParams;
import com.iara.core.exception.OperationNotPermittedException;
import com.iara.core.repository.ApplicationParamsRepository;
import com.iara.core.service.ApplicationParamsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationParamsServiceImpl implements ApplicationParamsService {

    private final ApplicationParamsRepository repository;

    @Override
    public ApplicationParams findByKey(String key) {
        Optional<ApplicationParams> optionalApplicationParams = repository.findByKey(key);
        if (optionalApplicationParams.isEmpty()) {
            return null;
        }

        ApplicationParams applicationParams = optionalApplicationParams.get();
        if (applicationParams.getSecure()) {
            applicationParams.setValue(null);
        }

        return applicationParams;
    }

    @Override
    public ApplicationParams findByKeyInternal(String key) {
        return repository.findByKey(key).orElse(null);
    }

    @Override
    public Page<ApplicationParams> search(Specification<ApplicationParams> spec, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ApplicationParams persist(ApplicationParams entity) {
        ApplicationParams persisted = repository.save(entity);
        if (persisted.getSecure()) {
            persisted.setValue(null);
        }
        return persisted;
    }

    @Override
    public void delete(String id) {
        throw new OperationNotPermittedException("You cannot delete params.");
    }
}

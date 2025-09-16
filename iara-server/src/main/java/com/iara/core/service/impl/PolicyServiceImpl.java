package com.iara.core.service.impl;

import com.iara.core.entity.Policy;
import com.iara.core.exception.RequiredParameterException;
import com.iara.core.repository.PolicyRepository;
import com.iara.core.service.PolicyExecutorService;
import com.iara.core.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository repository;
    private final PolicyExecutorService policyExecutorService;

    @Override
    public Page<Policy> search(Specification<Policy> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public Policy persist(Policy entity) {
        if (StringUtils.isBlank(entity.getName()) || StringUtils.isBlank(entity.getRule())) {
            throw new RequiredParameterException("Neither name and/or rule can be empty/null.");
        }

        policyExecutorService.validatePolicyRule(entity.getRule());
        return repository.save(entity);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}

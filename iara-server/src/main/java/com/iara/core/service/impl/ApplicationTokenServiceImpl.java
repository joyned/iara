package com.iara.core.service.impl;

import com.iara.core.entity.ApplicationToken;
import com.iara.core.exception.OperationNotPermittedException;
import com.iara.core.repository.ApplicationTokenRepository;
import com.iara.core.service.ApplicationTokenService;
import com.iara.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationTokenServiceImpl implements ApplicationTokenService {

    private final ApplicationTokenRepository repository;

    @Override
    public Optional<ApplicationToken> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<ApplicationToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public Page<ApplicationToken> search(Specification<ApplicationToken> spec, Pageable pageable) {
        return repository.findAll(pageable).map(applicationToken -> {
            applicationToken.setToken("******");
            return applicationToken;
        });
    }

    @Override
    public ApplicationToken persist(ApplicationToken entity) {
        if (StringUtils.isNotBlank(entity.getId())) {
            throw new OperationNotPermittedException("It's not permitted to update a Application Token.");
        }
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        entity.setToken(PasswordGenerator.generateSecurePasswordWithOutSpecials(150));
        entity.setCreatedBy(userEmail);
        return repository.save(entity);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}

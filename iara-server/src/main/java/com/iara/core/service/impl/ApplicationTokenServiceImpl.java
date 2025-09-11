package com.iara.core.service.impl;

import com.iara.core.entity.ApplicationToken;
import com.iara.core.entity.Policy;
import com.iara.core.entity.User;
import com.iara.core.exception.OperationNotPermittedException;
import com.iara.core.repository.ApplicationTokenRepository;
import com.iara.core.service.ApplicationTokenService;
import com.iara.core.service.UserService;
import com.iara.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationTokenServiceImpl implements ApplicationTokenService {

    private final ApplicationTokenRepository repository;
    private final UserService userService;

    @Override
    public Optional<ApplicationToken> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<ApplicationToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public Page<ApplicationToken> userTokens(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.findByCreatedBy(email, pageable).map(applicationToken -> {
            applicationToken.setToken("******");
            return applicationToken;
        });
    }

    @Override
    @Transactional
    public ApplicationToken persistUserToken(ApplicationToken token) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Policy> policies = new ArrayList<>();
            user.getRoles().forEach(role -> policies.addAll(role.getPolicies()));
            token.setPolicy(policies.getFirst());
            token.setCreatedBy(email);
            token.setToken(PasswordGenerator.generateSecurePasswordWithOutSpecials(150));
            return repository.save(token);
        }
        return null;
    }

    @Override
    public void deleteUserToken(String id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userService.findByEmail(email);
        Optional<ApplicationToken> optionalApplicationToken = repository.findById(id);
        if (optionalApplicationToken.isPresent() && optionalUser.isPresent()) {
            User user = optionalUser.get();
            ApplicationToken token = optionalApplicationToken.get();
            if (token.getCreatedBy().equals(user.getEmail())) {
                delete(token.getId());
            } else {
                throw new OperationNotPermittedException("The token with ID %s does not belong to %s", user.getEmail());
            }
        }
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

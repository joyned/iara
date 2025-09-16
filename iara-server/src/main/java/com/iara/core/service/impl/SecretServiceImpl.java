package com.iara.core.service.impl;

import com.iara.core.entity.Secret;
import com.iara.core.entity.SecretVersion;
import com.iara.core.entity.specification.BaseNamespacedSpecification;
import com.iara.core.exception.DestroyedSecretException;
import com.iara.core.exception.DuplicatedSecretException;
import com.iara.core.exception.SecretNotFoundException;
import com.iara.core.repository.SecretRepository;
import com.iara.core.repository.SecretVersionRepository;
import com.iara.core.service.PolicyExecutorService;
import com.iara.core.service.SecretService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecretServiceImpl implements SecretService {

    private final SecretRepository repository;
    private final SecretVersionRepository secretVersionRepository;
    private final PolicyExecutorService policyExecutorService;

    @Override
    public Page<Secret> search(Specification<Secret> spec, Pageable pageable) {
        spec = policyExecutorService.buildNamespacedSpec(spec);
        return repository.findAll(spec, pageable);
    }

    @Override
    public Secret persist(Secret entity) {
        if (StringUtils.isBlank(entity.getId())) {
            Optional<Secret> existed = repository.findByName(entity.getName());
            if (existed.isPresent()) {
                throw new DuplicatedSecretException("There is a secret with %s name. Please, change the name.", entity.getName());
            }
        }
        Secret persisted = repository.save(entity);
        entity.getVersions().forEach(version -> {
            version.setSecret(persisted);
            secretVersionRepository.save(version);
        });
        return persisted;
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public String getSecretVersionValue(String secretId, String secretVersionId) {
        Optional<Secret> optionalSecret = repository.findById(secretId);
        if (optionalSecret.isPresent()) {
            SecretVersion version = secretVersionRepository.findByIdAndSecret(secretVersionId, optionalSecret.get());
            if (version.getDestroyed()) {
                throw new DestroyedSecretException("Secret Version #%s is destroyed.", String.valueOf(version.getVersion()));
            }
            return version.getValue();
        } else {
            throw new SecretNotFoundException("Secret with ID %s was not found.", secretId);
        }
    }

    @Override
    @Transactional
    public SecretVersion addVersion(String secretId, SecretVersion entity, Boolean disablePastVersion) {
        Optional<Secret> optionalSecret = repository.findById(secretId);

        if (optionalSecret.isPresent()) {
            entity.setSecret(optionalSecret.get());
            SecretVersion newVersion = secretVersionRepository.save(entity);
            if (Objects.nonNull(disablePastVersion) && disablePastVersion) {
                List<SecretVersion> pastVersions =
                        secretVersionRepository.findBySecretAndDisabledAndDestroyedOrderByVersionDesc(optionalSecret.get(), false, false);

                pastVersions.forEach(version -> {
                    if (!newVersion.getId().equals(version.getId())) {
                        version.setDisabled(true);
                        secretVersionRepository.save(version);
                    }
                });
            }
            return newVersion;
        } else {
            throw new SecretNotFoundException("Secret with ID %s was not found.", secretId);
        }
    }

    @Override
    public void disableSecretVersion(String secretId, Integer secretVersion) {
        Optional<Secret> optionalSecret = repository.findById(secretId);
        if (optionalSecret.isPresent()) {
            Secret secret = optionalSecret.get();
            SecretVersion version = secretVersionRepository.findByVersionAndSecret(secretVersion, secret);
            version.setDisabled(true);
            secretVersionRepository.save(version);
        } else {
            throw new SecretNotFoundException("Secret with ID %s was not found.", secretId);
        }
    }

    @Override
    public void destroySecretVersion(String secretId, Integer secretVersion) {
        Optional<Secret> optionalSecret = repository.findById(secretId);
        if (optionalSecret.isPresent()) {
            Secret secret = optionalSecret.get();
            SecretVersion version = secretVersionRepository.findByVersionAndSecret(secretVersion, secret);
            version.setDisabled(true);
            version.setDestroyed(true);
            version.setValue("DESTROYED");
            secretVersionRepository.save(version);
        } else {
            throw new SecretNotFoundException("Secret with ID %s was not found.", secretId);
        }
    }
}

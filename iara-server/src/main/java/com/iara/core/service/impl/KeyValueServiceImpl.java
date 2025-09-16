package com.iara.core.service.impl;

import com.iara.core.entity.Kv;
import com.iara.core.entity.KvHistory;
import com.iara.core.entity.specification.BaseNamespacedSpecification;
import com.iara.core.entity.specification.KvSpecification;
import com.iara.core.exception.DuplicatedKvException;
import com.iara.core.exception.KeyValueNotFoundException;
import com.iara.core.exception.RequiredParameterException;
import com.iara.core.repository.KeyValueHistoryRepository;
import com.iara.core.repository.KeyValueRepository;
import com.iara.core.service.KeyValueService;
import com.iara.core.service.PolicyExecutorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeyValueServiceImpl implements KeyValueService {

    private final KeyValueRepository repository;
    private final KeyValueHistoryRepository keyValueHistoryRepository;
    private final PolicyExecutorService policyExecutorService;

    @Override
    public Page<Kv> search(Specification<Kv> spec, Pageable pageable) {
        spec = policyExecutorService.buildNamespacedSpec(spec);
        return repository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public Kv persist(Kv entity) {
        if (StringUtils.isBlank(entity.getKey())) {
            throw new RequiredParameterException("KV key is required to save.");
        }

        if (Objects.isNull(entity.getNamespace()) || Objects.isNull(entity.getEnvironment())) {
            throw new RequiredParameterException("Neither Namespace and/or Environment can be null.");
        }

        if (StringUtils.isBlank(entity.getId())) {
            Optional<Kv> actual = repository.findByKey(entity.getKey());

            if (actual.isPresent()) {
                throw new DuplicatedKvException("The KV with name %s already exists.", entity.getKey());
            }
        }

        if (StringUtils.isNotBlank(entity.getId())) {
            persistHistory(entity);
        }
        return repository.save(entity);
    }

    public void persistHistory(Kv entity) {
        Optional<Kv> optionalKv = repository.findById(entity.getId());
        if (optionalKv.isPresent()) {
            String user = String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            KvHistory kvHistory = new KvHistory();
            kvHistory.setKeyValue(entity);
            kvHistory.setValue(optionalKv.get().getValue());
            kvHistory.setUser(user);
            kvHistory.setUpdatedAt(new Date());
            keyValueHistoryRepository.save(kvHistory);
        }
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<KvHistory> history(String kvId) {
        Optional<Kv> optionalKv = repository.findById(kvId);
        if (optionalKv.isPresent()) {
            return keyValueHistoryRepository.findByKeyValueOrderByUpdatedAtDesc(optionalKv.get());
        }
        throw new KeyValueNotFoundException("K/V of ID %s was not found.", kvId);
    }

    @Override
    public Kv get(String namespace, String environment, String kv) {
        Optional<Kv> optionalKv = repository.findByKeyAndNamespace_NameAndEnvironment_Name(kv, namespace, environment);
        if (optionalKv.isPresent() && policyExecutorService.hasWritePermissionInKV(optionalKv.get())) {
            return optionalKv.get();
        }
        throw new KeyValueNotFoundException("KV %s/%s/%s not found.", namespace, environment, kv);
    }
}

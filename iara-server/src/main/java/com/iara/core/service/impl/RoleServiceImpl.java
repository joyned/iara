package com.iara.core.service.impl;

import com.iara.core.entity.Role;
import com.iara.core.repository.RoleRepository;
import com.iara.core.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    @Override
    public Page<Role> search(Specification<Role> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public Role persist(Role entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}

package com.iara.core.service.impl;

import com.iara.core.entity.Role;
import com.iara.core.exception.DuplicatedRoleException;
import com.iara.core.exception.RequiredParameterException;
import com.iara.core.repository.RoleRepository;
import com.iara.core.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

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
        if (StringUtils.isBlank(entity.getName())) {
            throw new RequiredParameterException("Role name are required. Please, set a name.");
        }

        if (Objects.isNull(entity.getPolicies()) || entity.getPolicies().isEmpty()) {
            throw new RequiredParameterException("Each role should have at least one policy.");
        }

        if (repository.existsByNameAndIdNot(entity.getName(), entity.getId())) {
            throw new DuplicatedRoleException("There is already a role with %s name. Please, change it.", entity.getName());
        }

        return repository.save(entity);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}

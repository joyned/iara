package com.iara.core.repository;

import com.iara.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    boolean existsByNameAndIdNot(String name, String id);
}

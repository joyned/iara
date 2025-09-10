package com.iara.core.repository;

import com.iara.core.entity.Secret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecretRepository extends JpaRepository<Secret, String>, JpaSpecificationExecutor<Secret> {

    Optional<Secret> findByName(String name);
}

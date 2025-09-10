package com.iara.core.repository;

import com.iara.core.entity.Kv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeyValueRepository extends JpaRepository<Kv, String>, JpaSpecificationExecutor<Kv> {
    Optional<Kv> findByKeyAndNamespace_NameAndEnvironment_Name(String kv, String namespace, String environment);

    Optional<Kv> findByKey(String key);
}

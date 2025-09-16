package com.iara.core.repository;

import com.iara.core.entity.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.naming.Name;
import java.util.Optional;

@Repository
public interface NamespaceRepository extends JpaRepository<Namespace, String>, JpaSpecificationExecutor<Namespace> {

    Optional<Namespace> findByName(String name);
}

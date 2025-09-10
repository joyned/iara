package com.iara.core.repository;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, String>, JpaSpecificationExecutor<Environment> {

    List<Environment> findByNamespace(Namespace namespace);

}

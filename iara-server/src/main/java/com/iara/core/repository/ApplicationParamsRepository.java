package com.iara.core.repository;

import com.iara.core.entity.ApplicationParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationParamsRepository extends JpaRepository<ApplicationParams, String> {

    Optional<ApplicationParams> findByKey(String key);
}

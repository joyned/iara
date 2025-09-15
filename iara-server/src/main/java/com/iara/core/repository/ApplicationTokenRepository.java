package com.iara.core.repository;

import com.iara.core.entity.ApplicationToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationTokenRepository extends JpaRepository<ApplicationToken, String> {

    Optional<ApplicationToken> findByToken(String token);

    Page<ApplicationToken> findByCreatedBy(String createdBy, Pageable pageable);

    List<ApplicationToken> findByCreatedBy(String owner);

}

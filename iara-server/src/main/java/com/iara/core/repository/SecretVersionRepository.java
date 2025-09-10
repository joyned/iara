package com.iara.core.repository;

import com.iara.core.entity.Secret;
import com.iara.core.entity.SecretVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecretVersionRepository extends JpaRepository<SecretVersion, String> {

    SecretVersion findByVersionAndSecret(Integer version, Secret secret);

    SecretVersion findByIdAndSecret(String id, Secret secret);

    List<SecretVersion> findBySecretAndDisabledAndDestroyedOrderByVersionDesc(Secret secret, Boolean disable, Boolean destroyed);

}

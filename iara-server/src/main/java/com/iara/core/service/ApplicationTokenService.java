package com.iara.core.service;

import com.iara.core.entity.ApplicationToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ApplicationTokenService extends BaseService<ApplicationToken> {

    Optional<ApplicationToken> findById(String id);

    Optional<ApplicationToken> findByToken(String token);

    Page<ApplicationToken> userTokens(Pageable pageable);

    ApplicationToken persistUserToken(ApplicationToken token);

    void deleteUserToken(String id);
}

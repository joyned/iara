package com.iara.core.service;

import com.iara.core.entity.ApplicationToken;

import java.util.Optional;

public interface ApplicationTokenService extends BaseService<ApplicationToken> {

    Optional<ApplicationToken> findById(String id);

    Optional<ApplicationToken> findByToken(String token);
}

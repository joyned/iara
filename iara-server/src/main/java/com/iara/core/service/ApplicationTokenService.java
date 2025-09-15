package com.iara.core.service;

import com.iara.core.entity.ApplicationToken;
import com.iara.core.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ApplicationTokenService extends BaseService<ApplicationToken> {

    void saveAll(List<ApplicationToken> applicationTokens);

    List<ApplicationToken> findByOwner(String owner);

    Optional<ApplicationToken> findByToken(String token);

    Page<ApplicationToken> userTokens(Pageable pageable);

    ApplicationToken persistUserToken(ApplicationToken token);

    void updateUserTokensPolicies(User user);

    void deleteUserToken(String id);
}

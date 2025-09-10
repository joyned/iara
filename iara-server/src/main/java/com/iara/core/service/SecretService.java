package com.iara.core.service;

import com.iara.core.entity.Secret;
import com.iara.core.entity.SecretVersion;

public interface SecretService extends BaseService<Secret> {

    String getSecretVersionValue(String secretId, String secretVersionId) throws IllegalAccessException;

    SecretVersion addVersion(String secretId, SecretVersion entity, Boolean disablePastVersion);

    void disableSecretVersion(String secretId, Integer secretVersion);

    void destroySecretVersion(String secretId, Integer secretVersion);
}

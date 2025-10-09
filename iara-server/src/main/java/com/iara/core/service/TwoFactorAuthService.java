package com.iara.core.service;

import com.iara.core.entity.User;

public interface TwoFactorAuthService {

    User register(String user);

    boolean verify(String code, String secret);
}

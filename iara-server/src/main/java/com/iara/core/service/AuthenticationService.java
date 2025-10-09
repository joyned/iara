package com.iara.core.service;

import com.iara.core.entity.User;
import com.iara.core.model.Authentication;
import com.iara.core.model.OTPConfig;
import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.Set;

public interface AuthenticationService {

    OTPConfig doLogin(String email, String password, String ip);

    Authentication otpVerify(String code, String session, String ip);

    void doLogout(String token);

    Authentication doLoginGoogleSSO(String codeToken, String redirectUri, String ip);

    Authentication generateToken(String email, Set<String> scopes, User user);

    Claims validateToken(String token);

    Map<String, Object> validateAndGetScopesFromIaraToken(String iaraToken);

    String isGoogleSSOEnabled();
}

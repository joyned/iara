package com.iara.core.service;

import com.iara.core.model.Authentication;
import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.Set;

public interface AuthenticationService {

    Authentication doLogin(String email, String password, String ip);

    void doLogout(String token);

    Authentication doLoginGoogleSSO(String codeToken, String redirectUri, String ip);

    Authentication generateToken(String email, Set<String> scopes);

    Claims validateToken(String token);

    Map<String, Object> validateAndGetScopesFromIaraToken(String iaraToken);

    String isGoogleSSOEnabled();
}

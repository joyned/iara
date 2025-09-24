package com.iara.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iara.core.entity.*;
import com.iara.core.exception.InvalidCredentialsException;
import com.iara.core.exception.InvalidIaraTokenException;
import com.iara.core.exception.InvalidJwtException;
import com.iara.core.model.Authentication;
import com.iara.core.proxy.GoogleProxy;
import com.iara.core.service.ApplicationParamsService;
import com.iara.core.service.ApplicationTokenService;
import com.iara.core.service.AuthenticationService;
import com.iara.core.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    @Value("${auth.jwt.expiration}")
    private long tokenExpiration;

    @Value("${auth.jwt.issuer}")
    private String jwtIssuer;

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ApplicationTokenService applicationTokenService;
    private final ApplicationParamsService applicationParamsService;
    private final GoogleProxy googleProxy;

    @Override
    public Authentication doLogin(String email, String password) {
        Optional<User> optionalUser = userService.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());

            if (!passwordMatch) {
                throw new InvalidCredentialsException("Invalid credentials.");
            }

            Set<String> scopes = convertPoliciesIntoScopes(user);
            return generateToken(email, scopes);
        } else {
            throw new InvalidCredentialsException("Invalid credentials.");
        }
    }

    @Override
    public Authentication doLoginGoogleSSO(String codeToken, String redirectUri) {
        try {
            ApplicationParams clientIdParam = applicationParamsService.findByKey("GOOGLE_SSO_CLIENT_ID");
            ApplicationParams clientSecretParam = applicationParamsService.findByKeyInternal("GOOGLE_SSO_CLIENT_SECRET");
            String googleJwt = googleProxy.exchangeCodeToJwt(clientIdParam.getValue(), clientSecretParam.getValue(), codeToken, redirectUri);
            Map<String, Object> claims = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(googleJwt.split("\\.")[1])), Map.class);
            String email = claims.get("email").toString();
            Optional<User> optionalUser = userService.findByEmail(email);

            if (optionalUser.isEmpty()) {
                throw new InvalidCredentialsException("You are not authorized to use this application.");
            } else {
                User user = optionalUser.get();
                Set<String> scopes = convertPoliciesIntoScopes(user);
                if (StringUtils.isNotBlank((String) claims.get("picture"))) {
                    user.setPicture((String) claims.get("picture"));
                    userService.persist(user);
                }
                return generateToken(email, scopes);
            }
        } catch (Exception e) {
            throw new InvalidCredentialsException("You are not authorized to use this application.");
        }
    }

    @Override
    public Authentication generateToken(String email, Set<String> scopes) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis() + tokenExpiration);
            Date expiration = calendar.getTime();
            String token = Jwts.builder()
                    .issuer(jwtIssuer)
                    .subject(email)
                    .audience()
                    .add(email)
                    .and()
                    .issuedAt(new Date())
                    .expiration(expiration)
                    .claim("scopes", scopes)
                    .signWith(key)
                    .compact();

            return new Authentication(token, tokenExpiration / 1000);
        } catch (Exception e) {
            throw new InvalidCredentialsException("An error occurred while generating the access token. %s", e.getMessage());
        }
    }

    @Override
    public Claims validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            return Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                throw new com.iara.core.exception.ExpiredJwtException("The given JWT is expired. Please, log in again.");
            }
            throw new InvalidJwtException("Invalid JWT. %s", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> validateAndGetScopesFromIaraToken(String iaraToken) {
        Map<String, Object> response = new LinkedHashMap<>();
        Set<String> scopes = new LinkedHashSet<>();
        Optional<ApplicationToken> optionalApplicationToken = applicationTokenService.findByToken(iaraToken);
        if (optionalApplicationToken.isPresent()) {
            ApplicationToken applicationToken = optionalApplicationToken.get();

            if (Objects.nonNull(applicationToken.getExpiresAt()) && new Date().getTime() > applicationToken.getExpiresAt().getTime()) {
                throw new InvalidIaraTokenException("This token is expired.");
            }

            for (Policy policy : applicationToken.getPolicies()) {
                convertPolicyIntoSet(policy, scopes);
            }
            response.put("name", applicationToken.getName());
            response.put("scopes", scopes);
            return response;
        }
        throw new InvalidIaraTokenException("This token is not valid.");
    }

    @Override
    public String isGoogleSSOEnabled() {
        ApplicationParams applicationParams = applicationParamsService.findByKey("GOOGLE_SSO_ENABLED");
        if (Boolean.parseBoolean(applicationParams.getValue())) {
            return applicationParamsService.findByKey("GOOGLE_SSO_CLIENT_ID").getValue();
        }
        return null;
    }

    protected Set<String> convertPoliciesIntoScopes(User user) {
        Set<Role> userRoles = user.getRoles();

        if (Objects.nonNull(userRoles) && !userRoles.isEmpty()) {
            Set<String> scopes = new LinkedHashSet<>();

            for (Role role : userRoles) {
                for (Policy policy : role.getPolicies()) {
                    convertPolicyIntoSet(policy, scopes);
                }
            }
            return scopes;
        }

        return null;
    }

    private static void convertPolicyIntoSet(Policy policy, Set<String> scopes) {
        String[] rules = policy.getRule().split("\n");

        for (String rule : rules) {
            if (StringUtils.isNotBlank(rule)) {
                String[] ruleIndexes = rule.split(" ");
                boolean isAdminPolicy;
                if (ruleIndexes.length == 4) {
                    isAdminPolicy = true;
                } else {
                    isAdminPolicy = ((ruleIndexes[4].equals("AT") || ruleIndexes[2].equals("AT")) && (ruleIndexes[5].startsWith("#") || ruleIndexes[3].startsWith("#")));
                }

                boolean isWrite = false;
                if (isAdminPolicy) {
                    String perm;

                    if (ruleIndexes[3].equals("WRITE")) {
                        perm = "WRITE";
                        isWrite = true;
                    } else {
                        perm = "READ";
                    }

                    int resourceAdminIndex = isWrite ? 5 : 3;
                    String scope = ruleIndexes[resourceAdminIndex] + ":" + perm;
                    scopes.add(scope);
                } else {
                    String perm;

                    if (ruleIndexes[3].equals("WRITE")) {
                        perm = "WRITE";
                        isWrite = true;
                    } else {
                        perm = "READ";
                    }

                    int resourceIndex = isWrite ? 5 : 3;
                    String resource = ruleIndexes[resourceIndex];

                    int namespaceEnvIndex = isWrite ? 7 : 5;
                    String namespaceEnv = ruleIndexes[namespaceEnvIndex];

                    String scope = namespaceEnv + ":" + resource + ":" + perm;
                    scopes.add(scope);
                }
            }
        }
    }
}

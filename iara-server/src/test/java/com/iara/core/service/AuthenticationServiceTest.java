package com.iara.core.service;


import com.iara.config.security.TokenHolder;
import com.iara.core.entity.ApplicationToken;
import com.iara.core.entity.User;
import com.iara.core.exception.InvalidCredentialsException;
import com.iara.core.exception.InvalidIaraTokenException;
import com.iara.core.exception.InvalidJwtException;
import com.iara.core.model.OTPConfig;
import com.iara.core.proxy.GoogleProxy;
import com.iara.core.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
public class AuthenticationServiceTest {

    @Autowired
    AuthenticationServiceImpl authenticationService;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PolicyService policyService;

    @Autowired
    ApplicationTokenService applicationTokenService;

    @Autowired
    TokenHolder tokenHolder;

    @MockitoBean
    GoogleProxy googleProxy;

    @MockitoBean
    ApplicationParamsService applicationParamsService;

    @MockitoBean
    TwoFactorAuthService twoFactorAuthService;

    @BeforeEach
    void init() {
        User user = new User();
        user.setName("Testing User");
        user.setEmail("testing@iara.com");
        user.setPassword("iara");
        user.setRoles(roleService.search(null, Pageable.unpaged()).toSet());
        user.setOtpEnabled(true);
        user.setOtpBase32("123ABCD");
        user.setOtpAuthUrl("topt://");

        userService.persist(user);
    }

    @Test
    void Given_ValidCredentials_ShouldLogin() {
        OTPConfig otpConfig = authenticationService.doLogin("testing@iara.com", "iara", "127.0.0.1");
        assertNotNull(otpConfig);
        assertNotNull(otpConfig.getSession());
    }

    @Test
    void Given_InvalidCredentials_ShouldThrow() {
        assertThrows(InvalidCredentialsException.class, () -> authenticationService.doLogin("testing@iara.com", "wrong", "127.0.0.1"));
    }

    @Test
    void Given_NotExistingUser_ShouldThrow() {
        assertThrows(InvalidCredentialsException.class, () -> authenticationService.doLogin("worng@iara.com", "iara", "127.0.0.1"));
    }

    @Test
    void Given_InvalidToken_ShouldThrow() {
        assertThrows(InvalidJwtException.class, () -> authenticationService.validateToken("aaaaaaaaaaaaaaaaaaaa"));
    }

    @Test
    @WithMockUser(username = "testing@iara.com")
    void Given_ValidIaraToken_ShouldValidate() {
        ApplicationToken applicationToken = createValidToken();

        Map<String, Object> result = authenticationService.validateAndGetScopesFromIaraToken(applicationToken.getToken());
        assertNotNull(result);
        assertNotNull(result.get("name"));
        assertNotNull(result.get("scopes"));
    }

    @Test
    @WithMockUser(username = "testing@iara.com")
    void Given_InvalidIaraToken_ShouldThrow() {
        ApplicationToken applicationToken = createInvalidToken();
        assertThrows(InvalidIaraTokenException.class, () -> authenticationService.validateAndGetScopesFromIaraToken(applicationToken.getToken()));
    }

    @Test
    void Given_NonExistingIaraToken_ShouldThrow() {
        assertThrows(InvalidIaraTokenException.class, () -> authenticationService.validateAndGetScopesFromIaraToken("aaaaaaaa"));
    }

    @Test
    void Given_AnyToken_ShouldLogoutAndNotThrows() {
        assertDoesNotThrow(() -> authenticationService.doLogout("aaaaaaaa"));
    }

    ApplicationToken createValidToken() {
        ApplicationToken token = new ApplicationToken();
        token.setName("T1");
        token.setCreatedBy("system");
        token.setExpiresAt(getDate(20));
        token.setPolicies(policyService.search(null, Pageable.unpaged()).stream().toList());

        return applicationTokenService.persist(token);
    }

    ApplicationToken createInvalidToken() {
        ApplicationToken token = new ApplicationToken();
        token.setName("T2");
        token.setCreatedBy("system");
        token.setExpiresAt(getDate(-20));
        token.setPolicies(policyService.search(null, Pageable.unpaged()).stream().toList());

        return applicationTokenService.persist(token);
    }


    Date getDate(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + (hour));
        return calendar.getTime();
    }
}

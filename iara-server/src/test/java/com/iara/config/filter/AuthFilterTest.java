package com.iara.config.filter;

import com.iara.config.security.TokenHolder;
import com.iara.core.exception.InvalidJwtException;
import com.iara.core.service.AuthenticationService;
import com.iara.utils.CookieUtils;
import com.iara.utils.IpUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthFilterTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TokenHolder tokenHolder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private AuthFilter authFilter;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        authFilter = new AuthFilter(authenticationService, tokenHolder);
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        SecurityContextHolder.clearContext();
    }

    @Test
    void Given_ValidTokenInCookies_ShouldAuthenticateSuccessfully() throws Exception {
        String token = "valid-jwt-token";
        String ip = "192.168.1.1";
        Claims claims = mock(Claims.class);
        List<String> scopes = List.of("@default/development:KV:READ", "@default/development:SECRET:READ");

        Cookie cookie = new Cookie(CookieUtils.IARA_TOKEN, token);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(request.getRequestURI()).thenReturn("/v1/secrets");
        when(IpUtils.getIp(request)).thenReturn(ip);
        when(tokenHolder.isBlocked(token)).thenReturn(false);
        when(tokenHolder.getActive(token)).thenReturn(ip);
        when(authenticationService.validateToken(token)).thenReturn(claims);
        when(claims.get("scopes")).thenReturn(scopes);
        when(claims.getSubject()).thenReturn("user@email.com");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@email.com", SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals(2, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
    }

    @Test
    void Given_ValidIaraTokenInHeader_ShouldAuthenticateSuccessfully() throws Exception {
        String iaraToken = "valid-iara-token";
        Map<String, Object> tokenData = Map.of(
                "scopes", Set.of("@default/development:KV:READ"),
                "name", "service-account"
        );

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader(CookieUtils.IARA_TOKEN)).thenReturn(iaraToken);
        when(request.getRequestURI()).thenReturn("/v1/kv");
        when(authenticationService.validateAndGetScopesFromIaraToken(iaraToken)).thenReturn(tokenData);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("service-account", SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals(1, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
    }

    @Test
    void Given_BlockedToken_ShouldReturnUnauthorized() throws Exception {
        String token = "blocked-jwt-token";
        String ip = "192.168.1.1";

        Cookie cookie = new Cookie(CookieUtils.IARA_TOKEN, token);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(request.getRequestURI()).thenReturn("/v1/secrets");
        when(IpUtils.getIp(request)).thenReturn(ip);
        when(tokenHolder.isBlocked(token)).thenReturn(true);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(401);
        assertTrue(responseWriter.toString().contains("This token is invalid"));
    }

    @Test
    void Given_TokenWithDifferentIp_ShouldReturnUnauthorized() throws Exception {
        String token = "valid-jwt-token";
        String requestIp = "192.168.1.1";
        String tokenOwnerIp = "192.168.1.2";

        Cookie cookie = new Cookie(CookieUtils.IARA_TOKEN, token);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(request.getRequestURI()).thenReturn("/v1/secrets");
        when(IpUtils.getIp(request)).thenReturn(requestIp);
        when(tokenHolder.isBlocked(token)).thenReturn(false);
        when(tokenHolder.getActive(token)).thenReturn(tokenOwnerIp);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(403);
        assertTrue(responseWriter.toString().contains("This request was blocked because it is invalid."));
    }

    @Test
    void Given_NoToken_ShouldReturnUnauthorized() throws Exception {
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader(CookieUtils.IARA_TOKEN)).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/v1/secrets");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(401);
        assertTrue(responseWriter.toString().contains("Token not found"));
    }

    @Test
    void Given_InvalidToken_ShouldReturnUnauthorized() throws Exception {
        String token = "invalid-jwt-token";
        String ip = "192.168.1.1";

        Cookie cookie = new Cookie(CookieUtils.IARA_TOKEN, token);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(request.getRequestURI()).thenReturn("/v1/secrets");
        when(IpUtils.getIp(request)).thenReturn(ip);
        when(tokenHolder.isBlocked(token)).thenReturn(false);
        when(tokenHolder.getActive(token)).thenReturn(ip);
        when(authenticationService.validateToken(token)).thenThrow(new InvalidJwtException("Invalid token"));

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(401);
        assertTrue(responseWriter.toString().contains("Invalid token"));
    }

    @Test
    void Given_InvalidIaraToken_ShouldReturnUnauthorized() throws Exception {
        String iaraToken = "invalid-iara-token";

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader(CookieUtils.IARA_TOKEN)).thenReturn(iaraToken);
        when(authenticationService.validateAndGetScopesFromIaraToken(iaraToken))
                .thenThrow(new InvalidJwtException("Invalid iara token"));

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(401);
        assertTrue(responseWriter.toString().contains("Invalid iara token"));
    }

    @Test
    void Given_AuthenticationEndpoint_ShouldNotFilter() {
        when(request.getRequestURI()).thenReturn("/v1/authentication/login");

        boolean result = authFilter.shouldNotFilter(request);

        assertTrue(result);
    }

    @Test
    void Given_NonAuthenticationEndpoint_ShouldFilter() {
        when(request.getRequestURI()).thenReturn("/v1/secrets");

        boolean result = authFilter.shouldNotFilter(request);

        assertFalse(result);
    }

    @Test
    void Given_MultipleCookies_ShouldExtractCorrectToken() {
        String token = "correct-token";
        Cookie tokenCookie = new Cookie(CookieUtils.IARA_TOKEN, token);
        Cookie otherCookie = new Cookie("other-cookie", "value");

        when(request.getCookies()).thenReturn(new Cookie[]{otherCookie, tokenCookie});

        String result = authFilter.getTokenFromCookies(request);

        assertEquals(token, result);
    }

    @Test
    void Given_NoCookies_ShouldReturnNull() {
        when(request.getCookies()).thenReturn(null);

        String result = authFilter.getTokenFromCookies(request);

        assertNull(result);
    }

    @Test
    void Given_EmptyCookiesArray_ShouldReturnNull() {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        String result = authFilter.getTokenFromCookies(request);

        assertNull(result);
    }

    @Test
    void Given_CookiesWithoutIaraToken_ShouldReturnNull() {
        Cookie cookie1 = new Cookie("cookie1", "value1");
        Cookie cookie2 = new Cookie("cookie2", "value2");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie1, cookie2});

        String result = authFilter.getTokenFromCookies(request);

        assertNull(result);
    }

    @Test
    void Given_ExceptionDuringProcessing_ShouldReturnUnauthorized() throws Exception {
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader(CookieUtils.IARA_TOKEN)).thenReturn("token");
        when(request.getRequestURI()).thenReturn("/v1/secrets");
        when(authenticationService.validateAndGetScopesFromIaraToken(anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(401);
        assertTrue(responseWriter.toString().contains("Unexpected error"));
    }

    @Test
    void Given_BaseException_ShouldUseExceptionProperties() throws Exception {
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader(CookieUtils.IARA_TOKEN)).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/v1/secrets");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(401);
        String responseBody = responseWriter.toString();
        assertTrue(responseBody.contains("TOKEN_MISSING"));
        assertTrue(responseBody.contains("Token not found."));
    }

    @Test
    void Given_ValidTokenWithEmptyScopes_ShouldAuthenticateWithNoAuthorities() throws Exception {
        String token = "valid-jwt-token";
        String ip = "192.168.1.1";
        Claims claims = mock(Claims.class);
        List<String> scopes = List.of();

        Cookie cookie = new Cookie(CookieUtils.IARA_TOKEN, token);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(request.getRequestURI()).thenReturn("/v1/secrets");
        when(IpUtils.getIp(request)).thenReturn(ip);
        when(tokenHolder.isBlocked(token)).thenReturn(false);
        when(tokenHolder.getActive(token)).thenReturn(ip);
        when(authenticationService.validateToken(token)).thenReturn(claims);
        when(claims.get("scopes")).thenReturn(scopes);
        when(claims.getSubject()).thenReturn("user@email.com");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@email.com", SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty());
    }

    @Test
    void Given_ValidIaraTokenWithEmptyScopes_ShouldAuthenticateWithNoAuthorities() throws Exception {
        String iaraToken = "valid-iara-token";
        Map<String, Object> tokenData = Map.of(
                "scopes", Set.of(),
                "name", "service-account"
        );

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader(CookieUtils.IARA_TOKEN)).thenReturn(iaraToken);
        when(request.getRequestURI()).thenReturn("/v1/kv");
        when(authenticationService.validateAndGetScopesFromIaraToken(iaraToken)).thenReturn(tokenData);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("service-account", SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty());
    }
}

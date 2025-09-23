package com.iara.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iara.config.GlobalError;
import com.iara.core.exception.BaseException;
import com.iara.core.exception.TokenMissingException;
import com.iara.core.service.AuthenticationService;
import com.iara.utils.CookieUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final AuthenticationService service;

    @Override
    @SuppressWarnings("unchecked")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            String token = getTokenFromCookies(request);
            if (StringUtils.isNotBlank(token)) {
                Claims claims = service.validateToken(token);
                List<String> scopes = (List<String>) claims.get("scopes");
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                List<SimpleGrantedAuthority> simpleGrantedAuthorities = new LinkedList<>();
                scopes.forEach(s -> simpleGrantedAuthorities.add(new SimpleGrantedAuthority(s)));
                securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(claims.getSubject(), token, simpleGrantedAuthorities));
                SecurityContextHolder.setContext(securityContext);

                filterChain.doFilter(request, response);
            } else if (StringUtils.isNotBlank(request.getHeader(CookieUtils.IARA_TOKEN))) {
                String iaraToken = request.getHeader(CookieUtils.IARA_TOKEN);
                Map<String, Object> res = service.validateAndGetScopesFromIaraToken(iaraToken);
                Set<String> scopes = (Set<String>) res.get("scopes");
                String name = res.get("name").toString();
                List<SimpleGrantedAuthority> simpleGrantedAuthorities = new LinkedList<>();
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                scopes.forEach(s -> simpleGrantedAuthorities.add(new SimpleGrantedAuthority(s)));
                securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(name, iaraToken, simpleGrantedAuthorities));
                SecurityContextHolder.setContext(securityContext);

                filterChain.doFilter(request, response);
            } else {
                unauthorized(response, "Token not found.", null);
            }
        } catch (Exception e) {
            log.error("Failed to authenticate.", e);
            unauthorized(response, e.getMessage(), e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/v1/authentication");
    }

    protected void unauthorized(HttpServletResponse response, String message, Throwable e) throws IOException {
        GlobalError globalError;
        if (e instanceof BaseException) {
            globalError = new GlobalError(((BaseException) e).getKey(), e.getMessage(), null, ((BaseException) e).getStatus());
        } else {
            globalError = new GlobalError("UNAUTHORIZED", message, null, 401);
        }
        response.setStatus(globalError.getStatus());
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(globalError));
    }

    protected String getTokenFromCookies(HttpServletRequest request) {
        if (Objects.isNull(request.getCookies())) {
            return null;
        }

        Optional<Cookie> opCookie = Arrays.stream(request.getCookies()).filter(cookie -> CookieUtils.IARA_TOKEN.equals(cookie.getName())).findFirst();

        return opCookie.map(Cookie::getValue).orElse(null);

    }
}


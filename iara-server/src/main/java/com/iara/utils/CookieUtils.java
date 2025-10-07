package com.iara.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
public class CookieUtils {
    public static final String IARA_TOKEN = "Iara-Token";
    public static final String IARA_EXPIRES_IN = "Iara-Expires-In";

    public void createIaraTokenCookies(HttpServletRequest request, HttpServletResponse response, String token, String expiresIn) {
        createCookie(request, response, IARA_TOKEN, token, true, Long.parseLong(expiresIn));
        createCookie(request, response, IARA_EXPIRES_IN, expiresIn, false, Long.parseLong(expiresIn));
    }

    public void createCookie(HttpServletRequest request, HttpServletResponse response, String name, String value,
                             boolean httpOnly, long maxAge) {
        StringBuilder cookieHeader = new StringBuilder();
        String domain = request.getServerName().replaceAll(".*\\.(?=.*\\.)", "");

        cookieHeader.append(name).append('=').append(value);
        cookieHeader.append("; Path=/");
        cookieHeader.append("; Max-Age=").append(maxAge);
        cookieHeader.append("; Domain=").append(domain);
        cookieHeader.append("; SameSite=").append("Strict");
        cookieHeader.append("; Secure");

        if (httpOnly) {
            cookieHeader.append("; HttpOnly");
        }

        response.addHeader("Set-Cookie", cookieHeader.toString());
    }

    public void invalidateCookies(HttpServletRequest request, HttpServletResponse response) {
        createCookie(request, response, IARA_TOKEN, "", true, -1);
        createCookie(request, response, IARA_EXPIRES_IN, "", false, -1);
    }

    public String getTokenFromCookies(HttpServletRequest request) {
        if (Objects.isNull(request.getCookies())) {
            return null;
        }
        Optional<Cookie> opCookie = Arrays.stream(request.getCookies()).filter(cookie -> CookieUtils.IARA_TOKEN.equals(cookie.getName())).findFirst();

        return opCookie.map(Cookie::getValue).orElse(null);

    }
}

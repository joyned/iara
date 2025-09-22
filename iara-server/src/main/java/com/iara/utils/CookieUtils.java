package com.iara.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        cookieHeader.append("; Domain=").append("localhost.iara.com");
        cookieHeader.append("; SameSite=").append("Strict");
        cookieHeader.append("; Secure");

        if (httpOnly) {
            cookieHeader.append("; HttpOnly");
        }

        response.addHeader("Set-Cookie", cookieHeader.toString());
    }

    public Long convertExpiresAtToCookieMaxAge(long expiresAt) {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();

        calendar.set(Calendar.SECOND, (int) (calendar.get(Calendar.SECOND) + expiresAt));

        return TimeUnit.MILLISECONDS.toSeconds(calendar.getTime().getTime()) -
                TimeUnit.MILLISECONDS.toSeconds(now.getTime());
    }
}

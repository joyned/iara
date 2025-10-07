package com.iara.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

public class IpUtils {

    public static final String X_REAL_IP = "X-Real-Ip";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private IpUtils() {
    }

    public static String getIp(HttpServletRequest httpServletRequest) {
        if (Objects.nonNull(httpServletRequest.getHeader(X_REAL_IP))) {
            return httpServletRequest.getHeader(X_REAL_IP);
        } else if (Objects.nonNull(httpServletRequest.getHeader(X_FORWARDED_FOR))) {
            return httpServletRequest.getHeader(X_FORWARDED_FOR);
        } else {
            return httpServletRequest.getRemoteAddr();
        }
    }
}

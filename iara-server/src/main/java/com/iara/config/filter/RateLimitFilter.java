package com.iara.config.filter;

import com.iara.core.exception.RateLimitException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class RateLimitFilter implements Filter {

    private static final String X_REAL_IP = "X-Real-Ip";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private static final String ERROR_MESSAGE = "Too many requests at endpoint %s from IP %s! Please try again after %s milliseconds!";
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> requestCounts = new ConcurrentHashMap<>();

    @Value("${spring.security.rateLimit:#{60}}")
    private int rateLimit;

    @Value("${spring.security.rateLimitDuration:#{60000}}")
    private long rateDuration;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String key = getIp(httpRequest);
        long currentTime = System.currentTimeMillis();

        requestCounts.putIfAbsent(key, new ConcurrentLinkedQueue<>());
        requestCounts.get(key).add(currentTime);
        cleanUpRequestCounts(currentTime);

        if (requestCounts.get(key).size() > rateLimit) {
            throw new RateLimitException(ERROR_MESSAGE, httpRequest.getRequestURI(), key, String.valueOf(rateDuration));
        }

        chain.doFilter(request, response);
    }

    private void cleanUpRequestCounts(final long currentTime) {
        requestCounts.forEach((key, timestamps) ->
                timestamps.removeIf(t -> timeIsTooOld(currentTime, t)));
    }

    private boolean timeIsTooOld(final long currentTime, final long timeToCheck) {
        return currentTime - timeToCheck > rateDuration;
    }

    protected String getIp(HttpServletRequest httpServletRequest) {
        if (Objects.nonNull(httpServletRequest.getHeader(X_REAL_IP))) {
            return httpServletRequest.getHeader(X_REAL_IP);
        } else if (Objects.nonNull(httpServletRequest.getHeader(X_FORWARDED_FOR))) {
            return httpServletRequest.getHeader(X_FORWARDED_FOR);
        } else {
            return httpServletRequest.getRemoteAddr();
        }
    }

}

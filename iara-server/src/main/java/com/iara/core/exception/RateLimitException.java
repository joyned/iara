package com.iara.core.exception;

public class RateLimitException extends BaseException{
    public RateLimitException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "RATE_LIMIT_EXCEPT";
    }

    @Override
    public int getStatus() {
        return 429;
    }
}

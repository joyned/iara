package com.iara.core.exception;

public class ExpiredJwtException extends BaseException {
    public ExpiredJwtException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "EXPIRED_JWT";
    }

    @Override
    public int getStatus() {
        return 401;
    }
}

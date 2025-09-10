package com.iara.core.exception;

public class InvalidJwtException extends BaseException {
    public InvalidJwtException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "INVALID_JWT";
    }

    @Override
    public int getStatus() {
        return 401;
    }
}

package com.iara.core.exception;

public class TokenMissingException extends BaseException {
    public TokenMissingException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "TOKEN_MISSING";
    }

    @Override
    public int getStatus() {
        return 401;
    }
}

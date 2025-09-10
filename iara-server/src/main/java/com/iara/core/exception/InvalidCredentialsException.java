package com.iara.core.exception;

public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "INVALID_CREDENTIALS";
    }

    @Override
    public int getStatus() {
        return 401;
    }
}

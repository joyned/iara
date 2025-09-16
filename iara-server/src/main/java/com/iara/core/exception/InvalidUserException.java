package com.iara.core.exception;

public class InvalidUserException extends BaseException {
    public InvalidUserException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "INVALID_USER";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

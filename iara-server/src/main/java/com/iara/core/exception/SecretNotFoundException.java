package com.iara.core.exception;

public class SecretNotFoundException extends BaseException {
    public SecretNotFoundException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "SECRET_NOT_FOUND";
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

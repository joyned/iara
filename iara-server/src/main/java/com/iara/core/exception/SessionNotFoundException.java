package com.iara.core.exception;

public class SessionNotFoundException extends BaseException {
    public SessionNotFoundException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "SESSION_NOT_FOUND";
    }

    @Override
    public int getStatus() {
        return 401;
    }
}

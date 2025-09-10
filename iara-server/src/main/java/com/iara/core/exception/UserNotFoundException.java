package com.iara.core.exception;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "USER_NOT_FOUND";
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

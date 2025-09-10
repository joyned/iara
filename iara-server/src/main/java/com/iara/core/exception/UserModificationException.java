package com.iara.core.exception;

public class UserModificationException extends BaseException {
    public UserModificationException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "USER_MODIFICATION_NOT_ALLOWED";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

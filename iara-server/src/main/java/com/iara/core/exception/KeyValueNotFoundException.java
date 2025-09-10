package com.iara.core.exception;

public class KeyValueNotFoundException extends BaseException {
    public KeyValueNotFoundException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "KEY_VALUE_NOT_FOUND";
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

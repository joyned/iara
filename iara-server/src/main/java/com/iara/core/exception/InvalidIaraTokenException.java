package com.iara.core.exception;

public class InvalidIaraTokenException extends BaseException {
    public InvalidIaraTokenException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "INVALID_IARA_TOKEN";
    }

    @Override
    public int getStatus() {
        return 401;
    }
}

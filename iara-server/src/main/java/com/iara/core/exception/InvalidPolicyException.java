package com.iara.core.exception;

public class InvalidPolicyException extends BaseException {
    public InvalidPolicyException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "INVALID_POLICY";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

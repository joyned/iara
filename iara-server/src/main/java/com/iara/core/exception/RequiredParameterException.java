package com.iara.core.exception;

public class RequiredParameterException extends BaseException {
    public RequiredParameterException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "REQUIRED_PARAMETER";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

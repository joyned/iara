package com.iara.core.exception;

public class InvalidNamespaceException extends BaseException {
    public InvalidNamespaceException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "INVALID_NAMESPACE";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

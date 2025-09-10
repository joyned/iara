package com.iara.core.exception;

public abstract class BaseException extends RuntimeException {
    public BaseException(String message, String... args) {
        super(String.format(message, args));
    }

    public abstract String getKey();

    public abstract int getStatus();
}

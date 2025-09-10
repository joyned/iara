package com.iara.core.exception;

public class DestroyedSecretException extends BaseException {
    public DestroyedSecretException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "DESTROYED_SECRET";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

package com.iara.core.exception;

public class DuplicatedSecretException extends BaseException {
    public DuplicatedSecretException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "DUPLICATED_SECRET";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

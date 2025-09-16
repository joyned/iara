package com.iara.core.exception;

public class DuplicatedUserEmailException extends BaseException {
    public DuplicatedUserEmailException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "DUPLICATED_USER_EMAIL";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

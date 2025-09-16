package com.iara.core.exception;

public class DuplicatedRoleException extends BaseException {
    public DuplicatedRoleException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "DUPLICATED_ROLE";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

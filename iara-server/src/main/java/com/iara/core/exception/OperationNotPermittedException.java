package com.iara.core.exception;

public class OperationNotPermittedException extends BaseException {
    public OperationNotPermittedException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "OPERATION_NOT_PERMITTED";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

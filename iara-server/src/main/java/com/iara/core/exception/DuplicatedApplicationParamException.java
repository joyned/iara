package com.iara.core.exception;

public class DuplicatedApplicationParamException extends BaseException {
    public DuplicatedApplicationParamException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "DUPLICATED_APPLICATION_PARAM";
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

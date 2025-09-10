package com.iara.core.exception;

public class DuplicatedKvException extends BaseException {
    public DuplicatedKvException(String message, String... args) {
        super(message, args);
    }

    @Override
    public int getStatus() {
        return 400;
    }

    @Override
    public String getKey() {
        return "DUPLICATED_KV";
    }
}

package com.iara.core.exception;

public class BlockedRequestException extends BaseException {
    public BlockedRequestException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "BLOCKED_EXCEPTION";
    }

    @Override
    public int getStatus() {
        return 403;
    }
}

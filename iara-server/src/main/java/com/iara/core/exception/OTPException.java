package com.iara.core.exception;

public class OTPException extends BaseException {
    public OTPException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "OTP_ERROR";
    }

    @Override
    public int getStatus() {
        return 401;
    }
}

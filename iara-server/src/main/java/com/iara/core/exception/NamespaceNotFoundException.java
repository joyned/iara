package com.iara.core.exception;

public class NamespaceNotFoundException extends BaseException {
    public NamespaceNotFoundException(String message, String... args) {
        super(message, args);
    }

    @Override
    public String getKey() {
        return "NAMESPACE_NOT_FOUND";
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

package com.gdgguadalajara.common.model;


public class DomainException extends RuntimeException {
    private final int statusCode;

    public DomainException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static DomainException notFound(String message) {
        return new DomainException(message, 404);
    }

    public static DomainException badRequest(String message) {
        return new DomainException(message, 400);
    }

    public static DomainException forbidden(String message) {
        return new DomainException(message, 403);
    }
}

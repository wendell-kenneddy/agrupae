package com.agrupae.domain.exception;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException() {
        super("Invalid refresh token.");
    }
}

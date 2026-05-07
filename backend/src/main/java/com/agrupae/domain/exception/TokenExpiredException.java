package com.agrupae.domain.exception;

public class TokenExpiredException extends DomainException {
    public TokenExpiredException() {
        super("Refresh token has expired.");
    }
}

package com.agrupae.domain.exception;

public class TokenRevokedException extends DomainException {
    public TokenRevokedException() {
        super("Refresh token has been revoked.");
    }
}

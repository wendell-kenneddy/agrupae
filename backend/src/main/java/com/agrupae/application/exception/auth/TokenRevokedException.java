package com.agrupae.application.exception.auth;

import com.agrupae.application.exception.ApplicationException;

public class TokenRevokedException extends ApplicationException {
    public TokenRevokedException() {
        super("Refresh token has been revoked.");
    }
}

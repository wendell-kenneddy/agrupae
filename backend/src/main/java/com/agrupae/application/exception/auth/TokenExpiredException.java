package com.agrupae.application.exception.auth;

import com.agrupae.application.exception.ApplicationException;

public class TokenExpiredException extends ApplicationException {
    public TokenExpiredException() {
        super("Refresh token has expired.");
    }
}

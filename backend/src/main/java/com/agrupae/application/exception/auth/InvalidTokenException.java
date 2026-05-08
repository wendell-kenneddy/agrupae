package com.agrupae.application.exception.auth;

import com.agrupae.application.exception.ApplicationException;

public class InvalidTokenException extends ApplicationException {
    public InvalidTokenException() {
        super("Invalid refresh token.");
    }
}

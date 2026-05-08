package com.agrupae.application.exception.auth;

import com.agrupae.application.exception.ApplicationException;

public class InvalidCredentialsException extends ApplicationException {
    public InvalidCredentialsException() {
        super("Invalid email or password.");
    }
}

package com.agrupae.application.exception.user;

import com.agrupae.application.exception.ApplicationException;

public class EmailAlreadyInUseException extends ApplicationException {
    public EmailAlreadyInUseException() {
        super("Email is already in use.");
    }
}

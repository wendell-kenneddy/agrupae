package com.agrupae.application.exception.user;

import com.agrupae.application.exception.ApplicationException;

public class UserAlreadyExistsException extends ApplicationException {
    public UserAlreadyExistsException(String email) {
        super("User already exists.");
    }
}

package com.agrupae.application.exception.user;

import com.agrupae.domain.exception.DomainException;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String email) {
        super("User already exists.");
    }
}

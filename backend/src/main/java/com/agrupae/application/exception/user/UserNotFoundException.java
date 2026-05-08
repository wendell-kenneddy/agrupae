package com.agrupae.application.exception.user;

import com.agrupae.domain.exception.DomainException;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException() {
        super("User not found.");
    }
}

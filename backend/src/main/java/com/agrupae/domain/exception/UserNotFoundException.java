package com.agrupae.domain.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException() {
        super("User not found.");
    }
}

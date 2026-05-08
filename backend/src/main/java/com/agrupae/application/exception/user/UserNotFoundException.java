package com.agrupae.application.exception.user;

import com.agrupae.application.exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException() {
        super("User not found.");
    }
}

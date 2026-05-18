package com.agrupae.application.exception.course;

import com.agrupae.application.exception.ApplicationException;

public class InvalidInviteCodeException extends ApplicationException {
    public InvalidInviteCodeException() {
        super("Invalid invite code.");
    }
}

package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class AssignmentExpiredException extends ApplicationException {
    public AssignmentExpiredException() {
        super("The assignment deadline has already passed.");
    }
}

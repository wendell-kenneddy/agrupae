package com.agrupae.application.exception.assignment;

import com.agrupae.application.exception.ApplicationException;

public class AssignmentNotFoundException extends ApplicationException {
    public AssignmentNotFoundException() {
        super("Assignment not found.");
    }
}

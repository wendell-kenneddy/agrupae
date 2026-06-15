package com.agrupae.application.exception.assignment;

import com.agrupae.application.exception.ApplicationException;

public class AssignmentArtifactNotFoundException extends ApplicationException {
    public AssignmentArtifactNotFoundException() {
        super("Assignment artifact not found.");
    }
}

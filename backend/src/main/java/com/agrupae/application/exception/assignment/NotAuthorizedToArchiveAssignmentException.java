package com.agrupae.application.exception.assignment;

import com.agrupae.application.exception.ApplicationException;

public class NotAuthorizedToArchiveAssignmentException extends ApplicationException {
    public NotAuthorizedToArchiveAssignmentException() {
        super("Only the class leader or an admin can archive this assignment.");
    }
}

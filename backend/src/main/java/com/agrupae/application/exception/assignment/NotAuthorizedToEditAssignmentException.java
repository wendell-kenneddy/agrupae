package com.agrupae.application.exception.assignment;

import com.agrupae.application.exception.ApplicationException;

public class NotAuthorizedToEditAssignmentException extends ApplicationException {
    public NotAuthorizedToEditAssignmentException() {
        super("Only the class leader or an admin can edit this assignment.");
    }
}

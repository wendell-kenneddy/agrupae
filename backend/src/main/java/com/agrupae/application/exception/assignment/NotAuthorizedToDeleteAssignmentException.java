package com.agrupae.application.exception.assignment;

public class NotAuthorizedToDeleteAssignmentException extends RuntimeException {
    public NotAuthorizedToDeleteAssignmentException() {
        super("You are not authorized to delete this assignment.");
    }
    
}

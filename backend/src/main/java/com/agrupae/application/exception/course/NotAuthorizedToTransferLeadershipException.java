package com.agrupae.application.exception.course;

import com.agrupae.application.exception.ApplicationException;

public class NotAuthorizedToTransferLeadershipException extends ApplicationException{
    public NotAuthorizedToTransferLeadershipException() {
        super("Only the course leader can transfer leadership of this course.");
    }
}

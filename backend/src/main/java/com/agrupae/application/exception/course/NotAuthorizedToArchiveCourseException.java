package com.agrupae.application.exception.course;

import com.agrupae.application.exception.ApplicationException;

public class NotAuthorizedToArchiveCourseException extends ApplicationException {
    public NotAuthorizedToArchiveCourseException() {
        super("Only the class leader or an admin can archive this course.");
    }
}

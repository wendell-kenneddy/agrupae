package com.agrupae.application.exception.course;

import com.agrupae.application.exception.ApplicationException;

public class AlreadyJoinedCourseException extends ApplicationException {
    public AlreadyJoinedCourseException() {
        super("User has already joined this course.");
    }
}

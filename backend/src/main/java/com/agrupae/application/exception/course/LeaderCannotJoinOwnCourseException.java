package com.agrupae.application.exception.course;

import com.agrupae.application.exception.ApplicationException;

public class LeaderCannotJoinOwnCourseException extends ApplicationException {
    public LeaderCannotJoinOwnCourseException() {
        super("Course leader cannot join their own course as a student.");
    }
}

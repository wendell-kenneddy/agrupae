package com.agrupae.application.exception.course;

import com.agrupae.application.exception.ApplicationException;

public class CourseNotFoundException extends ApplicationException {
    public CourseNotFoundException() {
        super("Course not found.");
    }
}

package com.agrupae.application.exception.course;

import com.agrupae.application.exception.ApplicationException;

public class CourseArchivedException extends ApplicationException {
    public CourseArchivedException() {
        super("Course is archived.");
    }
}

package com.agrupae.domain.course;

import com.agrupae.domain.exception.DomainException;

public class CourseArchivedException extends DomainException {
    public CourseArchivedException() {
        super("Course is archived.");
    }
}

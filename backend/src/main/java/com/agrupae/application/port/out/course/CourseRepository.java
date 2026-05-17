package com.agrupae.application.port.out.course;

import java.util.UUID;

import com.agrupae.domain.course.Course;

public interface CourseRepository {
    Course findById(UUID id);

    Course save(Course course);
}

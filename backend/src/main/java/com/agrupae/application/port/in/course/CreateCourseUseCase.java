package com.agrupae.application.port.in.course;

import java.util.UUID;

public interface CreateCourseUseCase {
    CourseView handle(UUID leaderId, String name, String description);
}

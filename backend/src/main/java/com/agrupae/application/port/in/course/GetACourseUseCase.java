package com.agrupae.application.port.in.course;

import java.util.UUID;

public interface GetACourseUseCase {
    CourseView handle(UUID studentId, UUID courseId);
}

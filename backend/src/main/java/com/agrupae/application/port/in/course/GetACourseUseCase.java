package com.agrupae.application.port.in.course;

import com.agrupae.domain.course.Course;
import com.agrupae.application.port.in.course.CourseView;
import java.util.UUID;

public interface GetACourseUseCase {
    CourseView handle(UUID studentId, UUID courseId);
}

package com.agrupae.application.port.in.course;

import java.util.UUID;

public interface JoinCourseUseCase {
    CourseView handle(UUID studentId, String inviteCode);
}

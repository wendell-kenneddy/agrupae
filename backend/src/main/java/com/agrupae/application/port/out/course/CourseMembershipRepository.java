package com.agrupae.application.port.out.course;

import java.util.UUID;

import com.agrupae.domain.course.CourseMembership;

public interface CourseMembershipRepository {
    boolean exists(UUID studentId, UUID courseId);

    CourseMembership save(CourseMembership membership);
}

package com.agrupae.application.service.course;

import com.agrupae.application.port.in.course.GetACourseUseCase;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.course.CourseView;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class GetACourseService implements GetACourseUseCase {
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;

    public CourseView handle(@NonNull UUID studentId, @NonNull UUID courseId) {

        Course course = this.courseRepository.findById(courseId);
        if (course == null) throw new CourseNotFoundException();

        if (this.courseMembershipRepository.exists(studentId, courseId)) {
            return new CourseView(course.getId(), 
            course.getLeaderId(), course.getName(),
            course.getDescription(), course.getInviteCode(),
            course.isArchived(), course.getCreatedAt(), course.getUpdatedAt());
        } else 
            throw new CourseNotFoundException();
    }
}

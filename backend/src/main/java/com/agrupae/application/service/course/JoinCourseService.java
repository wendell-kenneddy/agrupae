package com.agrupae.application.service.course;

import java.util.UUID;

import com.agrupae.application.exception.course.AlreadyJoinedCourseException;
import com.agrupae.application.exception.course.InvalidInviteCodeException;
import com.agrupae.application.exception.course.LeaderCannotJoinOwnCourseException;
import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.in.course.JoinCourseUseCase;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.course.CourseMembership;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JoinCourseService implements JoinCourseUseCase {
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;

    @Override
    public CourseView handle(@NonNull UUID studentId, @NonNull String inviteCode) {
        Course course = this.courseRepository.findByInviteCode(inviteCode.trim());
        
        if (course == null || course.isArchived())
            throw new InvalidInviteCodeException();

        if (course.getLeaderId().equals(studentId))
            throw new LeaderCannotJoinOwnCourseException();

        if (this.courseMembershipRepository.exists(studentId, course.getId()))
            throw new AlreadyJoinedCourseException();

        this.courseMembershipRepository.save(CourseMembership.create(studentId, course.getId()));

        return new CourseView(
                course.getId(),
                course.getLeaderId(),
                course.getName(),
                course.getDescription(),
                course.getInviteCode(),
                course.isArchived(),
                course.getCreatedAt(),
                course.getUpdatedAt());
    }
}

package com.agrupae.application.service.course;

import java.util.UUID;
import java.util.List;

import com.agrupae.application.port.in.course.GetMembersUseCase;
import com.agrupae.application.port.in.user.UserProfileView;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.course.CourseMembership;

import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import lombok.NonNull;

import com.agrupae.application.exception.course.CourseNotFoundException;

@RequiredArgsConstructor
public class GetMembersService implements GetMembersUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public Page<UserProfileView> handle(@NonNull UUID courseId,@NonNull UUID actorId,@NonNull Pageable pageable) {
        Course course = this.courseRepository.findById(courseId);

        if (course == null | !courseMembershipRepository.exists(actorId, courseId)) throw new CourseNotFoundException();

        List<CourseMembership> memberships = this.courseMembershipRepository.findByCourseId(courseId);

        List<UUID> studentIds = memberships.stream()
                .map(CourseMembership::getStudentId)
                .toList();
        
        return userRepository.findAllByIdIn(studentIds, pageable)
                .map(user -> new UserProfileView(
                    user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt(), user.getUpdatedAt()
                ));
    }
}

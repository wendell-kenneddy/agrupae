package com.agrupae.application.service.course;

import com.agrupae.application.port.in.course.GetCoursesUseCase;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.domain.course.CourseMembership;
import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.out.course.CourseRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetCoursesService implements GetCoursesUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final CourseRepository courseRepository;

    @Override
    public Page<CourseView> handle(@NonNull UUID studentId, @NonNull Pageable pageable) {
        List<CourseMembership> memberships = courseMembershipRepository.findByStudentId(studentId);

        if (memberships.isEmpty())
            return Page.empty(pageable);

        List<UUID> courseIds = memberships.stream()
                .map(CourseMembership::getCourseId)
                .toList();

        return courseRepository.findAllByIdIn(courseIds, pageable)
                .map(course -> new CourseView(
                        course.getId(),
                        course.getLeaderId(),
                        course.getName(),
                        course.getDescription(),
                        course.getInviteCode(),
                        course.isArchived(),
                        course.getCreatedAt(),
                        course.getUpdatedAt()));
    }
}

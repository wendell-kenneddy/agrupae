package com.agrupae.application.service.assignment;

import java.util.UUID;

import com.agrupae.application.port.in.assignment.GetAssignmentsUseCase;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.domain.course.Course;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.CourseMembership;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.application.port.out.assignment.AssignmentRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class GetAssignmentsService implements GetAssignmentsUseCase {
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;

    @Override
    public Page<AssignmentView> handle(@NonNull UUID studentId, @NonNull UUID courseId, Pageable pageable) {
        Course course = this.courseRepository.findById(courseId);
        if (course == null)
            throw new CourseNotFoundException();

        if (!this.courseMembershipRepository.exists(studentId, courseId))
            throw new CourseNotFoundException();

        return this.assignmentRepository.findByCourseId(courseId, pageable)
                .map(assignment -> new AssignmentView(
                    assignment.getId(), assignment.getCourseId(), assignment.getName(),
                    assignment.getDescription(),assignment.getAssignmentFlags(),
                    assignment.isArchived(), assignment.getDueDate(),
                    assignment.getCreatedAt(), assignment.getUpdatedAt()
                ));
    }
    
}

package com.agrupae.application.service.assignment;

import java.util.UUID;
import com.agrupae.application.port.in.assignment.GetAnAssignmentUseCase;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.domain.course.Course;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.application.port.out.assignment.AssignmentRepository;

@RequiredArgsConstructor
public class GetAnAssignmentService implements GetAnAssignmentUseCase {
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;

    @Override
    public AssignmentView handle(@NonNull UUID studentId, @NonNull UUID courseId, @NonNull UUID assignmentId) {
        Course course = this.courseRepository.findById(courseId);

        if (course == null || !this.courseMembershipRepository.exists(studentId, courseId)) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);

        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        return new AssignmentView(
                assignment.getId(),
                assignment.getCourseId(),
                assignment.getName(),
                assignment.getDescription(),
                assignment.getAssignmentFlags(),
                assignment.isArchived(),
                assignment.getDueDate(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt());
    }
}

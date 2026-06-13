package com.agrupae.application.service.assignment;

import java.util.UUID;

import com.agrupae.domain.assignment.Assignment;
import com.agrupae.application.port.in.assignment.AddReferenceArtifactUseCase;
import com.agrupae.application.port.in.assignment.AssignmentArtifactView;
import com.agrupae.application.port.out.assignment.AssignmentArtifactRepository;
import com.agrupae.domain.assignment.AssignmentArtifact;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.assignment.NotCourseLeaderException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.out.course.CourseMembershipRepository;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class AddReferenceArtifactService implements AddReferenceArtifactUseCase {
    private final AssignmentArtifactRepository assignmentArtifactRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;

    @Override
    public AssignmentArtifactView handle(
            @NonNull UUID userId,
            @NonNull UUID courseId,
            @NonNull UUID assignmentId,
            @NonNull String name,
            @NonNull String description,
            @NonNull String resourceLink) {

        Assignment assignment = this.assignmentRepository.findById(assignmentId);
        if (assignment == null)
            throw new AssignmentNotFoundException();

        Course course = this.courseRepository.findById(assignment.getCourseId());
        if (course == null | !courseMembershipRepository.exists(userId, courseId))
            throw new CourseNotFoundException();

        if (!course.getLeaderId().equals(userId)) {
            throw new NotCourseLeaderException("The user is not the course leader.");
        }

        AssignmentArtifact artifact = AssignmentArtifact.create(
                assignmentId,
                name,
                description,
                resourceLink);

        AssignmentArtifact saved = this.assignmentArtifactRepository.save(artifact);

        return new AssignmentArtifactView(
                saved.getId(),
                saved.getAssignmentId(),
                saved.getName(),
                saved.getDescription(),
                saved.getResourceLink(),
                saved.getCreatedAt(),
                saved.getUpdatedAt());
    }
}

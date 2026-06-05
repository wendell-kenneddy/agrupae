package com.agrupae.application.service.assignment;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.agrupae.application.port.in.assignment.AssignmentArtifactView;
import com.agrupae.domain.assignment.AssignmentArtifact;
import com.agrupae.application.port.in.assignment.GetAssignmentArtifactsUseCase;
import com.agrupae.application.port.out.assignment.AssignmentArtifactRepository;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.domain.assignment.Assignment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAssignmentArtifactsService implements GetAssignmentArtifactsUseCase {
    private final AssignmentRepository assignmentRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentArtifactRepository assignmentArtifactRepository;

    @Override
    public List<AssignmentArtifactView> handle(@NonNull UUID userId, @NonNull UUID assignmentId) {
        Assignment assignment = this.assignmentRepository.findById(assignmentId);
        if (assignment == null)
            throw new AssignmentNotFoundException();

        UUID courseId = assignment.getCourseId();
        if (!this.courseMembershipRepository.exists(courseId, userId))
            throw new CourseNotFoundException();

        List<AssignmentArtifact> artifacts = this.assignmentArtifactRepository.findByAssignmentId(assignmentId);

        return artifacts.stream()
                .map(artifact -> new AssignmentArtifactView(
                        artifact.getId(),
                        artifact.getAssignmentId(),
                        artifact.getName(),
                        artifact.getDescription(),
                        artifact.getResourceLink(),
                        artifact.getCreatedAt(),
                        artifact.getUpdatedAt()))
                .collect(Collectors.toList());
    }
}

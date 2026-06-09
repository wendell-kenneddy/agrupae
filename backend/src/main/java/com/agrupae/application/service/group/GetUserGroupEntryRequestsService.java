package com.agrupae.application.service.group;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.group.GetUserGroupEntryRequestsUseCase;
import com.agrupae.application.port.in.group.GroupEntryRequestView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetUserGroupEntryRequestsService implements GetUserGroupEntryRequestsUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupEntryRequestRepository groupEntryRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GroupEntryRequestView> handle(UUID courseId, UUID assignmentId, UUID userId) {
        if (!this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);
        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        return this.groupEntryRequestRepository.findByAssignmentIdAndUserId(assignmentId, userId)
                .stream()
                .map(request -> new GroupEntryRequestView(
                        request.getId(),
                        request.getGroupId(),
                        request.getUserId(),
                        request.getStatus(),
                        request.getCreatedAt(),
                        request.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
}

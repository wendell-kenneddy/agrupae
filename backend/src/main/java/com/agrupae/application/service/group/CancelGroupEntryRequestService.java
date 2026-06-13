package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.GroupEntryRequestNotFoundException;
import com.agrupae.application.port.in.group.CancelGroupEntryRequestUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.domain.group.GroupEntryRequestStatus;
import com.agrupae.domain.exception.DomainException;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class CancelGroupEntryRequestService implements CancelGroupEntryRequestUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupEntryRequestRepository groupEntryRequestRepository;

    @Override
    @Transactional
    public void handle(@NonNull UUID courseId, @NonNull UUID assignmentId, @NonNull UUID groupId, @NonNull UUID requestId, @NonNull UUID userId) {
        if (!this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);
        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        Group group = this.groupRepository.findById(groupId);
        if (group == null || !group.getAssignmentId().equals(assignmentId)) {
            throw new GroupNotFoundException();
        }

        GroupEntryRequest request = this.groupEntryRequestRepository.findById(requestId);
        if (request == null || !request.getUserId().equals(userId) || !request.getGroupId().equals(groupId)) {
            throw new GroupEntryRequestNotFoundException();
        }

        if (request.getStatus() != GroupEntryRequestStatus.PENDING) {
            throw new DomainException("Only PENDING requests can be cancelled.");
        }

        this.groupEntryRequestRepository.deleteById(requestId);
    }
}

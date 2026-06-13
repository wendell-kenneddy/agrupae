package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.GroupEntryRequestNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.port.in.group.RejectGroupEntryRequestUseCase;
import com.agrupae.application.port.in.group.GroupEntryRequestView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupEntryRequest;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class RejectGroupEntryRequestService implements RejectGroupEntryRequestUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupEntryRequestRepository groupEntryRequestRepository;

    @Override
    @Transactional
    public GroupEntryRequestView handle(@NonNull UUID courseId, @NonNull UUID assignmentId, @NonNull UUID groupId, @NonNull UUID requestId, @NonNull UUID userId) {
        if (!this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);
        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        if (assignment.isArchived()) {
            throw new AssignmentArchivedException();
        }

        Group group = this.groupRepository.findById(groupId);
        if (group == null || !group.getAssignmentId().equals(assignmentId)) {
            throw new GroupNotFoundException();
        }

        if (!group.getLeaderId().equals(userId)) {
            throw new NotGroupLeaderException();
        }

        GroupEntryRequest request = this.groupEntryRequestRepository.findById(requestId);
        if (request == null || !request.getGroupId().equals(groupId)) {
            throw new GroupEntryRequestNotFoundException();
        }

        request.reject();
        GroupEntryRequest savedRequest = this.groupEntryRequestRepository.save(request);

        return new GroupEntryRequestView(
                savedRequest.getId(),
                savedRequest.getGroupId(),
                savedRequest.getUserId(),
                savedRequest.getStatus(),
                savedRequest.getCreatedAt(),
                savedRequest.getUpdatedAt());
    }
}

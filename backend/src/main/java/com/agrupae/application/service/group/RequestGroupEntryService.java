package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupMemberLimitReachedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.GroupNotClosedException;
import com.agrupae.application.exception.group.StudentAlreadyInGroupException;
import com.agrupae.application.exception.group.PendingRequestAlreadyExistsException;
import com.agrupae.application.port.in.group.RequestGroupEntryUseCase;
import com.agrupae.application.port.in.group.GroupEntryRequestView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupEntryRequest;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequestGroupEntryService implements RequestGroupEntryUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupEntryRequestRepository groupEntryRequestRepository;

    @Override
    @Transactional
    public GroupEntryRequestView handle(UUID courseId, UUID assignmentId, UUID groupId, UUID userId) {
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

        if (group.isOpen()) {
            throw new GroupNotClosedException();
        }

        if (this.groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId)) {
            throw new StudentAlreadyInGroupException();
        }

        int memberCount = this.groupMemberRepository.countByGroupId(groupId);
        if (memberCount >= assignment.getAssignmentFlags().maxGroupMembers()) {
            throw new GroupMemberLimitReachedException();
        }

        if (this.groupEntryRequestRepository.existsPendingByAssignmentIdAndUserId(assignmentId, userId)) {
            throw new PendingRequestAlreadyExistsException();
        }

        GroupEntryRequest request = GroupEntryRequest.create(groupId, userId);
        GroupEntryRequest savedRequest = this.groupEntryRequestRepository.save(request);

        return new GroupEntryRequestView(
                savedRequest.getId(),
                savedRequest.getGroupId(),
                savedRequest.getUserId(),
                savedRequest.getStatus(),
                savedRequest.getCreatedAt(),
                savedRequest.getUpdatedAt()
        );
    }
}

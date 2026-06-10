package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupMemberLimitReachedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.GroupEntryRequestNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.exception.group.StudentAlreadyInGroupException;
import com.agrupae.application.port.in.group.AcceptGroupEntryRequestUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupMember;
import com.agrupae.domain.group.GroupEntryRequest;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AcceptGroupEntryRequestService implements AcceptGroupEntryRequestUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupEntryRequestRepository groupEntryRequestRepository;

    @Override
    @Transactional
    public void handle(UUID courseId, UUID assignmentId, UUID groupId, UUID requestId, UUID userId) {
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

        if (this.groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, request.getUserId())) {
            throw new StudentAlreadyInGroupException();
        }

        int memberCount = this.groupMemberRepository.countByGroupId(groupId);
        int maxMembers = assignment.getAssignmentFlags().maxGroupMembers();
        if (memberCount >= maxMembers) {
            throw new GroupMemberLimitReachedException();
        }

        request.accept();
        this.groupEntryRequestRepository.save(request);

        this.groupMemberRepository.save(new GroupMember(groupId, request.getUserId()));

        int newMemberCount = memberCount + 1;
        if (newMemberCount >= maxMembers) {
            this.groupEntryRequestRepository.deleteAllPendingByGroupId(groupId);
        }
    }
}

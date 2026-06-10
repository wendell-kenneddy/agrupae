package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupMemberLimitReachedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.GroupEntryRequestNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.exception.group.StudentAlreadyInGroupException;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupMember;
import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.domain.group.GroupEntryRequestStatus;
import com.agrupae.domain.exception.DomainException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AcceptGroupEntryRequestServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupMemberRepository groupMemberRepository;
    private GroupEntryRequestRepository groupEntryRequestRepository;
    private AcceptGroupEntryRequestService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupMemberRepository = mock(GroupMemberRepository.class);
        groupEntryRequestRepository = mock(GroupEntryRequestRepository.class);
        service = new AcceptGroupEntryRequestService(
                courseMembershipRepository, assignmentRepository,
                groupRepository, groupMemberRepository, groupEntryRequestRepository);
    }

    private static AssignmentFlags defaultFlags(int maxMembers) {
        return new AssignmentFlags(maxMembers, 10, true, true, false, false, false, false, false);
    }

    private static Assignment activeAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                defaultFlags(4), false,
                now.plusSeconds(86_400), now, now);
    }

    private static Assignment archivedAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                defaultFlags(4), true,
                now.plusSeconds(86_400), now, now);
    }

    private static Group closedGroup(UUID groupId, UUID assignmentId, UUID leaderId) {
        Instant now = Instant.now();
        return Group.reconstruct(
                groupId, assignmentId, leaderId, "Team Beta",
                false, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withValidInputs_acceptsRequestAndSavesMember() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID studentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, groupId, studentId, GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, studentId)).thenReturn(false);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(2); // capacity is 4

            service.handle(courseId, assignmentId, groupId, requestId, leaderId);

            ArgumentCaptor<GroupEntryRequest> requestCaptor = ArgumentCaptor.forClass(GroupEntryRequest.class);
            verify(groupEntryRequestRepository).save(requestCaptor.capture());
            assertThat(requestCaptor.getValue().getStatus()).isEqualTo(GroupEntryRequestStatus.ACCEPTED);

            ArgumentCaptor<GroupMember> memberCaptor = ArgumentCaptor.forClass(GroupMember.class);
            verify(groupMemberRepository).save(memberCaptor.capture());
            assertThat(memberCaptor.getValue().groupId()).isEqualTo(groupId);
            assertThat(memberCaptor.getValue().memberId()).isEqualTo(studentId);

            verify(groupEntryRequestRepository, never()).deleteAllPendingByGroupId(any());
        }

        @Test
        void withValidInputs_groupFillsUp_deletesRemainingPendingRequests() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID studentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, groupId, studentId, GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, studentId)).thenReturn(false);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(3); // capacity is 4, new one will make it 4

            service.handle(courseId, assignmentId, groupId, requestId, leaderId);

            verify(groupEntryRequestRepository).deleteAllPendingByGroupId(groupId);
        }

        @Test
        void withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, userId))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(groupEntryRequestRepository, never()).save(any());
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, userId))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void withAssignmentArchived_throwsAssignmentArchivedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(archivedAssignment(assignmentId, courseId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, userId))
                    .isInstanceOf(AssignmentArchivedException.class);
        }

        @Test
        void withGroupNotFound_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, userId))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withUserNotGroupLeader_throwsNotGroupLeaderException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, userId))
                    .isInstanceOf(NotGroupLeaderException.class);
        }

        @Test
        void withRequestNotFound_throwsGroupEntryRequestNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, leaderId))
                    .isInstanceOf(GroupEntryRequestNotFoundException.class);
        }

        @Test
        void withRequestForDifferentGroup_throwsGroupEntryRequestNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID otherGroupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID studentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, otherGroupId, studentId, GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, leaderId))
                    .isInstanceOf(GroupEntryRequestNotFoundException.class);
        }

        @Test
        void withStudentAlreadyInAnotherGroup_throwsStudentAlreadyInGroupException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID studentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, groupId, studentId, GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, studentId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, leaderId))
                    .isInstanceOf(StudentAlreadyInGroupException.class);
        }

        @Test
        void withGroupAlreadyFull_throwsGroupMemberLimitReachedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID studentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, groupId, studentId, GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, studentId)).thenReturn(false);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(4); // capacity is 4

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, leaderId))
                    .isInstanceOf(GroupMemberLimitReachedException.class);
        }

        @Test
        void withNonPendingRequest_throwsDomainException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID studentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, groupId, studentId, GroupEntryRequestStatus.ACCEPTED, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, studentId)).thenReturn(false);
            when(groupMemberRepository.countByGroupId(groupId)).thenReturn(2);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, leaderId))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Only PENDING requests can be accepted/rejected.");
        }
    }
}

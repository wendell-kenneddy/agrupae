package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.GroupEntryRequestNotFoundException;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.domain.group.GroupEntryRequestStatus;
import com.agrupae.domain.exception.DomainException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CancelGroupEntryRequestServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupEntryRequestRepository groupEntryRequestRepository;
    private CancelGroupEntryRequestService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupEntryRequestRepository = mock(GroupEntryRequestRepository.class);
        service = new CancelGroupEntryRequestService(
                courseMembershipRepository, assignmentRepository,
                groupRepository, groupEntryRequestRepository);
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

    private static Group closedGroup(UUID groupId, UUID assignmentId) {
        Instant now = Instant.now();
        return Group.reconstruct(
                groupId, assignmentId, UUID.randomUUID(), "Team Beta",
                false, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withValidInputs_deletesRequest() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, groupId, userId, GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            service.handle(courseId, assignmentId, groupId, requestId, userId);

            verify(groupEntryRequestRepository).deleteById(requestId);
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
            verify(groupEntryRequestRepository, never()).deleteById(any());
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
            verify(groupEntryRequestRepository, never()).deleteById(any());
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
            verify(groupEntryRequestRepository, never()).deleteById(any());
        }

        @Test
        void withRequestNotFound_throwsGroupEntryRequestNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId));
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, userId))
                    .isInstanceOf(GroupEntryRequestNotFoundException.class);
            verify(groupEntryRequestRepository, never()).deleteById(any());
        }

        @Test
        void withRequestForDifferentUser_throwsGroupEntryRequestNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID otherUserId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, groupId, otherUserId, GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, userId))
                    .isInstanceOf(GroupEntryRequestNotFoundException.class);
            verify(groupEntryRequestRepository, never()).deleteById(any());
        }

        @Test
        void withRequestForDifferentGroup_throwsGroupEntryRequestNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID otherGroupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, otherGroupId, userId, GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, userId))
                    .isInstanceOf(GroupEntryRequestNotFoundException.class);
            verify(groupEntryRequestRepository, never()).deleteById(any());
        }

        @Test
        void withNonPendingRequest_throwsDomainException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId));

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    requestId, groupId, userId, GroupEntryRequestStatus.ACCEPTED, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findById(requestId)).thenReturn(request);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, requestId, userId))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Only PENDING requests can be cancelled.");
            verify(groupEntryRequestRepository, never()).deleteById(any());
        }
    }
}

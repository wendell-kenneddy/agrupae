package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.port.in.group.GroupEntryRequestView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.domain.group.GroupEntryRequestStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetGroupEntryRequestsServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private GroupEntryRequestRepository groupEntryRequestRepository;
    private GetGroupEntryRequestsService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupEntryRequestRepository = mock(GroupEntryRequestRepository.class);
        service = new GetGroupEntryRequestsService(
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

    private static Group closedGroup(UUID groupId, UUID assignmentId, UUID leaderId) {
        Instant now = Instant.now();
        return Group.reconstruct(
                groupId, assignmentId, leaderId, "Team Beta",
                false, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withValidInputs_noStatusFilter_returnsAllRequests() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            GroupEntryRequest req1 = GroupEntryRequest.reconstruct(
                    UUID.randomUUID(), groupId, UUID.randomUUID(), GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            GroupEntryRequest req2 = GroupEntryRequest.reconstruct(
                    UUID.randomUUID(), groupId, UUID.randomUUID(), GroupEntryRequestStatus.REJECTED, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findByGroupId(groupId)).thenReturn(List.of(req1, req2));

            List<GroupEntryRequestView> result = service.handle(courseId, assignmentId, groupId, leaderId, null);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).status()).isEqualTo(GroupEntryRequestStatus.PENDING);
            assertThat(result.get(1).status()).isEqualTo(GroupEntryRequestStatus.REJECTED);
        }

        @Test
        void withValidInputs_withStatusFilter_returnsFilteredRequests() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            GroupEntryRequest req1 = GroupEntryRequest.reconstruct(
                    UUID.randomUUID(), groupId, UUID.randomUUID(), GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            when(groupEntryRequestRepository.findByGroupIdAndStatus(groupId, GroupEntryRequestStatus.PENDING)).thenReturn(List.of(req1));

            List<GroupEntryRequestView> result = service.handle(courseId, assignmentId, groupId, leaderId, GroupEntryRequestStatus.PENDING);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo(GroupEntryRequestStatus.PENDING);
        }

        @Test
        void withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, null))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, null))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void withGroupNotFound_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, null))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withUserNotGroupLeader_throwsNotGroupLeaderException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(groupRepository.findById(groupId))
                    .thenReturn(closedGroup(groupId, assignmentId, leaderId));

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, groupId, userId, null))
                    .isInstanceOf(NotGroupLeaderException.class);
        }
    }
}

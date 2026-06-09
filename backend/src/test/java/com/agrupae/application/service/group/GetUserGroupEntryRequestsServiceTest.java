package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.group.GroupEntryRequestView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.domain.group.GroupEntryRequestStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUserGroupEntryRequestsServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupEntryRequestRepository groupEntryRequestRepository;
    private GetUserGroupEntryRequestsService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupEntryRequestRepository = mock(GroupEntryRequestRepository.class);
        service = new GetUserGroupEntryRequestsService(
                courseMembershipRepository, assignmentRepository, groupEntryRequestRepository);
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

    @Nested
    class Handle {

        @Test
        void withValidInputs_returnsRequests() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));

            GroupEntryRequest req1 = GroupEntryRequest.reconstruct(
                    UUID.randomUUID(), groupId, userId, GroupEntryRequestStatus.PENDING, Instant.now(), Instant.now());
            GroupEntryRequest req2 = GroupEntryRequest.reconstruct(
                    UUID.randomUUID(), groupId, userId, GroupEntryRequestStatus.REJECTED, Instant.now(), Instant.now());

            when(groupEntryRequestRepository.findByAssignmentIdAndUserId(assignmentId, userId))
                    .thenReturn(List.of(req1, req2));

            List<GroupEntryRequestView> result = service.handle(courseId, assignmentId, userId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(req1.getId());
            assertThat(result.get(0).status()).isEqualTo(GroupEntryRequestStatus.PENDING);
            assertThat(result.get(1).id()).isEqualTo(req2.getId());
            assertThat(result.get(1).status()).isEqualTo(GroupEntryRequestStatus.REJECTED);
        }

        @Test
        void withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, userId))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(courseId, assignmentId, userId))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }
    }
}

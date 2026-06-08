package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupCreationNotAllowedException;
import com.agrupae.application.exception.group.MaxGroupsReachedException;
import com.agrupae.application.exception.group.StudentAlreadyInGroupException;
import com.agrupae.application.port.in.group.GroupView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.exception.DomainException;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupMember;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateGroupServiceTest {

    private AssignmentRepository assignmentRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private GroupRepository groupRepository;
    private GroupMemberRepository groupMemberRepository;
    private CreateGroupService service;

    @BeforeEach
    void setUp() {
        assignmentRepository = mock(AssignmentRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        groupRepository = mock(GroupRepository.class);
        groupMemberRepository = mock(GroupMemberRepository.class);
        service = new CreateGroupService(
                assignmentRepository, courseMembershipRepository,
                groupRepository, groupMemberRepository);
    }

    private static AssignmentFlags flagsWithCreation(boolean studentsCanCreate, int maxGroups) {
        return new AssignmentFlags(4, maxGroups, studentsCanCreate, true, false, false, false, false, false);
    }

    private static Assignment activeAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flagsWithCreation(true, 10), false,
                now.plusSeconds(86_400), now, now);
    }

    private static Assignment archivedAssignment(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flagsWithCreation(true, 10), true,
                now.plusSeconds(86_400), now, now);
    }

    private static Assignment assignmentWithCreationDisabled(UUID assignmentId, UUID courseId) {
        Instant now = Instant.now();
        AssignmentFlags flags = new AssignmentFlags(4, 10, false, true, false, false, false, false, true);
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flags, false, now.plusSeconds(86_400), now, now);
    }

    @Nested
    class Handle {

        @Test
        void withValidInputs_persistsGroupAndMemberAndReturnsView() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId))
                    .thenReturn(false);
            when(groupRepository.countByAssignmentId(assignmentId)).thenReturn(0);

            GroupView view = service.handle(userId, courseId, assignmentId, "Team Alpha", true);

            verify(groupRepository).save(any(Group.class));
            verify(groupMemberRepository).save(any(GroupMember.class));
            assertThat(view.id()).isNotNull();
            assertThat(view.assignmentId()).isEqualTo(assignmentId);
            assertThat(view.leaderId()).isEqualTo(userId);
            assertThat(view.name()).isEqualTo("Team Alpha");
            assertThat(view.open()).isTrue();
            assertThat(view.membersCanEditArtifacts()).isTrue();
            assertThat(view.createdAt()).isNotNull();
            assertThat(view.updatedAt()).isEqualTo(view.createdAt());
        }

        @Test
        void withNonExistentAssignment_throwsAssignmentNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, "Team", true))
                    .isInstanceOf(AssignmentNotFoundException.class);

            verify(groupRepository, never()).save(any());
        }

        @Test
        void withAssignmentFromDifferentCourse_throwsAssignmentNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID otherCourseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, otherCourseId));

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, "Team", true))
                    .isInstanceOf(AssignmentNotFoundException.class);

            verify(groupRepository, never()).save(any());
        }

        @Test
        void withArchivedAssignment_throwsAssignmentArchivedException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(archivedAssignment(assignmentId, courseId));

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, "Team", true))
                    .isInstanceOf(AssignmentArchivedException.class);

            verify(groupRepository, never()).save(any());
        }

        @Test
        void withCreationDisabled_throwsGroupCreationNotAllowedException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignmentWithCreationDisabled(assignmentId, courseId));

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, "Team", true))
                    .isInstanceOf(GroupCreationNotAllowedException.class);

            verify(groupRepository, never()).save(any());
        }

        @Test
        void withUserNotEnrolled_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, "Team", true))
                    .isInstanceOf(CourseNotFoundException.class);

            verify(assignmentRepository, never()).findById(any());
            verify(groupRepository, never()).save(any());
        }

        @Test
        void withStudentAlreadyInGroup_throwsStudentAlreadyInGroupException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, "Team", true))
                    .isInstanceOf(StudentAlreadyInGroupException.class);

            verify(groupRepository, never()).save(any());
        }

        @Test
        void withMaxGroupsReached_throwsMaxGroupsReachedException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Instant now = Instant.now();
            Assignment assignment = Assignment.reconstruct(
                    assignmentId, courseId, "Assignment", "Desc",
                    new AssignmentFlags(4, 2, true, true, false, false, false, false, false),
                    false, now.plusSeconds(86_400), now, now);

            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId))
                    .thenReturn(false);
            when(groupRepository.countByAssignmentId(assignmentId)).thenReturn(2);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, "Team", true))
                    .isInstanceOf(MaxGroupsReachedException.class);

            verify(groupRepository, never()).save(any());
        }

        @Test
        void withBlankName_propagatesDomainException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(activeAssignment(assignmentId, courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(groupMemberRepository.existsByAssignmentIdAndMemberId(assignmentId, userId))
                    .thenReturn(false);
            when(groupRepository.countByAssignmentId(assignmentId)).thenReturn(0);

            assertThatThrownBy(() -> service.handle(userId, courseId, assignmentId, "   ", true))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Group name cannot be blank.");

            verify(groupRepository, never()).save(any());
        }
    }
}

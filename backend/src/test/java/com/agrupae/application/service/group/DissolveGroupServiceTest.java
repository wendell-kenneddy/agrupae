package com.agrupae.application.service.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupDissolutionNotAllowedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.role.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DissolveGroupServiceTest {

    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private AssignmentRepository assignmentRepository;
    private GroupRepository groupRepository;
    private DissolveGroupService service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        groupRepository = mock(GroupRepository.class);
        service = new DissolveGroupService(
                courseRepository, courseMembershipRepository, assignmentRepository, groupRepository);
    }

    private static Course course(UUID courseId, UUID leaderId) {
        Instant now = Instant.now();
        return Course.reconstruct(courseId, leaderId, "Algorithms", "A course",
                "INVITE", false, now, now);
    }

    private static AssignmentFlags flags(boolean dissolveAllowed, boolean supervisorCanEdit) {
        return new AssignmentFlags(4, 10, true, true, dissolveAllowed, false, false, false, supervisorCanEdit);
    }

    private static Assignment assignment(
            UUID assignmentId, UUID courseId, boolean archived, AssignmentFlags flags) {
        Instant now = Instant.now();
        return Assignment.reconstruct(
                assignmentId, courseId, "Assignment 1", "Description",
                flags, archived, now.plusSeconds(86_400), now, now);
    }

    private static Group group(UUID groupId, UUID assignmentId, UUID leaderId) {
        Instant now = Instant.now();
        return Group.reconstruct(groupId, assignmentId, leaderId, "Team Alpha", true, true, now, now);
    }

    @Nested
    class Handle {

        @Test
        void asGroupLeaderWithDissolveAllowed_deletesGroup() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID groupLeaderId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(groupLeaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, courseId, false, flags(true, false)));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, groupLeaderId));

            service.handle(groupLeaderId, Role.USER, courseId, assignmentId, groupId);

            verify(groupRepository).deleteById(groupId);
        }

        @Test
        void asCourseLeaderWithSupervisorEditAllowed_dissolvesGroup() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID courseLeaderId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, courseLeaderId));
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, courseId, false, flags(false, true)));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, UUID.randomUUID()));

            service.handle(courseLeaderId, Role.USER, courseId, assignmentId, groupId);

            verify(groupRepository).deleteById(groupId);
        }

        @Test
        void asAdminWithSupervisorEditAllowed_dissolvesGroup() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID adminId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, courseId, false, flags(false, true)));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, UUID.randomUUID()));

            service.handle(adminId, Role.ADMIN, courseId, assignmentId, groupId);

            verify(groupRepository).deleteById(groupId);
        }

        @Test
        void withCourseNotFound_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(
                    UUID.randomUUID(), Role.USER, courseId, UUID.randomUUID(), UUID.randomUUID()))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(groupRepository, never()).deleteById(any());
        }

        @Test
        void withActorNotEnrolledNorLeader_throwsCourseNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID actorId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(
                    actorId, Role.USER, courseId, UUID.randomUUID(), UUID.randomUUID()))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(groupRepository, never()).deleteById(any());
        }

        @Test
        void withAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID actorId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(
                    actorId, Role.USER, courseId, assignmentId, UUID.randomUUID()))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void withAssignmentFromDifferentCourse_throwsAssignmentNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID actorId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, UUID.randomUUID(), false, flags(true, false)));

            assertThatThrownBy(() -> service.handle(
                    actorId, Role.USER, courseId, assignmentId, UUID.randomUUID()))
                    .isInstanceOf(AssignmentNotFoundException.class);
        }

        @Test
        void withAssignmentArchived_throwsAssignmentArchivedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID actorId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, courseId, true, flags(true, false)));

            assertThatThrownBy(() -> service.handle(
                    actorId, Role.USER, courseId, assignmentId, UUID.randomUUID()))
                    .isInstanceOf(AssignmentArchivedException.class);
        }

        @Test
        void withGroupNotFound_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID actorId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, courseId, false, flags(true, false)));
            when(groupRepository.findById(groupId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId, groupId))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void withGroupFromDifferentAssignment_throwsGroupNotFoundException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID actorId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, courseId, false, flags(true, false)));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, UUID.randomUUID(), actorId));

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId, groupId))
                    .isInstanceOf(GroupNotFoundException.class);
        }

        @Test
        void asGroupLeaderWithDissolveNotAllowed_throwsGroupDissolutionNotAllowedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID groupLeaderId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(groupLeaderId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, courseId, false, flags(false, true)));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, groupLeaderId));

            assertThatThrownBy(() -> service.handle(groupLeaderId, Role.USER, courseId, assignmentId, groupId))
                    .isInstanceOf(GroupDissolutionNotAllowedException.class);
            verify(groupRepository, never()).deleteById(any());
        }

        @Test
        void asCourseLeaderWithSupervisorEditNotAllowed_throwsGroupDissolutionNotAllowedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID courseLeaderId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, courseLeaderId));
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, courseId, false, flags(true, false)));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, UUID.randomUUID()));

            assertThatThrownBy(() -> service.handle(courseLeaderId, Role.USER, courseId, assignmentId, groupId))
                    .isInstanceOf(GroupDissolutionNotAllowedException.class);
            verify(groupRepository, never()).deleteById(any());
        }

        @Test
        void asRegularMemberWithDissolveAllowed_throwsGroupDissolutionNotAllowedException() {
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID memberId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(course(courseId, UUID.randomUUID()));
            when(courseMembershipRepository.exists(memberId, courseId)).thenReturn(true);
            when(assignmentRepository.findById(assignmentId))
                    .thenReturn(assignment(assignmentId, courseId, false, flags(true, false)));
            when(groupRepository.findById(groupId))
                    .thenReturn(group(groupId, assignmentId, UUID.randomUUID()));

            assertThatThrownBy(() -> service.handle(memberId, Role.USER, courseId, assignmentId, groupId))
                    .isInstanceOf(GroupDissolutionNotAllowedException.class);
            verify(groupRepository, never()).deleteById(any());
        }
    }
}

package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.assignment.NotAuthorizedToDeleteAssignmentException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.role.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteAssignmentServiceTest {

    private AssignmentRepository assignmentRepository;
    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private DeleteAssignmentService service;

    @BeforeEach
    void setUp() {
        assignmentRepository = mock(AssignmentRepository.class);
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        service = new DeleteAssignmentService(assignmentRepository, courseRepository, courseMembershipRepository);
    }

    private static AssignmentFlags validFlags() {
        return new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
    }

    private Course buildCourse(UUID id, UUID leaderId) {
        Instant now = Instant.now();
        return Course.reconstruct(id, leaderId, "Algorithms", "A course on algorithms",
                UUID.randomUUID().toString(), false, now, now);
    }

    private Assignment buildAssignment(UUID id, UUID courseId) {
        Instant now = Instant.now();
        return Assignment.reconstruct(id, courseId, "Assignment Name", "Description",
                validFlags(), false, now.plusSeconds(86_400), now, now);
    }

    @Nested
    class Handle {

        @Test
        void asLeader_deletesAssignment() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, actorId);
            Assignment assignment = buildAssignment(assignmentId, courseId);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            service.handle(actorId, Role.USER, courseId, assignmentId);

            verify(assignmentRepository).delete(assignmentId);
        }

        @Test
        void asAdmin_deletesAssignment() {
            UUID actorId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, leaderId);
            Assignment assignment = buildAssignment(assignmentId, courseId);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            service.handle(actorId, Role.ADMIN, courseId, assignmentId);

            verify(assignmentRepository).delete(assignmentId);
        }

        @Test
        void asNonLeaderUser_throwsNotAuthorizedAndDoesNotDelete() {
            UUID actorId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, leaderId);
            Assignment assignment = buildAssignment(assignmentId, courseId);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId))
                    .isInstanceOf(NotAuthorizedToDeleteAssignmentException.class);

            verify(assignmentRepository, never()).delete(assignmentId);
        }

        @Test
        void whenCourseNotFound_throwsCourseNotFoundException() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(null);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId))
                    .isInstanceOf(CourseNotFoundException.class);

            verify(assignmentRepository, never()).delete(assignmentId);
        }

        @Test
        void whenUserIsNotCourseMembers_throwsCourseNotFoundException() {
            UUID actorId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, leaderId);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId))
                    .isInstanceOf(CourseNotFoundException.class);

            verify(assignmentRepository, never()).delete(assignmentId);
        }

        @Test
        void whenAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, actorId);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId))
                    .isInstanceOf(AssignmentNotFoundException.class);

            verify(assignmentRepository, never()).delete(assignmentId);
        }

        @Test
        void whenAssignmentBelongsToDifferentCourse_throwsAssignmentNotFoundException() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID differentCourseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, actorId);
            Assignment assignment = buildAssignment(assignmentId, differentCourseId);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId))
                    .isInstanceOf(AssignmentNotFoundException.class);

            verify(assignmentRepository, never()).delete(assignmentId);
        }

        @Test
        void withNullActorId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(null, Role.USER, UUID.randomUUID(), UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);

            verify(assignmentRepository, never()).delete(null);
        }

        @Test
        void withNullActorRole_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), null, UUID.randomUUID(), UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);

            verify(assignmentRepository, never()).delete(null);
        }

        @Test
        void withNullCourseId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), Role.USER, null, UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);

            verify(assignmentRepository, never()).delete(null);
        }

        @Test
        void withNullAssignmentId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), Role.USER, UUID.randomUUID(), null))
                    .isInstanceOf(NullPointerException.class);

            verify(assignmentRepository, never()).delete(null);
        }
    }
}

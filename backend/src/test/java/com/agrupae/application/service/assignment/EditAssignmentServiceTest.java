package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.assignment.NotAuthorizedToEditAssignmentException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.exception.DomainException;
import com.agrupae.domain.role.Role;

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

class EditAssignmentServiceTest {

    private AssignmentRepository assignmentRepository;
    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private EditAssignmentService service;

    @BeforeEach
    void setUp() {
        assignmentRepository = mock(AssignmentRepository.class);
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        service = new EditAssignmentService(assignmentRepository, courseRepository, courseMembershipRepository);
    }

    private static AssignmentFlags validFlags() {
        return new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
    }

    private Course buildCourse(UUID id, UUID leaderId, boolean archived) {
        Instant now = Instant.now();
        return Course.reconstruct(id, leaderId, "Algorithms", "A course on algorithms",
                UUID.randomUUID().toString(), archived, now, now);
    }

    private Assignment buildAssignment(UUID id, UUID courseId, boolean archived) {
        Instant now = Instant.now();
        Instant dueDate = now.plusSeconds(86_400);
        return Assignment.reconstruct(id, courseId, "Assignment Name", "Description",
                validFlags(), archived, dueDate, now, now);
    }

    @Nested
    class Handle {

        @Test
        void asLeader_updatesAndPersists() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, actorId, false);
            Assignment assignment = buildAssignment(assignmentId, courseId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            String newName = "New Assignment Name";
            String newDesc = "New Description";
            Instant newDueDate = Instant.now().plusSeconds(172_800);
            AssignmentFlags newFlags = new AssignmentFlags(3, 5, true, false, false, false, false, false, true);

            AssignmentView view = service.handle(actorId, Role.USER, courseId, assignmentId, newName, newDesc, newDueDate, newFlags);

            ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
            verify(assignmentRepository).save(captor.capture());

            assertThat(captor.getValue().getId()).isEqualTo(assignmentId);
            assertThat(captor.getValue().getName()).isEqualTo(newName);
            assertThat(captor.getValue().getDescription()).isEqualTo(newDesc);
            assertThat(captor.getValue().getDueDate()).isEqualTo(newDueDate);
            assertThat(captor.getValue().getAssignmentFlags()).isEqualTo(newFlags);

            assertThat(view.id()).isEqualTo(assignmentId);
            assertThat(view.name()).isEqualTo(newName);
            assertThat(view.description()).isEqualTo(newDesc);
            assertThat(view.dueDate()).isEqualTo(newDueDate);
            assertThat(view.assignmentFlags()).isEqualTo(newFlags);
        }

        @Test
        void asAdmin_updatesAndPersists() {
            UUID actorId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, leaderId, false);
            Assignment assignment = buildAssignment(assignmentId, courseId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            String newName = "New Assignment Name";
            String newDesc = "New Description";
            Instant newDueDate = Instant.now().plusSeconds(172_800);
            AssignmentFlags newFlags = new AssignmentFlags(3, 5, true, false, false, false, false, false, true);

            AssignmentView view = service.handle(actorId, Role.ADMIN, courseId, assignmentId, newName, newDesc, newDueDate, newFlags);

            ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
            verify(assignmentRepository).save(captor.capture());

            assertThat(captor.getValue().getId()).isEqualTo(assignmentId);
            assertThat(captor.getValue().getName()).isEqualTo(newName);
            assertThat(view.name()).isEqualTo(newName);
        }

        @Test
        void asNonLeaderUser_throwsNotAuthorizedAndDoesNotSave() {
            UUID actorId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, leaderId, false);
            Assignment assignment = buildAssignment(assignmentId, courseId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            String newName = "New Assignment Name";
            String newDesc = "New Description";
            Instant newDueDate = Instant.now().plusSeconds(172_800);
            AssignmentFlags newFlags = new AssignmentFlags(3, 5, true, false, false, false, false, false, true);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId, newName, newDesc, newDueDate, newFlags))
                    .isInstanceOf(NotAuthorizedToEditAssignmentException.class);

            verify(assignmentRepository, never()).save(any());
            assertThat(assignment.getName()).isEqualTo("Assignment Name");
        }

        @Test
        void whenCourseNotFound_throwsCourseNotFoundException() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId, "Name", "Desc", Instant.now().plusSeconds(86_400), validFlags()))
                    .isInstanceOf(CourseNotFoundException.class);

            verify(assignmentRepository, never()).save(any());
        }

        @Test
        void whenAssignmentNotFound_throwsAssignmentNotFoundException() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            Course course = buildCourse(courseId, actorId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(null);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId, "Name", "Desc", Instant.now().plusSeconds(86_400), validFlags()))
                    .isInstanceOf(AssignmentNotFoundException.class);

            verify(assignmentRepository, never()).save(any());
        }

        @Test
        void whenAssignmentBelongsToDifferentCourse_throwsAssignmentNotFoundException() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID differentCourseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, actorId, false);
            Assignment assignment = buildAssignment(assignmentId, differentCourseId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId, "Name", "Desc", Instant.now().plusSeconds(86_400), validFlags()))
                    .isInstanceOf(AssignmentNotFoundException.class);

            verify(assignmentRepository, never()).save(any());
        }

        @Test
        void whenDomainValidationFails_propagatesDomainExceptionAndDoesNotSave() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();

            Course course = buildCourse(courseId, actorId, false);
            Assignment assignment = buildAssignment(assignmentId, courseId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findById(assignmentId)).thenReturn(assignment);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(true);

            // Invalid because dueDate is in the past
            Instant pastDueDate = Instant.now().minusSeconds(10);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, assignmentId, "Name", "Desc", pastDueDate, validFlags()))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Due date must be in the future.");

            verify(assignmentRepository, never()).save(any());
        }
    }
}

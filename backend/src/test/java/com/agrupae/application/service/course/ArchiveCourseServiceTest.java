package com.agrupae.application.service.course;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.NotAuthorizedToArchiveCourseException;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.application.exception.course.CourseArchivedException;
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

class ArchiveCourseServiceTest {

    private CourseRepository courseRepository;
    private AssignmentRepository assignmentRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private ArchiveCourseService service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        service = new ArchiveCourseService(courseRepository, assignmentRepository, courseMembershipRepository);
    }

    private Course buildCourse(UUID id, UUID leaderId, boolean archived) {
        Instant now = Instant.now();
        return Course.reconstruct(id, leaderId, "Algorithms", "A course on algorithms",
                UUID.randomUUID().toString(), archived, now, now);
    }

    private static AssignmentFlags validFlags() {
        return new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
    }

    private Assignment buildAssignment(UUID id, UUID courseId, boolean archived) {
        Instant now = Instant.now();
        Instant dueDate = now.plusSeconds(86_400);
        return Assignment.reconstruct(id, courseId, "Assignment Name", "Description",
                validFlags(), archived, dueDate, now, now);
    }

    @Nested
    class ArchiveCourse {

        @Test
        void asLeader_archivesAndPersists() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);

            service.handle(leaderId, Role.USER, courseId);
            ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);

            verify(courseRepository).save(captor.capture());

            assertThat(captor.getValue().getId()).isEqualTo(courseId);
            assertThat(captor.getValue().isArchived()).isTrue();
        }

        @Test
        void asAdminWhoIsNotLeader_archivesAndPersists() {
            UUID adminId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(adminId, courseId)).thenReturn(true);

            service.handle(adminId, Role.ADMIN, courseId);
            ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);

            verify(courseRepository).save(captor.capture());

            assertThat(captor.getValue().isArchived()).isTrue();
        }

        @Test
        void asNonLeaderRegularUser_throwsNotAuthorizedAndDoesNotSave() {
            UUID strangerId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(strangerId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(strangerId, Role.USER, courseId))
                    .isInstanceOf(NotAuthorizedToArchiveCourseException.class);
            verify(courseRepository, never()).save(any());
            assertThat(course.isArchived()).isFalse();
        }

        @Test
        void whenCourseNotFound_throwsCourseNotFoundAndDoesNotSave() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(null);
            when(courseMembershipRepository.exists(actorId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void whenAlreadyArchived_propagatesDomainExceptionAndDoesNotSave() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, true);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(leaderId, Role.USER, courseId))
                    .isInstanceOf(CourseArchivedException.class)
                    .hasMessage("Course is archived.");
            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullActorId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(null, Role.USER, UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullActorRole_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), null, UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullCourseId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), Role.USER, null))
                    .isInstanceOf(NullPointerException.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void withActiveAssignments_archivesAllAssignments() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);
            Assignment a1 = buildAssignment(UUID.randomUUID(), courseId, false);
            Assignment a2 = buildAssignment(UUID.randomUUID(), courseId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findByCourseId(courseId)).thenReturn(List.of(a1, a2));
            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);

            service.handle(leaderId, Role.USER, courseId);

            verify(courseRepository).save(course);
            verify(assignmentRepository).saveAll(List.of(a1, a2));
            assertThat(a1.isArchived()).isTrue();
            assertThat(a2.isArchived()).isTrue();
        }

        @Test
        void withMixedAssignments_archivesOnlyActiveOnes() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);
            Assignment a1 = buildAssignment(UUID.randomUUID(), courseId, false);
            Assignment a2 = buildAssignment(UUID.randomUUID(), courseId, true);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findByCourseId(courseId)).thenReturn(List.of(a1, a2));
            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);

            service.handle(leaderId, Role.USER, courseId);

            verify(courseRepository).save(course);
            verify(assignmentRepository).saveAll(List.of(a1));
            assertThat(a1.isArchived()).isTrue();
            assertThat(a2.isArchived()).isTrue();
        }

        @Test
        void withNoAssignments_doesNotCallSaveAll() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findByCourseId(courseId)).thenReturn(List.of());
            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);

            service.handle(leaderId, Role.USER, courseId);

            verify(courseRepository).save(course);
            verify(assignmentRepository, never()).saveAll(any());
        }

        @Test
        void withAllAssignmentsAlreadyArchived_doesNotCallSaveAll() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);
            Assignment a1 = buildAssignment(UUID.randomUUID(), courseId, true);
            Assignment a2 = buildAssignment(UUID.randomUUID(), courseId, true);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(assignmentRepository.findByCourseId(courseId)).thenReturn(List.of(a1, a2));
            when(courseMembershipRepository.exists(leaderId, courseId)).thenReturn(true);

            service.handle(leaderId, Role.USER, courseId);

            verify(courseRepository).save(course);
            verify(assignmentRepository, never()).saveAll(any());
        }
    }
}

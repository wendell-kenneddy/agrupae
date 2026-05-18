package com.agrupae.application.service.course;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.NotAuthorizedToArchiveCourseException;
import com.agrupae.application.port.out.course.CourseRepository;
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

class ArchiveCourseServiceTest {

    private CourseRepository courseRepository;
    private ArchiveCourseService service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        service = new ArchiveCourseService(courseRepository);
    }

    private Course buildCourse(UUID id, UUID leaderId, boolean archived) {
        Instant now = Instant.now();
        return Course.reconstruct(id, leaderId, "Algorithms", "A course on algorithms",
                UUID.randomUUID().toString(), archived, now, now);
    }

    @Nested
    class ArchiveCourse {

        @Test
        void asLeader_archivesAndPersists() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);

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

            assertThatThrownBy(() -> service.handle(leaderId, Role.USER, courseId))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Course is already archived.");
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
    }
}

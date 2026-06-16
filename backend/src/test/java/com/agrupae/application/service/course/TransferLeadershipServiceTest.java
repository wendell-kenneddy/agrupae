package com.agrupae.application.service.course;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.NotAuthorizedToTransferLeadershipException;
import com.agrupae.application.exception.course.TargetUserNotEnrolled;
import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
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

class TransferLeadershipServiceTest {

    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private TransferLeadershipService service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        service = new TransferLeadershipService(courseRepository, courseMembershipRepository);
    }

    private Course buildCourse(UUID id, UUID leaderId, boolean archived) {
        Instant now = Instant.now();
        return Course.reconstruct(id, leaderId, "Algorithms", "A course on algorithms",
                UUID.randomUUID().toString(), archived, now, now);
    }

    @Nested
    class TransferLeadership {

        @Test
        void asLeader_transfersLeadershipPersistsAndReturnsCourseView() {
            UUID leaderId = UUID.randomUUID();
            UUID newLeaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(newLeaderId, courseId)).thenReturn(true);

            CourseView view = service.handle(leaderId, Role.USER, courseId, newLeaderId);
            ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);

            verify(courseRepository).save(captor.capture());
            assertThat(captor.getValue().getId()).isEqualTo(courseId);
            assertThat(captor.getValue().getLeaderId()).isEqualTo(newLeaderId);
            assertThat(view.id()).isEqualTo(courseId);
            assertThat(view.leaderId()).isEqualTo(newLeaderId);
        }

        @Test
        void asAdminWhoIsNotLeader_transfersLeadershipAndPersists() {
            UUID adminId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID newLeaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(newLeaderId, courseId)).thenReturn(true);

            CourseView view = service.handle(adminId, Role.ADMIN, courseId, newLeaderId);
            ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);

            verify(courseRepository).save(captor.capture());
            assertThat(captor.getValue().getLeaderId()).isEqualTo(newLeaderId);
            assertThat(view.leaderId()).isEqualTo(newLeaderId);
        }

        @Test
        void asNonLeaderRegularUser_throwsNotAuthorizedAndDoesNotSave() {
            UUID strangerId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            UUID newLeaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);

            assertThatThrownBy(() -> service.handle(strangerId, Role.USER, courseId, newLeaderId))
                    .isInstanceOf(NotAuthorizedToTransferLeadershipException.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void whenCourseNotFound_throwsCourseNotFoundAndDoesNotSave() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID newLeaderId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(actorId, Role.USER, courseId, newLeaderId))
                    .isInstanceOf(CourseNotFoundException.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void whenNewLeaderIsNotEnrolled_throwsTargetUserNotEnrolledAndDoesNotSave() {
            UUID leaderId = UUID.randomUUID();
            UUID newLeaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, false);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(newLeaderId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(leaderId, Role.USER, courseId, newLeaderId))
                    .isInstanceOf(TargetUserNotEnrolled.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void whenCourseIsArchived_propagatesDomainExceptionAndDoesNotSave() {
            UUID leaderId = UUID.randomUUID();
            UUID newLeaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId, true);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(newLeaderId, courseId)).thenReturn(true);

            assertThatThrownBy(() -> service.handle(leaderId, Role.ADMIN, courseId, newLeaderId))
                    .isInstanceOf(CourseArchivedException.class)
                    .hasMessage("Course is archived.");
            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullActorId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(null, Role.USER, UUID.randomUUID(), UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullActorRole_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), null, UUID.randomUUID(), UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullCourseId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), Role.USER, null, UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);
            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullNewLeaderId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), Role.USER, UUID.randomUUID(), null))
                    .isInstanceOf(NullPointerException.class);
            verify(courseRepository, never()).save(any());
        }
    }
}

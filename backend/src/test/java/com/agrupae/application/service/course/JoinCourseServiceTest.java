package com.agrupae.application.service.course;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.course.AlreadyJoinedCourseException;
import com.agrupae.application.exception.course.InvalidInviteCodeException;
import com.agrupae.application.exception.course.LeaderCannotJoinOwnCourseException;
import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.course.CourseMembership;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JoinCourseServiceTest {

    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private JoinCourseService service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        service = new JoinCourseService(courseRepository, courseMembershipRepository);
    }

    private Course buildCourse(UUID id, UUID leaderId, String inviteCode, boolean archived) {
        Instant now = Instant.now();
        return Course.reconstruct(id, leaderId, "Algorithms", "A course on algorithms",
                inviteCode, archived, now, now);
    }

    @Nested
    class JoinCourse {

        @Test
        void withValidCode_persistsMembershipAndReturnsCourseView() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Course course = buildCourse(courseId, leaderId, inviteCode, false);

            when(courseRepository.findByInviteCode(inviteCode)).thenReturn(course);
            when(courseMembershipRepository.exists(studentId, courseId)).thenReturn(false);
            when(courseMembershipRepository.save(any(CourseMembership.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            CourseView view = service.handle(studentId, inviteCode);
            ArgumentCaptor<CourseMembership> captor = ArgumentCaptor.forClass(CourseMembership.class);

            verify(courseMembershipRepository).save(captor.capture());

            CourseMembership saved = captor.getValue();

            assertThat(saved.getStudentId()).isEqualTo(studentId);
            assertThat(saved.getCourseId()).isEqualTo(courseId);
            assertThat(view.id()).isEqualTo(courseId);
            assertThat(view.leaderId()).isEqualTo(leaderId);
            assertThat(view.name()).isEqualTo("Algorithms");
            assertThat(view.inviteCode()).isEqualTo(inviteCode);
            assertThat(view.archived()).isFalse();
        }

        @Test
        void withUnknownCode_throwsInvalidInviteCodeAndDoesNotSave() {
            UUID studentId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();

            when(courseRepository.findByInviteCode(inviteCode)).thenReturn(null);
            assertThatThrownBy(() -> service.handle(studentId, inviteCode))
                    .isInstanceOf(InvalidInviteCodeException.class)
                    .hasMessage("Invalid invite code.");
            verify(courseMembershipRepository, never()).save(any());
        }

        @Test
        void withArchivedCourse_throwsInvalidInviteCodeAndDoesNotSave() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Course course = buildCourse(courseId, leaderId, inviteCode, true);
            when(courseRepository.findByInviteCode(inviteCode)).thenReturn(course);

            assertThatThrownBy(() -> service.handle(studentId, inviteCode))
                    .isInstanceOf(InvalidInviteCodeException.class)
                    .hasMessage("Invalid invite code.");

            verify(courseMembershipRepository, never()).exists(any(), any());
            verify(courseMembershipRepository, never()).save(any());
        }

        @Test
        void whenLeaderJoinsOwnCourse_throwsLeaderCannotJoinOwnCourseAndDoesNotSave() {
            UUID leaderId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Course course = buildCourse(courseId, leaderId, inviteCode, false);

            when(courseRepository.findByInviteCode(inviteCode)).thenReturn(course);
            assertThatThrownBy(() -> service.handle(leaderId, inviteCode))
                    .isInstanceOf(LeaderCannotJoinOwnCourseException.class);
            verify(courseMembershipRepository, never()).exists(any(), any());
            verify(courseMembershipRepository, never()).save(any());
        }

        @Test
        void whenAlreadyJoined_throwsAlreadyJoinedCourseAndDoesNotSave() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Course course = buildCourse(courseId, leaderId, inviteCode, false);

            when(courseRepository.findByInviteCode(inviteCode)).thenReturn(course);
            when(courseMembershipRepository.exists(studentId, courseId)).thenReturn(true);
            assertThatThrownBy(() -> service.handle(studentId, inviteCode))
                    .isInstanceOf(AlreadyJoinedCourseException.class);
            verify(courseMembershipRepository, never()).save(any());
        }

        @Test
        void trimsInviteCodeBeforeLookup() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            String inviteCode = UUID.randomUUID().toString();
            Course course = buildCourse(courseId, leaderId, inviteCode, false);

            when(courseRepository.findByInviteCode(inviteCode)).thenReturn(course);
            when(courseMembershipRepository.exists(studentId, courseId)).thenReturn(false);
            when(courseMembershipRepository.save(any(CourseMembership.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            service.handle(studentId, "  " + inviteCode + "  ");
            verify(courseRepository).findByInviteCode(eq(inviteCode));
        }

        @Test
        void withNullStudentId_throwsNullPointerException() {
            String inviteCode = UUID.randomUUID().toString();

            assertThatThrownBy(() -> service.handle(null, inviteCode))
                    .isInstanceOf(NullPointerException.class);
            verify(courseMembershipRepository, never()).save(any());
        }

        @Test
        void withNullInviteCode_throwsNullPointerException() {
            UUID studentId = UUID.randomUUID();

            assertThatThrownBy(() -> service.handle(studentId, null))
                    .isInstanceOf(NullPointerException.class);
            verify(courseMembershipRepository, never()).save(any());
        }
    }
}

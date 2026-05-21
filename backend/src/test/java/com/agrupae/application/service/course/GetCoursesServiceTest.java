package com.agrupae.application.service.course;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.course.CourseMembership;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetCoursesServiceTest {

    private CourseMembershipRepository courseMembershipRepository;
    private CourseRepository courseRepository;
    private GetCoursesService service;

    @BeforeEach
    void setUp() {
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        courseRepository = mock(CourseRepository.class);
        service = new GetCoursesService(courseMembershipRepository, courseRepository);
    }

    private Course buildCourse(UUID id, UUID leaderId, String name, boolean archived) {
        Instant now = Instant.now();
        return Course.reconstruct(id, leaderId, name, "Some description",
                UUID.randomUUID().toString(), archived, now, now);
    }

    private CourseMembership buildMembership(UUID studentId, UUID courseId) {
        return CourseMembership.reconstruct(studentId, courseId, Instant.now());
    }

    @Nested
    class Handle {

        @Test
        void withMemberships_returnsMappedPageOfCourseViews() {
            UUID studentId = UUID.randomUUID();
            UUID courseAId = UUID.randomUUID();
            UUID courseBId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);
            Course courseA = buildCourse(courseAId, leaderId, "Algorithms", false);
            Course courseB = buildCourse(courseBId, leaderId, "Data Structures", false);
            List<CourseMembership> memberships = List.of(
                    buildMembership(studentId, courseAId),
                    buildMembership(studentId, courseBId));

            when(courseMembershipRepository.findByStudentId(studentId)).thenReturn(memberships);
            when(courseRepository.findAllByIdIn(any(), eq(pageable)))
                    .thenReturn(new PageImpl<>(List.of(courseA, courseB), pageable, 2));

            Page<CourseView> result = service.handle(studentId, pageable);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<UUID>> captor = ArgumentCaptor.forClass(List.class);
            verify(courseRepository).findAllByIdIn(captor.capture(), eq(pageable));
            assertThat(captor.getValue()).containsExactly(courseAId, courseBId);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).hasSize(2);
            CourseView viewA = result.getContent().get(0);
            assertThat(viewA.id()).isEqualTo(courseAId);
            assertThat(viewA.leaderId()).isEqualTo(leaderId);
            assertThat(viewA.name()).isEqualTo("Algorithms");
            assertThat(viewA.description()).isEqualTo(courseA.getDescription());
            assertThat(viewA.inviteCode()).isEqualTo(courseA.getInviteCode());
            assertThat(viewA.archived()).isFalse();
            assertThat(viewA.createdAt()).isEqualTo(courseA.getCreatedAt());
            assertThat(viewA.updatedAt()).isEqualTo(courseA.getUpdatedAt());
            CourseView viewB = result.getContent().get(1);
            assertThat(viewB.id()).isEqualTo(courseBId);
            assertThat(viewB.name()).isEqualTo("Data Structures");
        }

        @Test
        void withNoMemberships_returnsEmptyPageWithoutQueryingCourses() {
            UUID studentId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            when(courseMembershipRepository.findByStudentId(studentId)).thenReturn(List.of());

            Page<CourseView> result = service.handle(studentId, pageable);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            verify(courseRepository, never()).findAllByIdIn(any(), any());
        }

        @Test
        void passesPageableThroughToCourseRepository() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(2, 5);

            when(courseMembershipRepository.findByStudentId(studentId))
                    .thenReturn(List.of(buildMembership(studentId, courseId)));
            when(courseRepository.findAllByIdIn(any(), eq(pageable)))
                    .thenReturn(Page.empty(pageable));

            service.handle(studentId, pageable);

            verify(courseRepository).findAllByIdIn(any(), eq(pageable));
        }

        @Test
        void withArchivedCourse_mappingPreservesArchivedFlag() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Course archived = buildCourse(courseId, leaderId, "Archived Course", true);
            when(courseMembershipRepository.findByStudentId(studentId))
                    .thenReturn(List.of(buildMembership(studentId, courseId)));
            when(courseRepository.findAllByIdIn(any(), eq(pageable)))
                    .thenReturn(new PageImpl<>(List.of(archived), pageable, 1));

            Page<CourseView> result = service.handle(studentId, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).archived()).isTrue();
        }

        static Stream<Arguments> nullArgCases() {
            GetCoursesService freshService = new GetCoursesService(
                    mock(CourseMembershipRepository.class),
                    mock(CourseRepository.class));
            UUID studentId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);
            return Stream.of(
                    Arguments.of("StudentId", (ThrowingCallable) () -> freshService.handle(null, pageable)),
                    Arguments.of("Pageable",  (ThrowingCallable) () -> freshService.handle(studentId, null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }
}

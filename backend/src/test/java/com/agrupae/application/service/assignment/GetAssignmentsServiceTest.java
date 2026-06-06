package com.agrupae.application.service.assignment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.assignment.AssignmentView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class GetAssignmentsServiceTest {

    private AssignmentRepository assignmentRepository;
    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private GetAssignmentsService service;

    @BeforeEach
    void setUp() {
        assignmentRepository = mock(AssignmentRepository.class);
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        service = new GetAssignmentsService(courseRepository, courseMembershipRepository, assignmentRepository);
    }

    private static Assignment buildAssignment(UUID assignmentId, UUID courseId, String name) {
        Instant now = Instant.now();
        AssignmentFlags flags = new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
        return Assignment.reconstruct(assignmentId, courseId, name, "Description", flags, false, now.plusSeconds(86_400), now, now);
    }

    private static Course course(UUID courseId) {
        Instant now = Instant.now();
        return Course.reconstruct(courseId, UUID.randomUUID(), "Algorithms", "A course",
                UUID.randomUUID().toString(), false, now, now);
    }

    @Nested
    class Handle {

        @Test
        void withValidInputs_returnsPagedMappedView() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID assignmentId1 = UUID.randomUUID();
            UUID assignmentId2 = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Assignment assignment1 = buildAssignment(assignmentId1, courseId, "First assignment");
            Assignment assignment2 = buildAssignment(assignmentId2, courseId, "Second assignment");
            Page<Assignment> assignments = new PageImpl<>(List.of(assignment1, assignment2), pageable, 2);

            when(courseRepository.findById(courseId)).thenReturn(course(courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(true);
            when(assignmentRepository.findByCourseId(courseId, pageable)).thenReturn(assignments);

            Page<AssignmentView> page = service.handle(userId, courseId, pageable);

            assertThat(page.getTotalElements()).isEqualTo(2);
            assertThat(page.getContent()).hasSize(2);
            assertThat(page.getContent().get(0).id()).isEqualTo(assignmentId1);
            assertThat(page.getContent().get(1).id()).isEqualTo(assignmentId2);
            assertThat(page.getContent().get(0).name()).isEqualTo("First assignment");
            assertThat(page.getContent().get(1).name()).isEqualTo("Second assignment");
        }

        @Test
        void withNonExistentCourse_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(userId, courseId, pageable))
                    .isInstanceOf(CourseNotFoundException.class);

            verify(assignmentRepository, never()).findByCourseId(courseId, pageable);
        }

        @Test
        void withUserNotMember_throwsCourseNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            when(courseRepository.findById(courseId)).thenReturn(course(courseId));
            when(courseMembershipRepository.exists(userId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(userId, courseId, pageable))
                    .isInstanceOf(CourseNotFoundException.class);

            verify(assignmentRepository, never()).findByCourseId(courseId, pageable);
        }

        static Stream<Arguments> nullArgCases() {
            AssignmentRepository assignmentRepository = mock(AssignmentRepository.class);
            CourseRepository courseRepository = mock(CourseRepository.class);
            CourseMembershipRepository courseMembershipRepository = mock(CourseMembershipRepository.class);
            GetAssignmentsService freshService = new GetAssignmentsService(courseRepository, courseMembershipRepository, assignmentRepository);

            UUID userId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            return Stream.of(
                    Arguments.of("UserId",
                            (ThrowingCallable) () -> freshService.handle(null, courseId, pageable)),
                    Arguments.of("CourseId",
                            (ThrowingCallable) () -> freshService.handle(userId, null, pageable))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }
}

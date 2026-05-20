package com.agrupae.application.service.course;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.exception.DomainException;
import com.agrupae.application.port.out.course.CourseMembershipRepository;

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

class CreateCourseServiceTest {

    private CourseRepository courseRepository;
    private CreateCourseService service;
    private CourseMembershipRepository courseMembershipRepository;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        service = new CreateCourseService(courseRepository, courseMembershipRepository);
    }

    @Nested
    class CreateCourse {

        @Test
        void withValidInputs_persistsAndReturnsMappedView() {
            UUID leaderId = UUID.randomUUID();
            UUID savedId = UUID.randomUUID();
            String savedInviteCode = UUID.randomUUID().toString();
            Instant now = Instant.now();
            Course persisted = Course.reconstruct(
                    savedId, leaderId, "Algorithms", "A course on algorithms",
                    savedInviteCode, false, now, now);
            when(courseRepository.save(any(Course.class))).thenReturn(persisted);

            CourseView view = service.handle(leaderId, "Algorithms", "A course on algorithms");

            verify(courseRepository).save(any(Course.class));
            assertThat(view.id()).isEqualTo(savedId);
            assertThat(view.leaderId()).isEqualTo(leaderId);
            assertThat(view.name()).isEqualTo("Algorithms");
            assertThat(view.description()).isEqualTo("A course on algorithms");
            assertThat(view.inviteCode()).isEqualTo(savedInviteCode);
            assertThat(view.archived()).isFalse();
            assertThat(view.createdAt()).isEqualTo(now);
            assertThat(view.updatedAt()).isEqualTo(now);
        }

        @Test
        void withNullDescription_returnsViewWithNullDescription() {
            UUID leaderId = UUID.randomUUID();
            when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

            CourseView view = service.handle(leaderId, "Algorithms", null);

            assertThat(view.description()).isNull();
        }

        @Test
        void withBlankName_propagatesDomainException() {
            UUID leaderId = UUID.randomUUID();

            assertThatThrownBy(() -> service.handle(leaderId, "   ", null))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Course name cannot be blank.");

            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullLeaderId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(null, "Algorithms", null))
                    .isInstanceOf(NullPointerException.class);

            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullName_throwsNullPointerException() {
            UUID leaderId = UUID.randomUUID();

            assertThatThrownBy(() -> service.handle(leaderId, null, null))
                    .isInstanceOf(NullPointerException.class);

            verify(courseRepository, never()).save(any());
        }
    }
}

package com.agrupae.domain.course;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourseMembershipTest {

    @Nested
    class Create {

        @Test
        void withValidInputs_returnsPopulatedMembership() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            CourseMembership membership = CourseMembership.create(studentId, courseId);

            assertThat(membership.getStudentId()).isEqualTo(studentId);
            assertThat(membership.getCourseId()).isEqualTo(courseId);
            assertThat(membership.getCreatedAt()).isNotNull();
        }

        @Test
        void eachCall_preservesProvidedIdentifiers() {
            UUID studentAId = UUID.randomUUID();
            UUID studentBId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            CourseMembership a = CourseMembership.create(studentAId, courseId);
            CourseMembership b = CourseMembership.create(studentBId, courseId);

            assertThat(a.getStudentId()).isNotEqualTo(b.getStudentId());
            assertThat(a.getCourseId()).isEqualTo(b.getCourseId());
        }
    }

    @Nested
    class Reconstruct {

        @Test
        void withValidInputs_preservesAllFields() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");

            CourseMembership membership = CourseMembership.reconstruct(studentId, courseId, createdAt);

            assertThat(membership.getStudentId()).isEqualTo(studentId);
            assertThat(membership.getCourseId()).isEqualTo(courseId);
            assertThat(membership.getCreatedAt()).isEqualTo(createdAt);
        }

        static Stream<Arguments> nullArgCases() {
            Instant now = Instant.now();
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            return Stream.of(
                    Arguments.of("StudentId", (ThrowingCallable) () -> CourseMembership.reconstruct(null, courseId, now)),
                    Arguments.of("CourseId",  (ThrowingCallable) () -> CourseMembership.reconstruct(studentId, null, now)),
                    Arguments.of("CreatedAt", (ThrowingCallable) () -> CourseMembership.reconstruct(studentId, courseId, null))
            );
        }

        @ParameterizedTest(name = "withNull{0}_throwsNullPointerException")
        @MethodSource("nullArgCases")
        void withNullArg_throwsNullPointerException(String ignored, ThrowingCallable call) {
            assertThatThrownBy(call).isInstanceOf(NullPointerException.class);
        }
    }
}

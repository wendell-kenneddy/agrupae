package com.agrupae.application.service.course;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetACourseServiceTest {

    private CourseRepository courseRepository;
    private CourseMembershipRepository courseMembershipRepository;
    private GetACourseService service;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseMembershipRepository = mock(CourseMembershipRepository.class);
        service = new GetACourseService(courseRepository, courseMembershipRepository);
    }

    // Helper para evitar repetição: constrói um Course com os dados básicos
    private Course buildCourse(UUID id, UUID leaderId) {
        Instant now = Instant.now();
        return Course.reconstruct(id, leaderId, "Algorithms", "A course on algorithms",
                UUID.randomUUID().toString(), false, now, now);
    }

    @Nested
    class GetACourse {

        /*
         * CENÁRIO 1 — Caminho feliz:
         * O aluno existe e é membro do curso → deve retornar um CourseView
         * com todos os campos mapeados corretamente a partir do Course.
         */
        @Test
        void whenStudentIsMember_returnsCourseView() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(studentId, courseId)).thenReturn(true);

            CourseView result = service.handle(studentId, courseId);

            // Verifica que todos os campos do CourseView foram mapeados do Course
            assertThat(result.id()).isEqualTo(course.getId());
            assertThat(result.leaderId()).isEqualTo(course.getLeaderId());
            assertThat(result.name()).isEqualTo(course.getName());
            assertThat(result.description()).isEqualTo(course.getDescription());
            assertThat(result.inviteCode()).isEqualTo(course.getInviteCode());
            assertThat(result.archived()).isEqualTo(course.isArchived());
            assertThat(result.createdAt()).isEqualTo(course.getCreatedAt());
            assertThat(result.updatedAt()).isEqualTo(course.getUpdatedAt());
        }

        /*
         * CENÁRIO 2 — Curso não existe:
         * O repositório retorna null → deve lançar CourseNotFoundException
         * antes mesmo de checar membership.
         */
        @Test
        void whenCourseNotFound_throwsCourseNotFoundException() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(studentId, courseId))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        /*
         * CENÁRIO 3 — Aluno não é membro:
         * O curso existe, mas o aluno não tem membership → deve lançar
         * CourseNotFoundException (ocultando a existência do curso por segurança).
         */
        @Test
        void whenStudentIsNotMember_throwsCourseNotFoundException() {
            UUID studentId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            Course course = buildCourse(courseId, leaderId);

            when(courseRepository.findById(courseId)).thenReturn(course);
            when(courseMembershipRepository.exists(studentId, courseId)).thenReturn(false);

            assertThatThrownBy(() -> service.handle(studentId, courseId))
                    .isInstanceOf(CourseNotFoundException.class);
        }

        /*
         * CENÁRIO 4 — studentId nulo:
         * @NonNull do Lombok deve disparar NullPointerException imediatamente,
         * sem chegar a consultar os repositórios.
         */
        @Test
        void withNullStudentId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(null, UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);
        }

        /*
         * CENÁRIO 5 — courseId nulo:
         * @NonNull do Lombok deve disparar NullPointerException imediatamente,
         * sem chegar a consultar os repositórios.
         */
        @Test
        void withNullCourseId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}

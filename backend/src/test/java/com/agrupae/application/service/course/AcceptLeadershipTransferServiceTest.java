package com.agrupae.application.service.course;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.LeadershipTransferRequestNotFoundException;
import com.agrupae.application.port.in.course.LeadershipTransferRequestView;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.course.LeadershipTransferRequestRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.course.LeadershipTransferRequest;
import com.agrupae.domain.exception.DomainException;

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

class AcceptLeadershipTransferServiceTest {

    private LeadershipTransferRequestRepository leadershipTransferRequestRepository;
    private CourseRepository courseRepository;
    private LeadershipTransferRequestViewMapper viewMapper;
    private AcceptLeadershipTransferService service;

    @BeforeEach
    void setUp() {
        leadershipTransferRequestRepository = mock(LeadershipTransferRequestRepository.class);
        courseRepository = mock(CourseRepository.class);
        viewMapper = mock(LeadershipTransferRequestViewMapper.class);
        service = new AcceptLeadershipTransferService(leadershipTransferRequestRepository, courseRepository, viewMapper);
    }

    private Course buildCourse(UUID id, UUID leaderId, boolean archived) {
        Instant now = Instant.now();
        return Course.reconstruct(id, leaderId, "Algorithms", "A course on algorithms",
                UUID.randomUUID().toString(), archived, now, now);
    }

    private LeadershipTransferRequest buildPendingRequest(UUID requestId, UUID courseId, UUID senderId, UUID targetId) {
        return LeadershipTransferRequest.create(courseId, senderId, targetId);
    }

    @Nested
    class Handle {

        @Test
        void targetAcceptsPendingRequest_updatesRequestAndCourse() {
            UUID senderId = UUID.randomUUID();
            UUID targetId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();

            LeadershipTransferRequest request = buildPendingRequest(requestId, courseId, senderId, targetId);
            Course course = buildCourse(courseId, senderId, false);

            when(leadershipTransferRequestRepository.findById(request.getId())).thenReturn(request);
            when(courseRepository.findById(courseId)).thenReturn(course);
            when(viewMapper.toView(any(LeadershipTransferRequest.class)))
                    .thenAnswer(inv -> {
                        LeadershipTransferRequest r = inv.getArgument(0);
                        return new LeadershipTransferRequestView(
                                r.getId(), r.getCourseId(), r.getSenderId(), "",
                                r.getTargetId(), "", r.getStatus(), r.getCreatedAt(), r.getUpdatedAt());
                    });

            LeadershipTransferRequestView view = service.handle(targetId, courseId, request.getId());

            verify(leadershipTransferRequestRepository).save(any(LeadershipTransferRequest.class));
            verify(courseRepository).save(any(Course.class));
            assertThat(view.status().name()).isEqualTo("ACCEPTED");
        }

        @Test
        void nonTargetTriesToAccept_throwsDomainException() {
            UUID senderId = UUID.randomUUID();
            UUID targetId = UUID.randomUUID();
            UUID strangerId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            LeadershipTransferRequest request = buildPendingRequest(null, courseId, senderId, targetId);

            when(leadershipTransferRequestRepository.findById(request.getId())).thenReturn(request);

            assertThatThrownBy(() -> service.handle(strangerId, courseId, request.getId()))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Only the target user can accept the transfer request.");

            verify(courseRepository, never()).save(any());
        }

        @Test
        void requestNotFound_throwsLeadershipTransferRequestNotFoundException() {
            UUID actorId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();

            when(leadershipTransferRequestRepository.findById(requestId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(actorId, courseId, requestId))
                    .isInstanceOf(LeadershipTransferRequestNotFoundException.class);

            verify(courseRepository, never()).save(any());
        }

        @Test
        void requestBelongsToAnotherCourse_throwsLeadershipTransferRequestNotFoundException() {
            UUID senderId = UUID.randomUUID();
            UUID targetId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();
            UUID anotherCourseId = UUID.randomUUID();

            LeadershipTransferRequest request = buildPendingRequest(null, anotherCourseId, senderId, targetId);

            when(leadershipTransferRequestRepository.findById(request.getId())).thenReturn(request);

            assertThatThrownBy(() -> service.handle(targetId, courseId, request.getId()))
                    .isInstanceOf(LeadershipTransferRequestNotFoundException.class);

            verify(courseRepository, never()).save(any());
        }

        @Test
        void courseNotFound_throwsCourseNotFoundException() {
            UUID senderId = UUID.randomUUID();
            UUID targetId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            LeadershipTransferRequest request = buildPendingRequest(null, courseId, senderId, targetId);

            when(leadershipTransferRequestRepository.findById(request.getId())).thenReturn(request);
            when(courseRepository.findById(courseId)).thenReturn(null);

            assertThatThrownBy(() -> service.handle(targetId, courseId, request.getId()))
                    .isInstanceOf(CourseNotFoundException.class);

            verify(courseRepository, never()).save(any());
        }

        @Test
        void courseIsArchived_throwsCourseArchivedException() {
            UUID senderId = UUID.randomUUID();
            UUID targetId = UUID.randomUUID();
            UUID courseId = UUID.randomUUID();

            LeadershipTransferRequest request = buildPendingRequest(null, courseId, senderId, targetId);
            Course course = buildCourse(courseId, senderId, true);

            when(leadershipTransferRequestRepository.findById(request.getId())).thenReturn(request);
            when(courseRepository.findById(courseId)).thenReturn(course);

            assertThatThrownBy(() -> service.handle(targetId, courseId, request.getId()))
                    .isInstanceOf(CourseArchivedException.class);

            verify(courseRepository, never()).save(any());
        }

        @Test
        void withNullActorId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(null, UUID.randomUUID(), UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void withNullCourseId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), null, UUID.randomUUID()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void withNullRequestId_throwsNullPointerException() {
            assertThatThrownBy(() -> service.handle(UUID.randomUUID(), UUID.randomUUID(), null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}

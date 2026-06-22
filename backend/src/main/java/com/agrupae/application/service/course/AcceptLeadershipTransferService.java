package com.agrupae.application.service.course;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.LeadershipTransferRequestNotFoundException;
import com.agrupae.application.port.in.course.AcceptLeadershipTransferUseCase;
import com.agrupae.application.port.in.course.LeadershipTransferRequestView;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.course.LeadershipTransferRequestRepository;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.course.LeadershipTransferRequest;
import com.agrupae.domain.exception.DomainException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AcceptLeadershipTransferService implements AcceptLeadershipTransferUseCase {
    private final LeadershipTransferRequestRepository leadershipTransferRequestRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LeadershipTransferRequestView handle(
            @NonNull UUID actorId,
            @NonNull UUID courseId,
            @NonNull UUID requestId) {
        LeadershipTransferRequest request = this.leadershipTransferRequestRepository.findById(requestId);

        if (request == null || !request.getCourseId().equals(courseId)) {
            throw new LeadershipTransferRequestNotFoundException();
        }

        if (!request.getTargetId().equals(actorId)) {
            throw new DomainException("Only the target user can accept the transfer request.");
        }

        Course course = this.courseRepository.findById(courseId);

        if (course == null) {
            throw new CourseNotFoundException();
        }

        if (course.isArchived()) {
            throw new CourseArchivedException();
        }

        // Apply acceptance to request
        request.accept();
        this.leadershipTransferRequestRepository.save(request);

        // Update Course ownership
        course.transferLeadership(request.getTargetId());
        this.courseRepository.save(course);

        com.agrupae.domain.user.User sender = this.userRepository.findById(request.getSenderId());
        com.agrupae.domain.user.User target = this.userRepository.findById(request.getTargetId());

        return new LeadershipTransferRequestView(
                request.getId(),
                request.getCourseId(),
                request.getSenderId(),
                sender != null ? sender.getName() : "",
                request.getTargetId(),
                target != null ? target.getName() : "",
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt());
    }
}

package com.agrupae.application.service.course;

import java.util.List;
import java.util.UUID;

import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.TargetUserNotEnrolled;
import com.agrupae.application.exception.course.NotAuthorizedToTransferLeadershipException;
import com.agrupae.application.port.in.course.TransferLeadershipUseCase;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.course.LeadershipTransferRequestRepository;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.course.LeadershipTransferRequest;
import com.agrupae.domain.course.LeadershipTransferRequestStatus;
import com.agrupae.domain.role.Role;
import com.agrupae.application.port.in.course.LeadershipTransferRequestView;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransferLeadershipService implements TransferLeadershipUseCase {
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final LeadershipTransferRequestRepository leadershipTransferRequestRepository;
    private final UserRepository userRepository;

    @Override
    public LeadershipTransferRequestView handle(
            @NonNull UUID actorId,
            @NonNull Role actorRole,
            @NonNull UUID courseId,
            @NonNull UUID newLeaderId) {
        Course course = this.courseRepository.findById(courseId);

        if (course == null) {
            throw new CourseNotFoundException();
        }

        if (course.isArchived()) {
            throw new CourseArchivedException();
        }

        if (actorRole != Role.ADMIN && !course.getLeaderId().equals(actorId)) {
            throw new NotAuthorizedToTransferLeadershipException();
        }

        if (newLeaderId.equals(course.getLeaderId())) {
            throw new com.agrupae.domain.exception.DomainException("User is already the leader of the group.");
        }

        if (!courseMembershipRepository.exists(newLeaderId, courseId)) {
            throw new TargetUserNotEnrolled();
        }

        // Cancel any pending transfer request for this course
        List<LeadershipTransferRequest> pendingRequests = this.leadershipTransferRequestRepository
                .findByCourseIdAndStatus(courseId, LeadershipTransferRequestStatus.PENDING);
        for (LeadershipTransferRequest pending : pendingRequests) {
            pending.reject();
            this.leadershipTransferRequestRepository.save(pending);
        }

        // Create new pending request
        LeadershipTransferRequest request = LeadershipTransferRequest.create(courseId, actorId, newLeaderId);
        LeadershipTransferRequest saved = this.leadershipTransferRequestRepository.save(request);

        com.agrupae.domain.user.User sender = this.userRepository.findById(actorId);
        com.agrupae.domain.user.User target = this.userRepository.findById(newLeaderId);

        return new LeadershipTransferRequestView(
                saved.getId(),
                saved.getCourseId(),
                saved.getSenderId(),
                sender != null ? sender.getName() : "",
                saved.getTargetId(),
                target != null ? target.getName() : "",
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getUpdatedAt());
    }
}

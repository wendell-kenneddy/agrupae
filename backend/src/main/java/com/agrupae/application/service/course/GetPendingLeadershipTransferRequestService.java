package com.agrupae.application.service.course;

import java.util.List;
import java.util.UUID;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.port.in.course.GetPendingLeadershipTransferRequestUseCase;
import com.agrupae.application.port.in.course.LeadershipTransferRequestView;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.course.LeadershipTransferRequestRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.course.LeadershipTransferRequest;
import com.agrupae.domain.course.LeadershipTransferRequestStatus;
import com.agrupae.domain.exception.DomainException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetPendingLeadershipTransferRequestService implements GetPendingLeadershipTransferRequestUseCase {
    private final LeadershipTransferRequestRepository leadershipTransferRequestRepository;
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final LeadershipTransferRequestViewMapper viewMapper;

    @Override
    public LeadershipTransferRequestView handle(
            @NonNull UUID actorId,
            @NonNull UUID courseId) {
        Course course = this.courseRepository.findById(courseId);

        if (course == null) {
            throw new CourseNotFoundException();
        }

        // Verify that actor is enrolled in the course OR is the course leader
        boolean isMember = this.courseMembershipRepository.exists(actorId, courseId);
        boolean isLeader = course.getLeaderId().equals(actorId);
        if (!isMember && !isLeader) {
            throw new DomainException("User is not enrolled in this course.");
        }

        List<LeadershipTransferRequest> pendingRequests = this.leadershipTransferRequestRepository
                .findByCourseIdAndStatus(courseId, LeadershipTransferRequestStatus.PENDING);

        if (pendingRequests.isEmpty()) {
            return null; // No pending request
        }

        // Get the active pending request
        LeadershipTransferRequest request = pendingRequests.get(0);

        // Verify authorization: only the sender or target can view it
        if (!request.getSenderId().equals(actorId) && !request.getTargetId().equals(actorId)) {
            return null;
        }

        return this.viewMapper.toView(request);
    }
}


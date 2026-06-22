package com.agrupae.application.port.out.course;

import java.util.List;
import java.util.UUID;

import com.agrupae.domain.course.LeadershipTransferRequest;
import com.agrupae.domain.course.LeadershipTransferRequestStatus;

public interface LeadershipTransferRequestRepository {
    LeadershipTransferRequest save(LeadershipTransferRequest request);

    LeadershipTransferRequest findById(UUID id);

    List<LeadershipTransferRequest> findByCourseId(UUID courseId);

    List<LeadershipTransferRequest> findByCourseIdAndStatus(UUID courseId, LeadershipTransferRequestStatus status);
}

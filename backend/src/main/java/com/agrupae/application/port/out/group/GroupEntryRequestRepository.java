package com.agrupae.application.port.out.group;

import java.util.List;
import java.util.UUID;

import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.domain.group.GroupEntryRequestStatus;

public interface GroupEntryRequestRepository {
    GroupEntryRequest save(GroupEntryRequest request);

    GroupEntryRequest findById(UUID id);

    boolean existsPendingByAssignmentIdAndUserId(UUID assignmentId, UUID userId);

    List<GroupEntryRequest> findByAssignmentIdAndUserId(UUID assignmentId, UUID userId);

    List<GroupEntryRequest> findByGroupId(UUID groupId);

    List<GroupEntryRequest> findByGroupIdAndStatus(UUID groupId, GroupEntryRequestStatus status);

    void deleteById(UUID id);

    void deleteAllPendingByGroupId(UUID groupId);
}

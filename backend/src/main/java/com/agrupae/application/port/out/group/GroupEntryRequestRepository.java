package com.agrupae.application.port.out.group;

import java.util.List;
import java.util.UUID;

import com.agrupae.domain.group.GroupEntryRequest;

public interface GroupEntryRequestRepository {
    GroupEntryRequest save(GroupEntryRequest request);

    GroupEntryRequest findById(UUID id);

    boolean existsPendingByAssignmentIdAndUserId(UUID assignmentId, UUID userId);

    List<GroupEntryRequest> findByAssignmentIdAndUserId(UUID assignmentId, UUID userId);

    void deleteById(UUID id);

    void deleteAllPendingByGroupId(UUID groupId);
}

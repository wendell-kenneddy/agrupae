package com.agrupae.application.port.in.group;

import java.util.List;
import java.util.UUID;
import com.agrupae.domain.group.GroupEntryRequestStatus;

public interface GetGroupEntryRequestsUseCase {
    List<GroupEntryRequestView> handle(UUID courseId, UUID assignmentId, UUID groupId, UUID userId, GroupEntryRequestStatus status);
}

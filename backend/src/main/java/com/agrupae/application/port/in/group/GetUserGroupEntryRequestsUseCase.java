package com.agrupae.application.port.in.group;

import java.util.List;
import java.util.UUID;

public interface GetUserGroupEntryRequestsUseCase {
    List<GroupEntryRequestView> handle(UUID courseId, UUID assignmentId, UUID userId);
}

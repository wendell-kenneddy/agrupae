package com.agrupae.application.port.in.group;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetGroupMembersUseCase {
    Page<GroupMemberView> handle(UUID userId, UUID courseId, UUID assignmentId, UUID groupId, Pageable pageable);
}

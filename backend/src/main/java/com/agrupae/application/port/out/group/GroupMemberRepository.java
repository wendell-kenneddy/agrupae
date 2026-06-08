package com.agrupae.application.port.out.group;

import java.util.UUID;

import com.agrupae.domain.group.GroupMember;

public interface GroupMemberRepository {
    GroupMember save(GroupMember groupMember);

    boolean existsByAssignmentIdAndMemberId(UUID assignmentId, UUID memberId);
}

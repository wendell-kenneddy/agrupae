package com.agrupae.application.port.out.group;

import java.util.UUID;

import com.agrupae.domain.group.GroupMember;

public interface GroupMemberRepository {
    GroupMember save(GroupMember groupMember);

    boolean existsByAssignmentIdAndMemberId(UUID assignmentId, UUID memberId);

    boolean existsByGroupIdAndMemberId(UUID groupId, UUID memberId);

    int countByGroupId(UUID groupId);

    void deleteByGroupIdAndMemberId(UUID groupId, UUID memberId);

    UUID findOldestMemberIdExcluding(UUID groupId, UUID excludeMemberId);

    UUID findGroupIdByAssignmentIdAndMemberId(UUID assignmentId, UUID memberId);
}

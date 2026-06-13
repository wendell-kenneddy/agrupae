package com.agrupae.infrastructure.persistence.jpa.repository.group;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrupae.infrastructure.persistence.jpa.model.group.GroupMemberJpaEntity;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupMemberId;

@Repository
public interface GroupMemberJpaRepository extends JpaRepository<GroupMemberJpaEntity, GroupMemberId> {

    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END " +
           "FROM GroupMemberJpaEntity gm " +
           "JOIN GroupJpaEntity g ON gm.groupId = g.id " +
           "WHERE g.assignmentId = :assignmentId AND gm.memberId = :memberId")
    boolean existsByAssignmentIdAndMemberId(
            @Param("assignmentId") UUID assignmentId,
            @Param("memberId") UUID memberId);

    int countByGroupId(UUID groupId);

    GroupMemberJpaEntity findFirstByGroupIdAndMemberIdNotOrderByCreatedAtAsc(UUID groupId, UUID memberId);

    @Query("SELECT gm.groupId FROM GroupMemberJpaEntity gm " +
           "JOIN GroupJpaEntity g ON gm.groupId = g.id " +
           "WHERE g.assignmentId = :assignmentId AND gm.memberId = :memberId")
    UUID findGroupIdByAssignmentIdAndMemberId(
            @Param("assignmentId") UUID assignmentId,
            @Param("memberId") UUID memberId);
}

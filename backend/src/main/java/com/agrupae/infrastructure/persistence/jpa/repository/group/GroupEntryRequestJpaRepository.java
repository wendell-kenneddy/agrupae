package com.agrupae.infrastructure.persistence.jpa.repository.group;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrupae.domain.group.GroupEntryRequestStatus;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupEntryRequestJpaEntity;

@Repository
public interface GroupEntryRequestJpaRepository extends JpaRepository<GroupEntryRequestJpaEntity, UUID> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM GroupEntryRequestJpaEntity r " +
           "JOIN GroupJpaEntity g ON r.groupId = g.id " +
           "WHERE g.assignmentId = :assignmentId AND r.userId = :userId " +
           "AND r.status = :status")
    boolean existsByAssignmentIdAndUserIdAndStatus(
            @Param("assignmentId") UUID assignmentId,
            @Param("userId") UUID userId,
            @Param("status") GroupEntryRequestStatus status);

    @Query("SELECT r FROM GroupEntryRequestJpaEntity r " +
           "JOIN GroupJpaEntity g ON r.groupId = g.id " +
           "WHERE g.assignmentId = :assignmentId AND r.userId = :userId")
    List<GroupEntryRequestJpaEntity> findByAssignmentIdAndUserId(
            @Param("assignmentId") UUID assignmentId,
            @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM GroupEntryRequestJpaEntity r " +
           "WHERE r.groupId = :groupId AND r.status = :status")
    void deleteByGroupIdAndStatus(
            @Param("groupId") UUID groupId,
            @Param("status") GroupEntryRequestStatus status);
}

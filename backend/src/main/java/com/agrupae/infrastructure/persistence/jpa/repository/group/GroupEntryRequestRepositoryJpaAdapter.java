package com.agrupae.infrastructure.persistence.jpa.repository.group;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.domain.group.GroupEntryRequestStatus;
import com.agrupae.infrastructure.persistence.jpa.mapper.GroupEntryRequestJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupEntryRequestJpaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GroupEntryRequestRepositoryJpaAdapter implements GroupEntryRequestRepository {
    private final GroupEntryRequestJpaRepository groupEntryRequestJpaRepository;
    private final GroupEntryRequestJpaEntityMapper groupEntryRequestJpaEntityMapper;

    @Override
    public GroupEntryRequest save(GroupEntryRequest request) {
        GroupEntryRequestJpaEntity entity = this.groupEntryRequestJpaEntityMapper.toEntity(request);
        GroupEntryRequestJpaEntity saved = this.groupEntryRequestJpaRepository.save(entity);
        return this.groupEntryRequestJpaEntityMapper.toDomain(saved);
    }

    @Override
    public GroupEntryRequest findById(UUID id) {
        return this.groupEntryRequestJpaRepository.findById(id)
                .map(this.groupEntryRequestJpaEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public boolean existsPendingByAssignmentIdAndUserId(UUID assignmentId, UUID userId) {
        return this.groupEntryRequestJpaRepository.existsByAssignmentIdAndUserIdAndStatus(
                assignmentId, userId, GroupEntryRequestStatus.PENDING);
    }

    @Override
    public List<GroupEntryRequest> findByAssignmentIdAndUserId(UUID assignmentId, UUID userId) {
        return this.groupEntryRequestJpaRepository.findByAssignmentIdAndUserId(assignmentId, userId)
                .stream()
                .map(this.groupEntryRequestJpaEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        this.groupEntryRequestJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllPendingByGroupId(UUID groupId) {
        this.groupEntryRequestJpaRepository.deleteByGroupIdAndStatus(groupId, GroupEntryRequestStatus.PENDING);
    }

    @Override
    public List<GroupEntryRequest> findByGroupId(UUID groupId) {
        return this.groupEntryRequestJpaRepository.findByGroupId(groupId)
                .stream()
                .map(this.groupEntryRequestJpaEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupEntryRequest> findByGroupIdAndStatus(UUID groupId, GroupEntryRequestStatus status) {
        return this.groupEntryRequestJpaRepository.findByGroupIdAndStatus(groupId, status)
                .stream()
                .map(this.groupEntryRequestJpaEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}

package com.agrupae.infrastructure.persistence.jpa.repository.group;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.domain.group.GroupMember;
import com.agrupae.infrastructure.persistence.jpa.mapper.GroupMemberJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupMemberId;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupMemberJpaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GroupMemberRepositoryJpaAdapter implements GroupMemberRepository {
    private final GroupMemberJpaRepository groupMemberJpaRepository;
    private final GroupMemberJpaEntityMapper groupMemberJpaEntityMapper;

    @Override
    public GroupMember save(GroupMember groupMember) {
        GroupMemberJpaEntity entity = this.groupMemberJpaEntityMapper.toEntity(groupMember);
        GroupMemberJpaEntity saved = this.groupMemberJpaRepository.save(entity);
        return this.groupMemberJpaEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existsByAssignmentIdAndMemberId(UUID assignmentId, UUID memberId) {
        return this.groupMemberJpaRepository.existsByAssignmentIdAndMemberId(assignmentId, memberId);
    }

    @Override
    public boolean existsByGroupIdAndMemberId(UUID groupId, UUID memberId) {
        return this.groupMemberJpaRepository.existsById(new GroupMemberId(groupId, memberId));
    }

    @Override
    public int countByGroupId(UUID groupId) {
        return this.groupMemberJpaRepository.countByGroupId(groupId);
    }

    @Override
    public void deleteByGroupIdAndMemberId(UUID groupId, UUID memberId) {
        this.groupMemberJpaRepository.deleteById(new GroupMemberId(groupId, memberId));
    }

    @Override
    public UUID findOldestMemberIdExcluding(UUID groupId, UUID excludeMemberId) {
        GroupMemberJpaEntity entity = this.groupMemberJpaRepository
                .findFirstByGroupIdAndMemberIdNotOrderByCreatedAtAsc(groupId, excludeMemberId);

        if (entity == null) {
            return null;
        }

        return this.groupMemberJpaEntityMapper.toDomain(entity).memberId();
    }
}


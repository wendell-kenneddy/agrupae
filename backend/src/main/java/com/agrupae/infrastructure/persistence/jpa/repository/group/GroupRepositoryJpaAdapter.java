package com.agrupae.infrastructure.persistence.jpa.repository.group;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.group.Group;
import com.agrupae.infrastructure.persistence.jpa.mapper.GroupJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupJpaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryJpaAdapter implements GroupRepository {
    private final GroupJpaRepository groupJpaRepository;
    private final GroupJpaEntityMapper groupJpaEntityMapper;

    @Override
    public Group save(Group group) {
        GroupJpaEntity entity = this.groupJpaEntityMapper.toEntity(group);
        GroupJpaEntity saved = this.groupJpaRepository.save(entity);
        return this.groupJpaEntityMapper.toDomain(saved);
    }

    @Override
    public Group findById(UUID id) {
        return this.groupJpaRepository
                .findById(id)
                .map(this.groupJpaEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public int countByAssignmentId(UUID assignmentId) {
        return this.groupJpaRepository.countByAssignmentId(assignmentId);
    }
}

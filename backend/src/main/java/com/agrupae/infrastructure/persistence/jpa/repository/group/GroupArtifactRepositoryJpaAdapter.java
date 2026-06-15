package com.agrupae.infrastructure.persistence.jpa.repository.group;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.agrupae.application.port.out.group.GroupArtifactRepository;
import com.agrupae.domain.group.GroupArtifact;
import com.agrupae.infrastructure.persistence.jpa.mapper.GroupArtifactJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupArtifactJpaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GroupArtifactRepositoryJpaAdapter implements GroupArtifactRepository {
    private final GroupArtifactJpaRepository groupArtifactJpaRepository;
    private final GroupArtifactJpaEntityMapper groupArtifactJpaEntityMapper;

    @Override
    public GroupArtifact save(GroupArtifact artifact) {
        GroupArtifactJpaEntity entity = this.groupArtifactJpaEntityMapper.toEntity(artifact);
        GroupArtifactJpaEntity saved = this.groupArtifactJpaRepository.save(entity);
        return this.groupArtifactJpaEntityMapper.toDomain(saved);
    }

    @Override
    public List<GroupArtifact> findByGroupId(UUID groupId) {
        return this.groupArtifactJpaRepository
                .findByGroupId(groupId)
                .stream()
                .map(this.groupArtifactJpaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<GroupArtifact> findPublicByGroupId(UUID groupId) {
        return this.groupArtifactJpaRepository
                .findByGroupIdAndPrivateArtifactFalse(groupId)
                .stream()
                .map(this.groupArtifactJpaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public GroupArtifact findById(UUID id) {
        return this.groupArtifactJpaRepository
                .findById(id)
                .map(this.groupArtifactJpaEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public void deleteById(UUID id) {
        this.groupArtifactJpaRepository.deleteById(id);
    }
}

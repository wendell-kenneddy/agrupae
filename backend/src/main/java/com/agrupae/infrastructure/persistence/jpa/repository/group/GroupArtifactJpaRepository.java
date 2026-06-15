package com.agrupae.infrastructure.persistence.jpa.repository.group;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrupae.infrastructure.persistence.jpa.model.group.GroupArtifactJpaEntity;

public interface GroupArtifactJpaRepository extends JpaRepository<GroupArtifactJpaEntity, UUID> {
    List<GroupArtifactJpaEntity> findByGroupId(UUID groupId);
    List<GroupArtifactJpaEntity> findByGroupIdAndPrivateArtifactFalse(UUID groupId);
}

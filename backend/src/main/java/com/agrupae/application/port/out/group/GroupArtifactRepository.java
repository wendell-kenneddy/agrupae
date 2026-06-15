package com.agrupae.application.port.out.group;

import java.util.List;
import java.util.UUID;

import com.agrupae.domain.group.GroupArtifact;

public interface GroupArtifactRepository {
    GroupArtifact save(GroupArtifact artifact);
    List<GroupArtifact> findByGroupId(UUID groupId);
    List<GroupArtifact> findPublicByGroupId(UUID groupId);
    GroupArtifact findById(UUID id);
    void deleteById(UUID id);
}

package com.agrupae.domain.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.exception.DomainException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupArtifactTest {

    @Nested
    class Create {

        @Test
        void withValidInputs_createsGroupArtifact() {
            UUID groupId = UUID.randomUUID();

            GroupArtifact artifact = GroupArtifact.create(groupId, "Artifact 1", "Description of artifact", true,
                    "http://link.com");

            assertThat(artifact.getId()).isNotNull();
            assertThat(artifact.getGroupId()).isEqualTo(groupId);
            assertThat(artifact.getName()).isEqualTo("Artifact 1");
            assertThat(artifact.getDescription()).isEqualTo("Description of artifact");
            assertThat(artifact.isPrivateArtifact()).isTrue();
            assertThat(artifact.getResourceLink()).isEqualTo("http://link.com");
            assertThat(artifact.getCreatedAt()).isNotNull();
            assertThat(artifact.getUpdatedAt()).isEqualTo(artifact.getCreatedAt());
        }

        @Test
        void withBlankName_throwsDomainException() {
            assertThatThrownBy(() -> GroupArtifact.create(
                    UUID.randomUUID(), "   ", "Description", true, "http://link.com"))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Group artifact name cannot be blank.");
        }

        @Test
        void withBlankLink_throwsDomainException() {
            assertThatThrownBy(() -> GroupArtifact.create(
                    UUID.randomUUID(), "Artifact 1", "Description", true, "   "))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Resource link cannot be blank.");
        }
    }

    @Nested
    class Reconstruct {

        @Test
        void withValidInputs_reconstructsGroupArtifact() {
            UUID id = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            Instant now = Instant.now();

            GroupArtifact artifact = GroupArtifact.reconstruct(
                    id, groupId, "Artifact 2", "Desc", false, "http://link.com", now, now);

            assertThat(artifact.getId()).isEqualTo(id);
            assertThat(artifact.getGroupId()).isEqualTo(groupId);
            assertThat(artifact.getName()).isEqualTo("Artifact 2");
            assertThat(artifact.getDescription()).isEqualTo("Desc");
            assertThat(artifact.isPrivateArtifact()).isFalse();
            assertThat(artifact.getResourceLink()).isEqualTo("http://link.com");
            assertThat(artifact.getCreatedAt()).isEqualTo(now);
            assertThat(artifact.getUpdatedAt()).isEqualTo(now);
        }

        @Test
        void withUpdatedAtBeforeCreatedAt_throwsDomainException() {
            Instant createdAt = Instant.now();
            Instant updatedAt = createdAt.minusSeconds(60);

            assertThatThrownBy(() -> GroupArtifact.reconstruct(
                    UUID.randomUUID(), UUID.randomUUID(), "Artifact", "Desc", true, "http://link.com", createdAt,
                    updatedAt))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Update timestamp cannot be before creation timestamp.");
        }
    }

    @Nested
    class EditDetails {

        @Test
        void withValidInputs_updatesDetailsAndTimestamp() {
            GroupArtifact artifact = GroupArtifact.create(UUID.randomUUID(), "Artifact 1", "Desc", true,
                    "http://link.com");
            Instant originalUpdatedAt = artifact.getUpdatedAt();

            artifact.editDetails("New Name", "New Desc", "http://newlink.com");

            assertThat(artifact.getName()).isEqualTo("New Name");
            assertThat(artifact.getDescription()).isEqualTo("New Desc");
            assertThat(artifact.getResourceLink()).isEqualTo("http://newlink.com");
            assertThat(artifact.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        }

        @Test
        void withBlankName_throwsDomainException() {
            GroupArtifact artifact = GroupArtifact.create(UUID.randomUUID(), "Artifact 1", "Desc", true,
                    "http://link.com");

            assertThatThrownBy(() -> artifact.editDetails("  ", "New Desc", "http://newlink.com"))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Group artifact name cannot be blank.");
        }

        @Test
        void withBlankLink_throwsDomainException() {
            GroupArtifact artifact = GroupArtifact.create(UUID.randomUUID(), "Artifact 1", "Desc", true,
                    "http://link.com");

            assertThatThrownBy(() -> artifact.editDetails("New Name", "New Desc", "  "))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Resource link cannot be blank.");
        }
    }

    @Nested
    class Privacy {

        @Test
        void makesPrivate() {
            GroupArtifact artifact = GroupArtifact.create(UUID.randomUUID(), "Artifact 1", "Desc", false,
                    "http://link.com");

            artifact.makePrivate();
            assertThat(artifact.isPrivateArtifact()).isTrue();
        }

        @Test
        void makesPublic() {
            GroupArtifact artifact = GroupArtifact.create(UUID.randomUUID(), "Artifact 1", "Desc", true,
                    "http://link.com");

            artifact.makePublic();
            assertThat(artifact.isPrivateArtifact()).isFalse();
        }
    }
}

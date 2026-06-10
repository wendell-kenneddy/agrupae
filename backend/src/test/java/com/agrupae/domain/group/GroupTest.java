package com.agrupae.domain.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.exception.DomainException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupTest {

    @Nested
    class Create {

        @Test
        void withValidInputs_createsGroup() {
            UUID assignmentId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();

            Group group = Group.create(assignmentId, leaderId, "Team Alpha", true, true);

            assertThat(group.getId()).isNotNull();
            assertThat(group.getAssignmentId()).isEqualTo(assignmentId);
            assertThat(group.getLeaderId()).isEqualTo(leaderId);
            assertThat(group.getName()).isEqualTo("Team Alpha");
            assertThat(group.isOpen()).isTrue();
            assertThat(group.isMembersCanEditArtifacts()).isTrue();
            assertThat(group.getCreatedAt()).isNotNull();
            assertThat(group.getUpdatedAt()).isEqualTo(group.getCreatedAt());
        }

        @Test
        void withBlankName_throwsDomainException() {
            assertThatThrownBy(() -> Group.create(
                    UUID.randomUUID(), UUID.randomUUID(), "   ", true, true))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Group name cannot be blank.");
        }

        @Test
        void withNullName_throwsNullPointerException() {
            assertThatThrownBy(() -> Group.create(
                    UUID.randomUUID(), UUID.randomUUID(), null, true, true))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void withNullAssignmentId_throwsNullPointerException() {
            assertThatThrownBy(() -> Group.create(
                    null, UUID.randomUUID(), "Team Alpha", true, true))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void withNullLeaderId_throwsNullPointerException() {
            assertThatThrownBy(() -> Group.create(
                    UUID.randomUUID(), null, "Team Alpha", true, true))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class Reconstruct {

        @Test
        void withValidInputs_reconstructsGroup() {
            UUID id = UUID.randomUUID();
            UUID assignmentId = UUID.randomUUID();
            UUID leaderId = UUID.randomUUID();
            Instant now = Instant.now();

            Group group = Group.reconstruct(
                    id, assignmentId, leaderId, "Team Beta", false, true, now, now);

            assertThat(group.getId()).isEqualTo(id);
            assertThat(group.getAssignmentId()).isEqualTo(assignmentId);
            assertThat(group.getLeaderId()).isEqualTo(leaderId);
            assertThat(group.getName()).isEqualTo("Team Beta");
            assertThat(group.isOpen()).isFalse();
            assertThat(group.isMembersCanEditArtifacts()).isTrue();
            assertThat(group.getCreatedAt()).isEqualTo(now);
            assertThat(group.getUpdatedAt()).isEqualTo(now);
        }

        @Test
        void withUpdatedAtBeforeCreatedAt_throwsDomainException() {
            Instant createdAt = Instant.now();
            Instant updatedAt = createdAt.minusSeconds(60);

            assertThatThrownBy(() -> Group.reconstruct(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    "Team", true, true, createdAt, updatedAt))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Update timestamp cannot be before creation timestamp.");
        }
    }

    @Nested
    class EditName {

        @Test
        void withValidName_updatesNameAndTimestamp() {
            Group group = Group.create(UUID.randomUUID(), UUID.randomUUID(), "Old Name", true, true);
            Instant originalUpdatedAt = group.getUpdatedAt();

            group.editName("New Name");

            assertThat(group.getName()).isEqualTo("New Name");
            assertThat(group.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        }

        @Test
        void withBlankName_throwsDomainException() {
            Group group = Group.create(UUID.randomUUID(), UUID.randomUUID(), "Team", true, true);

            assertThatThrownBy(() -> group.editName("   "))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Group name cannot be blank.");
        }

        @Test
        void withNullName_throwsDomainException() {
            Group group = Group.create(UUID.randomUUID(), UUID.randomUUID(), "Team", true, true);

            assertThatThrownBy(() -> group.editName(null))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Group name cannot be blank.");
        }
    }

    @Nested
    class TransferLeadership {

        @Test
        void withDifferentUser_updatesLeaderAndTimestamp() {
            UUID originalLeader = UUID.randomUUID();
            UUID newLeader = UUID.randomUUID();
            Group group = Group.create(UUID.randomUUID(), originalLeader, "Team", true, true);

            group.transferLeadership(newLeader);

            assertThat(group.getLeaderId()).isEqualTo(newLeader);
        }

        @Test
        void withSameUser_throwsDomainException() {
            UUID leader = UUID.randomUUID();
            Group group = Group.create(UUID.randomUUID(), leader, "Team", true, true);

            assertThatThrownBy(() -> group.transferLeadership(leader))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("User is already the leader of the group.");
        }
    }

    @Nested
    class ChangeMode {

        @Test
        void changesOpenToClosed() {
            Group group = Group.create(UUID.randomUUID(), UUID.randomUUID(), "Team", true, true);

            group.changeMode(false);

            assertThat(group.isOpen()).isFalse();
        }

        @Test
        void changesClosedToOpen() {
            Group group = Group.create(UUID.randomUUID(), UUID.randomUUID(), "Team", false, true);

            group.changeMode(true);

            assertThat(group.isOpen()).isTrue();
        }
    }

    @Nested
    class ToggleMemberArtifactEdit {

        @Test
        void togglesEnabledToDisabled() {
            Group group = Group.create(UUID.randomUUID(), UUID.randomUUID(), "Team", true, true);

            group.toggleMemberArtifactEdit();

            assertThat(group.isMembersCanEditArtifacts()).isFalse();
        }

        @Test
        void togglesDisabledToEnabled() {
            Group group = Group.create(UUID.randomUUID(), UUID.randomUUID(), "Team", true, false);

            group.toggleMemberArtifactEdit();

            assertThat(group.isMembersCanEditArtifacts()).isTrue();
        }
    }
}

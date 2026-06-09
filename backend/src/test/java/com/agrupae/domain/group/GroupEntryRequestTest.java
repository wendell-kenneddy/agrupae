package com.agrupae.domain.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.exception.DomainException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupEntryRequestTest {

    @Nested
    class Create {

        @Test
        void withValidInputs_createsRequest() {
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            GroupEntryRequest request = GroupEntryRequest.create(groupId, userId);

            assertThat(request.getId()).isNotNull();
            assertThat(request.getGroupId()).isEqualTo(groupId);
            assertThat(request.getUserId()).isEqualTo(userId);
            assertThat(request.getStatus()).isEqualTo(GroupEntryRequestStatus.PENDING);
            assertThat(request.getCreatedAt()).isNotNull();
            assertThat(request.getUpdatedAt()).isEqualTo(request.getCreatedAt());
        }
    }

    @Nested
    class Reconstruct {

        @Test
        void withValidInputs_reconstructsRequest() {
            UUID id = UUID.randomUUID();
            UUID groupId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Instant now = Instant.now();

            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    id, groupId, userId, GroupEntryRequestStatus.ACCEPTED, now, now);

            assertThat(request.getId()).isEqualTo(id);
            assertThat(request.getGroupId()).isEqualTo(groupId);
            assertThat(request.getUserId()).isEqualTo(userId);
            assertThat(request.getStatus()).isEqualTo(GroupEntryRequestStatus.ACCEPTED);
            assertThat(request.getCreatedAt()).isEqualTo(now);
            assertThat(request.getUpdatedAt()).isEqualTo(now);
        }

        @Test
        void withUpdatedAtBeforeCreatedAt_throwsDomainException() {
            Instant createdAt = Instant.now();
            Instant updatedAt = createdAt.minusSeconds(60);

            assertThatThrownBy(() -> GroupEntryRequest.reconstruct(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    GroupEntryRequestStatus.PENDING, createdAt, updatedAt))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Update timestamp cannot be before creation timestamp.");
        }
    }

    @Nested
    class Accept {

        @Test
        void pendingRequest_acceptsRequest() {
            GroupEntryRequest request = GroupEntryRequest.create(UUID.randomUUID(), UUID.randomUUID());
            Instant originalUpdatedAt = request.getUpdatedAt();

            request.accept();

            assertThat(request.getStatus()).isEqualTo(GroupEntryRequestStatus.ACCEPTED);
            assertThat(request.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        }

        @Test
        void nonPendingRequest_throwsDomainException() {
            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    GroupEntryRequestStatus.ACCEPTED, Instant.now(), Instant.now());

            assertThatThrownBy(request::accept)
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Only PENDING requests can be accepted/rejected.");
        }
    }

    @Nested
    class Reject {

        @Test
        void pendingRequest_rejectsRequest() {
            GroupEntryRequest request = GroupEntryRequest.create(UUID.randomUUID(), UUID.randomUUID());
            Instant originalUpdatedAt = request.getUpdatedAt();

            request.reject();

            assertThat(request.getStatus()).isEqualTo(GroupEntryRequestStatus.REJECTED);
            assertThat(request.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        }

        @Test
        void nonPendingRequest_throwsDomainException() {
            GroupEntryRequest request = GroupEntryRequest.reconstruct(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    GroupEntryRequestStatus.REJECTED, Instant.now(), Instant.now());

            assertThatThrownBy(request::reject)
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Only PENDING requests can be accepted/rejected.");
        }
    }
}

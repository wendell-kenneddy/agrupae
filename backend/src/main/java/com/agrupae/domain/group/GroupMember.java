package com.agrupae.domain.group;

import java.util.UUID;

import lombok.NonNull;

public record GroupMember (@NonNull UUID groupId, @NonNull UUID memberId) {

}

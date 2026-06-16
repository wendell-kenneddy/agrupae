package com.agrupae.application.port.in.group;

import java.util.UUID;

public record GroupMemberView(
    UUID id,
    String name,
    String email,
    boolean isLeader
) {}

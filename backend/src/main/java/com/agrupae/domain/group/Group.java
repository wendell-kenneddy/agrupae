package com.agrupae.domain.group;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@Setter
public class Group {
    private UUID id;
    private UUID assignmentId;
    private UUID leaderId;
    private String name;
    private boolean open;
    private boolean membersCanEditArtifacts;
    private Instant createdAt;
    private Instant updatedAt;

}

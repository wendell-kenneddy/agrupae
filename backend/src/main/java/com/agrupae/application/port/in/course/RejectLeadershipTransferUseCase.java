package com.agrupae.application.port.in.course;

import java.util.UUID;

public interface RejectLeadershipTransferUseCase {
    LeadershipTransferRequestView handle(UUID actorId, UUID courseId, UUID requestId);
}

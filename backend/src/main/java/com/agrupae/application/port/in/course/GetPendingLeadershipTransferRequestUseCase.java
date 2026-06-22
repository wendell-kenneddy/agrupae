package com.agrupae.application.port.in.course;

import java.util.UUID;

public interface GetPendingLeadershipTransferRequestUseCase {
    LeadershipTransferRequestView handle(UUID actorId, UUID courseId);
}

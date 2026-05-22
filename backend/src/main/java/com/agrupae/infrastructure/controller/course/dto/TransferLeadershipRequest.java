package com.agrupae.infrastructure.controller.course.dto;

import java.util.UUID;

public record TransferLeadershipRequest(UUID newLeaderId) {
}

package com.agrupae.application.service.course;

import com.agrupae.application.port.in.course.LeadershipTransferRequestView;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.domain.course.LeadershipTransferRequest;
import com.agrupae.domain.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LeadershipTransferRequestViewMapper {
    private final UserRepository userRepository;

    public LeadershipTransferRequestView toView(LeadershipTransferRequest request) {
        User sender = this.userRepository.findById(request.getSenderId());
        User target = this.userRepository.findById(request.getTargetId());

        return new LeadershipTransferRequestView(
                request.getId(),
                request.getCourseId(),
                request.getSenderId(),
                sender != null ? sender.getName() : "",
                request.getTargetId(),
                target != null ? target.getName() : "",
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt());
    }
}

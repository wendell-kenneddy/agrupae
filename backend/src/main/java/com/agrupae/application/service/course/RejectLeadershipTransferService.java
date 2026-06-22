package com.agrupae.application.service.course;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.LeadershipTransferRequestNotFoundException;
import com.agrupae.application.port.in.course.RejectLeadershipTransferUseCase;
import com.agrupae.application.port.in.course.LeadershipTransferRequestView;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.course.LeadershipTransferRequestRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.course.LeadershipTransferRequest;
import com.agrupae.domain.exception.DomainException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RejectLeadershipTransferService implements RejectLeadershipTransferUseCase {
    private final LeadershipTransferRequestRepository leadershipTransferRequestRepository;
    private final CourseRepository courseRepository;
    private final LeadershipTransferRequestViewMapper viewMapper;

    @Override
    @Transactional
    public LeadershipTransferRequestView handle(
            @NonNull UUID actorId,
            @NonNull UUID courseId,
            @NonNull UUID requestId) {
        LeadershipTransferRequest request = this.leadershipTransferRequestRepository.findById(requestId);

        if (request == null || !request.getCourseId().equals(courseId)) {
            throw new LeadershipTransferRequestNotFoundException();
        }

        if (!request.getTargetId().equals(actorId)) {
            throw new DomainException("Only the target user can reject the transfer request.");
        }

        Course course = this.courseRepository.findById(courseId);

        if (course == null) {
            throw new CourseNotFoundException();
        }

        if (course.isArchived()) {
            throw new CourseArchivedException();
        }

        // Apply rejection
        request.reject();
        this.leadershipTransferRequestRepository.save(request);

        return this.viewMapper.toView(request);
    }
}


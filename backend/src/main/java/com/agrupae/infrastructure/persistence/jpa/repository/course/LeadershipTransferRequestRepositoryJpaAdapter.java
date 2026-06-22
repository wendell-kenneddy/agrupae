package com.agrupae.infrastructure.persistence.jpa.repository.course;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.agrupae.application.port.out.course.LeadershipTransferRequestRepository;
import com.agrupae.domain.course.LeadershipTransferRequest;
import com.agrupae.domain.course.LeadershipTransferRequestStatus;
import com.agrupae.infrastructure.persistence.jpa.mapper.LeadershipTransferRequestJpaEntityMapper;
import com.agrupae.infrastructure.persistence.jpa.model.course.LeadershipTransferRequestJpaEntity;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LeadershipTransferRequestRepositoryJpaAdapter implements LeadershipTransferRequestRepository {
    private final LeadershipTransferRequestJpaRepository repository;
    private final LeadershipTransferRequestJpaEntityMapper mapper;

    @Override
    public LeadershipTransferRequest save(LeadershipTransferRequest request) {
        LeadershipTransferRequestJpaEntity entity = this.mapper.toEntity(request);
        LeadershipTransferRequestJpaEntity saved = this.repository.save(entity);
        return this.mapper.toDomain(saved);
    }

    @Override
    public LeadershipTransferRequest findById(UUID id) {
        return this.repository.findById(id)
                .map(this.mapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<LeadershipTransferRequest> findByCourseId(UUID courseId) {
        return this.repository.findByCourseId(courseId).stream()
                .map(this.mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeadershipTransferRequest> findByCourseIdAndStatus(UUID courseId, LeadershipTransferRequestStatus status) {
        return this.repository.findByCourseIdAndStatus(courseId, status).stream()
                .map(this.mapper::toDomain)
                .collect(Collectors.toList());
    }
}

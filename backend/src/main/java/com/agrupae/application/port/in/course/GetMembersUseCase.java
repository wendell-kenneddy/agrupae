package com.agrupae.application.port.in.course;

import com.agrupae.application.port.in.user.UserProfileView;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetMembersUseCase {

    Page<UserProfileView> handle(UUID courseId, UUID actorId, Pageable pageable);

}

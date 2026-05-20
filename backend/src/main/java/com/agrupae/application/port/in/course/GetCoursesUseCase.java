package com.agrupae.application.port.in.course;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetCoursesUseCase {
    Page<CourseView> handle(UUID studentId, Pageable pageable);
}

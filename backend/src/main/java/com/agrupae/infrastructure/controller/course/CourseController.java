package com.agrupae.infrastructure.controller.course;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.in.course.CreateCourseUseCase;
import com.agrupae.application.port.in.course.JoinCourseUseCase;
import com.agrupae.infrastructure.controller.course.dto.CreateCourseRequest;
import com.agrupae.infrastructure.controller.course.dto.JoinCourseRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CreateCourseUseCase createCourseUseCase;
    private final JoinCourseUseCase joinCourseUseCase;

    @PostMapping
    public ResponseEntity<CourseView> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateCourseRequest request) {
        UUID leaderId = UUID.fromString(jwt.getSubject());
        CourseView view = this.createCourseUseCase.handle(leaderId, request.name(), request.description());

        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @PostMapping("/join")
    public ResponseEntity<CourseView> join(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody JoinCourseRequest request) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        CourseView view = this.joinCourseUseCase.handle(studentId, request.inviteCode());

        return ResponseEntity.ok(view);
    }
}

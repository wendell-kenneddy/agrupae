package com.agrupae.infrastructure.controller.course;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.agrupae.application.port.in.course.ArchiveCourseUseCase;
import com.agrupae.application.port.in.course.CourseView;
import com.agrupae.application.port.in.course.CreateCourseUseCase;
import com.agrupae.application.port.in.course.GetACourseUseCase;
import com.agrupae.application.port.in.course.JoinCourseUseCase;
import com.agrupae.application.port.in.course.GetCoursesUseCase;
import com.agrupae.application.port.in.course.TransferLeadershipUseCase;
import com.agrupae.domain.role.Role;
import com.agrupae.infrastructure.controller.course.dto.CreateCourseRequest;
import com.agrupae.infrastructure.controller.course.dto.JoinCourseRequest;
import com.agrupae.infrastructure.controller.course.dto.TransferLeadershipRequest;
import com.agrupae.application.port.in.user.UserProfileView;
import com.agrupae.application.port.in.course.GetMembersUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CreateCourseUseCase createCourseUseCase;
    private final JoinCourseUseCase joinCourseUseCase;
    private final ArchiveCourseUseCase archiveCourseUseCase;
    private final GetCoursesUseCase getCoursesUseCase;
    private final TransferLeadershipUseCase transferLeadershipUseCase;
    private final GetACourseUseCase getACourseUseCase;
    private final GetMembersUseCase getMembersUseCase;

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

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archive(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {
        UUID actorId = UUID.fromString(jwt.getSubject());
        Role actorRole = Role.valueOf(jwt.getClaimAsString("role"));
        this.archiveCourseUseCase.handle(actorId, actorRole, id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CourseView>> getCourses(
            @AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        Page<CourseView> courses = this.getCoursesUseCase.handle(studentId, pageable);

        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseView> getCourse(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        UUID actorId = UUID.fromString(jwt.getSubject());
        CourseView course = this.getACourseUseCase.handle(actorId,id);
        return ResponseEntity.ok(course);
    }

    @PostMapping("/{id}/transfer")
    public ResponseEntity<CourseView> transferLeadership(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @RequestBody TransferLeadershipRequest request) {
        UUID actorId = UUID.fromString(jwt.getSubject());
        Role actorRole = Role.valueOf(jwt.getClaimAsString("role"));
        UUID newLeaderId = request.newLeaderId();
        CourseView view = this.transferLeadershipUseCase.handle(actorId, actorRole, id,newLeaderId);

        return ResponseEntity.ok(view);
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<Page<UserProfileView>> getMembers(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId, Pageable pageable) {
        UUID actorId = UUID.fromString(jwt.getSubject());
        Page<UserProfileView> members = this.getMembersUseCase.handle(courseId, actorId, pageable);
        return ResponseEntity.ok(members);
    }

}

package com.agrupae.infrastructure.controller.group;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrupae.application.port.in.group.CreateGroupUseCase;
import com.agrupae.application.port.in.group.GetAssignmentGroupsUseCase;
import com.agrupae.application.port.in.group.AssignmentGroupsView;
import com.agrupae.application.port.in.group.GroupView;
import com.agrupae.application.port.in.group.JoinOpenGroupUseCase;
import com.agrupae.application.port.in.group.RequestGroupEntryUseCase;
import com.agrupae.application.port.in.group.CancelGroupEntryRequestUseCase;
import com.agrupae.application.port.in.group.AcceptGroupEntryRequestUseCase;
import com.agrupae.application.port.in.group.RejectGroupEntryRequestUseCase;
import com.agrupae.application.port.in.group.GetGroupEntryRequestsUseCase;
import com.agrupae.application.port.in.group.GroupEntryRequestView;
import com.agrupae.application.port.in.group.ChangeGroupModeUseCase;
import com.agrupae.application.port.in.group.DissolveGroupUseCase;
import com.agrupae.application.port.in.group.LeaveGroupUseCase;
import com.agrupae.application.port.in.group.ChangeGroupArtifactDeliverableStatus;
import com.agrupae.application.port.in.group.RemoveGroupMemberUseCase;
import com.agrupae.domain.group.GroupEntryRequestStatus;
import com.agrupae.domain.role.Role;
import com.agrupae.infrastructure.controller.group.dto.ChangeGroupModeRequest;
import com.agrupae.infrastructure.controller.group.dto.CreateGroupRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.agrupae.infrastructure.controller.group.dto.AddGroupArtifactRequest;
import com.agrupae.infrastructure.controller.group.dto.ChangeGroupArtifactDeliverableRequest;
import com.agrupae.infrastructure.controller.group.dto.EditGroupArtifactRequest;
import com.agrupae.infrastructure.controller.group.dto.ChangeGroupArtifactPrivacyRequest;
import com.agrupae.application.port.in.group.AddGroupArtifactUseCase;
import com.agrupae.application.port.in.group.GetGroupArtifactsUseCase;
import com.agrupae.application.port.in.group.GetPublicGroupArtifactsUseCase;
import com.agrupae.application.port.in.group.EditGroupArtifactUseCase;
import com.agrupae.application.port.in.group.DeleteGroupArtifactUseCase;
import com.agrupae.application.port.in.group.GroupArtifactView;
import com.agrupae.application.port.in.group.ChangeGroupArtifactPrivacyUseCase;
import com.agrupae.application.port.in.group.GetGroupMembersUseCase;
import com.agrupae.application.port.in.group.GroupMemberView;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}/assignments/{assignmentId}/groups")
public class GroupController {
        private final CreateGroupUseCase createGroupUseCase;
        private final JoinOpenGroupUseCase joinOpenGroupUseCase;
        private final RequestGroupEntryUseCase requestGroupEntryUseCase;
        private final CancelGroupEntryRequestUseCase cancelGroupEntryRequestUseCase;
        private final AcceptGroupEntryRequestUseCase acceptGroupEntryRequestUseCase;
        private final RejectGroupEntryRequestUseCase rejectGroupEntryRequestUseCase;
        private final GetGroupEntryRequestsUseCase getGroupEntryRequestsUseCase;
        private final ChangeGroupModeUseCase changeGroupModeUseCase;
        private final DissolveGroupUseCase dissolveGroupUseCase;
        private final LeaveGroupUseCase leaveGroupUseCase;
        private final RemoveGroupMemberUseCase removeGroupMemberUseCase;
        private final AddGroupArtifactUseCase addGroupArtifactUseCase;
        private final GetGroupArtifactsUseCase getGroupArtifactsUseCase;
        private final GetPublicGroupArtifactsUseCase getPublicGroupArtifactsUseCase;
        private final EditGroupArtifactUseCase editGroupArtifactUseCase;
        private final DeleteGroupArtifactUseCase deleteGroupArtifactUseCase;
        private final ChangeGroupArtifactPrivacyUseCase changeGroupArtifactPrivacyUseCase;
        private final GetAssignmentGroupsUseCase getAssignmentGroupsUseCase;
        private final GetGroupMembersUseCase getGroupMembersUseCase;
        private final ChangeGroupArtifactDeliverableStatus markGroupArtifactAsDeliverableUseCase;

        @GetMapping
        public ResponseEntity<AssignmentGroupsView> getGroups(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @AuthenticationPrincipal Jwt jwt,
                        Pageable pageable) {
                UUID userId = UUID.fromString(jwt.getSubject());
                AssignmentGroupsView view = this.getAssignmentGroupsUseCase.handle(
                                userId, courseId, assignmentId, pageable);
                return ResponseEntity.ok(view);
        }

        @PostMapping
        public ResponseEntity<GroupView> create(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @Valid @RequestBody CreateGroupRequest request,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                GroupView view = this.createGroupUseCase.handle(
                                userId,
                                courseId,
                                assignmentId,
                                request.name(),
                                request.open());

                return ResponseEntity.status(HttpStatus.CREATED).body(view);
        }

        @PostMapping("/{groupId}/join")
        public ResponseEntity<Void> join(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                this.joinOpenGroupUseCase.handle(courseId, assignmentId, groupId, userId);
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{groupId}/entry-requests")
        public ResponseEntity<GroupEntryRequestView> requestEntry(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                GroupEntryRequestView view = this.requestGroupEntryUseCase.handle(courseId, assignmentId, groupId,
                                userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(view);
        }

        @DeleteMapping("/{groupId}/entry-requests/{requestId}")
        public ResponseEntity<Void> cancelEntryRequest(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @PathVariable UUID requestId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                this.cancelGroupEntryRequestUseCase.handle(courseId, assignmentId, groupId, requestId, userId);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{groupId}/entry-requests")
        public ResponseEntity<List<GroupEntryRequestView>> getEntryRequests(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @RequestParam(required = false) GroupEntryRequestStatus status,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                List<GroupEntryRequestView> views = this.getGroupEntryRequestsUseCase.handle(
                                courseId, assignmentId, groupId, userId, status);
                return ResponseEntity.ok(views);
        }

        @PostMapping("/{groupId}/entry-requests/{requestId}/accept")
        public ResponseEntity<Void> acceptEntryRequest(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @PathVariable UUID requestId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                this.acceptGroupEntryRequestUseCase.handle(courseId, assignmentId, groupId, requestId, userId);
                return ResponseEntity.noContent().build();
        }

        @PutMapping("/{groupId}/mode")
        public ResponseEntity<Void> changeMode(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @Valid @RequestBody ChangeGroupModeRequest request,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                this.changeGroupModeUseCase.handle(courseId, assignmentId, groupId, userId, request.open());
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/{groupId}")
        public ResponseEntity<Void> dissolve(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID actorId = UUID.fromString(jwt.getSubject());
                Role actorRole = Role.valueOf(jwt.getClaimAsString("role"));
                this.dissolveGroupUseCase.handle(actorId, actorRole, courseId, assignmentId, groupId);
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/{groupId}/members/{memberId}")
        public ResponseEntity<Void> removeMember(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @PathVariable UUID memberId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                this.removeGroupMemberUseCase.handle(courseId, assignmentId, groupId, userId, memberId);
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{groupId}/entry-requests/{requestId}/reject")
        public ResponseEntity<GroupEntryRequestView> rejectEntryRequest(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @PathVariable UUID requestId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                GroupEntryRequestView view = this.rejectGroupEntryRequestUseCase.handle(
                                courseId, assignmentId, groupId, requestId, userId);
                return ResponseEntity.ok(view);
        }

        @DeleteMapping("/{groupId}/leave")
        public ResponseEntity<Void> leave(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                this.leaveGroupUseCase.handle(userId, groupId, courseId, assignmentId);
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{groupId}/artifacts")
        public ResponseEntity<GroupArtifactView> addArtifact(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @Valid @RequestBody AddGroupArtifactRequest request,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                GroupArtifactView view = this.addGroupArtifactUseCase.handle(
                                userId,
                                courseId,
                                assignmentId,
                                groupId,
                                request.name(),
                                request.description(),
                                request.privateArtifact(),
                                request.resourceLink());
                return ResponseEntity.status(HttpStatus.CREATED).body(view);
        }

        @GetMapping("/{groupId}/artifacts")
        public ResponseEntity<List<GroupArtifactView>> getArtifacts(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                List<GroupArtifactView> views = this.getGroupArtifactsUseCase.handle(
                                userId,
                                courseId,
                                assignmentId,
                                groupId);
                return ResponseEntity.ok(views);
        }

        @GetMapping("/{groupId}/artifacts/public")
        public ResponseEntity<List<GroupArtifactView>> getPublicArtifacts(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                List<GroupArtifactView> views = this.getPublicGroupArtifactsUseCase.handle(
                                userId,
                                courseId,
                                assignmentId,
                                groupId);
                return ResponseEntity.ok(views);
        }

        @PutMapping("/{groupId}/artifacts/{artifactId}")
        public ResponseEntity<GroupArtifactView> editArtifact(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @PathVariable UUID artifactId,
                        @Valid @RequestBody EditGroupArtifactRequest request,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                GroupArtifactView view = this.editGroupArtifactUseCase.handle(
                                userId,
                                courseId,
                                assignmentId,
                                groupId,
                                artifactId,
                                request.name(),
                                request.description(),
                                request.resourceLink());
                return ResponseEntity.ok(view);
        }

        @PutMapping("/{groupId}/artifacts/{artifactId}/privacy")
        public ResponseEntity<Void> changePrivacy(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @PathVariable UUID artifactId,
                        @Valid @RequestBody ChangeGroupArtifactPrivacyRequest request,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                this.changeGroupArtifactPrivacyUseCase.handle(
                                courseId,
                                assignmentId,
                                groupId,
                                artifactId,
                                userId,
                                request.privateArtifact());
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/{groupId}/artifacts/{artifactId}")
        public ResponseEntity<Void> deleteArtifact(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @PathVariable UUID artifactId,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                this.deleteGroupArtifactUseCase.handle(
                                userId,
                                courseId,
                                assignmentId,
                                groupId,
                                artifactId);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{groupId}/members")
        public ResponseEntity<Page<GroupMemberView>> getMembers(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        Pageable pageable,
                        @AuthenticationPrincipal Jwt jwt) {
                UUID userId = UUID.fromString(jwt.getSubject());
                Page<GroupMemberView> views = this.getGroupMembersUseCase.handle(
                                userId, courseId, assignmentId, groupId, pageable);
                return ResponseEntity.ok(views);
        }

        @PatchMapping("/{groupId}/artifacts/{artifactId}/deliverable")
        public ResponseEntity<Void> changeDeliverableStatus(
                        @PathVariable UUID courseId,
                        @PathVariable UUID assignmentId,
                        @PathVariable UUID groupId,
                        @PathVariable UUID artifactId,
                        @AuthenticationPrincipal Jwt jwt,
                        @Valid @RequestBody ChangeGroupArtifactDeliverableRequest request) {
                UUID userId = UUID.fromString(jwt.getSubject());
                this.markGroupArtifactAsDeliverableUseCase.handle(
                                courseId,
                                assignmentId,
                                groupId,
                                artifactId,
                                userId,
                                request.deliverable());
                return ResponseEntity.noContent().build();
        }
}

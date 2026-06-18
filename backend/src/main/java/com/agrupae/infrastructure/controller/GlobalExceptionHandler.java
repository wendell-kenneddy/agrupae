package com.agrupae.infrastructure.controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.assignment.AssignmentArtifactNotFoundException;
import com.agrupae.application.exception.assignment.NotAuthorizedToArchiveAssignmentException;
import com.agrupae.application.exception.assignment.NotAuthorizedToEditAssignmentException;
import com.agrupae.application.exception.assignment.NotCourseLeaderException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupCreationNotAllowedException;
import com.agrupae.application.exception.group.GroupMemberLimitReachedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.GroupArtifactNotFoundException;
import com.agrupae.application.exception.group.GroupNotOpenException;
import com.agrupae.application.exception.group.MaxGroupsReachedException;
import com.agrupae.application.exception.group.StudentAlreadyInGroupException;
import com.agrupae.application.exception.group.GroupNotClosedException;
import com.agrupae.application.exception.group.PendingRequestAlreadyExistsException;
import com.agrupae.application.exception.group.GroupEntryRequestNotFoundException;
import com.agrupae.application.exception.group.GroupDissolutionNotAllowedException;
import com.agrupae.application.exception.group.GroupMemberNotFoundException;
import com.agrupae.application.exception.group.GroupMemberRemovalNotAllowedException;
import com.agrupae.application.exception.group.SelfRemovalNotAllowedException;
import com.agrupae.application.exception.group.GroupModeChangeNotAllowedException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.exception.auth.InvalidCredentialsException;
import com.agrupae.application.exception.auth.InvalidTokenException;
import com.agrupae.application.exception.auth.TokenExpiredException;
import com.agrupae.application.exception.auth.TokenRevokedException;
import com.agrupae.application.exception.course.AlreadyJoinedCourseException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.InvalidInviteCodeException;
import com.agrupae.application.exception.course.LeaderCannotJoinOwnCourseException;
import com.agrupae.application.exception.course.NotAuthorizedToArchiveCourseException;
import com.agrupae.application.exception.assignment.NotAuthorizedToDeleteAssignmentException;
import com.agrupae.application.exception.course.NotAuthorizedToTransferLeadershipException;
import com.agrupae.application.exception.course.TargetUserNotEnrolled;
import com.agrupae.application.exception.user.EmailAlreadyInUseException;
import com.agrupae.application.exception.user.UserAlreadyExistsException;
import com.agrupae.application.exception.user.UserNotFoundException;
import com.agrupae.domain.exception.DomainException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler({ InvalidTokenException.class, TokenRevokedException.class, TokenExpiredException.class })
    public ResponseEntity<String> handleTokenErrors(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<String> handleEmailAlreadyInUse(EmailAlreadyInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<String> handleMissingCookie(MissingRequestCookieException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Required cookie missing: " + ex.getCookieName());
    }

    @ExceptionHandler(InvalidInviteCodeException.class)
    public ResponseEntity<String> handleInvalidInviteCode(InvalidInviteCodeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<String> handleCourseNotFound(CourseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NotAuthorizedToArchiveCourseException.class)
    public ResponseEntity<String> handleNotAuthorizedToArchiveCourse(NotAuthorizedToArchiveCourseException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler({ AlreadyJoinedCourseException.class, LeaderCannotJoinOwnCourseException.class })
    public ResponseEntity<String> handleCourseJoinConflicts(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(NotCourseLeaderException.class)
    public ResponseEntity<String> handleNotClassLeader(NotCourseLeaderException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomainException(DomainException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ex.getMessage());
    }

    @ExceptionHandler(NotAuthorizedToTransferLeadershipException.class)
    public ResponseEntity<String> handleNotAuthorizedToTransferLeadershipCourse(
            NotAuthorizedToTransferLeadershipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(TargetUserNotEnrolled.class)
    public ResponseEntity<String> handleTargetUserNotEnrolled(TargetUserNotEnrolled ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(AssignmentNotFoundException.class)
    public ResponseEntity<String> handleAssignmentNotFound(AssignmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NotAuthorizedToArchiveAssignmentException.class)
    public ResponseEntity<String> handleNotAuthorizedToArchiveAssignment(NotAuthorizedToArchiveAssignmentException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(NotAuthorizedToEditAssignmentException.class)
    public ResponseEntity<String> handleNotAuthorizedToEditAssignment(NotAuthorizedToEditAssignmentException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(GroupCreationNotAllowedException.class)
    public ResponseEntity<String> handleGroupCreationNotAllowed(GroupCreationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(StudentAlreadyInGroupException.class)
    public ResponseEntity<String> handleStudentAlreadyInGroup(StudentAlreadyInGroupException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler({ MaxGroupsReachedException.class, AssignmentArchivedException.class })
    public ResponseEntity<String> handleGroupDomainViolations(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ex.getMessage());
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<String> handleGroupNotFound(GroupNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(GroupArtifactNotFoundException.class)
    public ResponseEntity<String> handleGroupArtifactNotFound(GroupArtifactNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CourseArchivedException.class)
    public ResponseEntity<String> handleCourseArchived(CourseArchivedException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ex.getMessage());
    }

    @ExceptionHandler({ GroupNotOpenException.class, GroupMemberLimitReachedException.class })
    public ResponseEntity<String> handleGroupJoinConflicts(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(GroupEntryRequestNotFoundException.class)
    public ResponseEntity<String> handleGroupEntryRequestNotFound(GroupEntryRequestNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler({ GroupNotClosedException.class, PendingRequestAlreadyExistsException.class })
    public ResponseEntity<String> handleGroupEntryConflicts(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(NotGroupLeaderException.class)
    public ResponseEntity<String> handleNotGroupLeader(NotGroupLeaderException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(GroupModeChangeNotAllowedException.class)
    public ResponseEntity<String> handleGroupModeChangeNotAllowed(GroupModeChangeNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(GroupDissolutionNotAllowedException.class)
    public ResponseEntity<String> handleGroupDissolutionNotAllowed(GroupDissolutionNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(GroupMemberRemovalNotAllowedException.class)
    public ResponseEntity<String> handleGroupMemberRemovalNotAllowed(GroupMemberRemovalNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(GroupMemberNotFoundException.class)
    public ResponseEntity<String> handleGroupMemberNotFound(GroupMemberNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SelfRemovalNotAllowedException.class)
    public ResponseEntity<String> handleSelfRemovalNotAllowed(SelfRemovalNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler({ NotAuthorizedToDeleteAssignmentException.class })
    public ResponseEntity<String> handleNotAuthorizedToDeleteAssignment(NotAuthorizedToDeleteAssignmentException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(AssignmentArtifactNotFoundException.class)
    public ResponseEntity<String> handleAssignmentArtifactNotFound(AssignmentArtifactNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}

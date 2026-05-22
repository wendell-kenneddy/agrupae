package com.agrupae.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.agrupae.application.exception.auth.InvalidCredentialsException;
import com.agrupae.application.exception.auth.InvalidTokenException;
import com.agrupae.application.exception.auth.TokenExpiredException;
import com.agrupae.application.exception.auth.TokenRevokedException;
import com.agrupae.application.exception.course.AlreadyJoinedCourseException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.InvalidInviteCodeException;
import com.agrupae.application.exception.course.LeaderCannotJoinOwnCourseException;
import com.agrupae.application.exception.course.NotAuthorizedToArchiveCourseException;
import com.agrupae.application.exception.course.NotAuthorizedToTransferLeadershipException;
import com.agrupae.application.exception.course.TargetUserNotEnrolled;
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

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomainException(DomainException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ex.getMessage());
    }

    @ExceptionHandler(NotAuthorizedToTransferLeadershipException.class)
    public ResponseEntity<String> handleNotAuthorizedToTransferLeadershipCourse(NotAuthorizedToTransferLeadershipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(TargetUserNotEnrolled.class)
    public ResponseEntity<String> handleTargetUserNotEnrolled(TargetUserNotEnrolled ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    } 
}

package com.agrupae.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.agrupae.domain.exception.InvalidCredentialsException;
import com.agrupae.domain.exception.InvalidTokenException;
import com.agrupae.domain.exception.TokenExpiredException;
import com.agrupae.domain.exception.TokenRevokedException;
import com.agrupae.domain.exception.UserAlreadyExistsException;
import com.agrupae.domain.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler({InvalidTokenException.class, TokenRevokedException.class, TokenExpiredException.class})
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Required cookie missing: " + ex.getCookieName());
    }
}

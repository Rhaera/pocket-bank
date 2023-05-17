package com.github.rhaera.project.pocketbank.controller.exception.advice;

import com.github.rhaera.project.pocketbank.controller.exception.response.WebExceptionResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class PocketBankApplicationControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<WebExceptionResponse> ioExceptionHandler(IOException e, WebRequest request) {
        return new ResponseEntity<>(new WebExceptionResponse(Date.from(Instant.now()), request.getDescription(false), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<WebExceptionResponse> invalidArgumentExceptionHandler(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(new WebExceptionResponse(Date.from(Instant.now()),
                                                                             e.getMessage(),
                                                                             e.getFieldErrors()
                                                                             .stream()
                                                                             .map(FieldError::getDefaultMessage)
                                                                             .filter(Objects::nonNull)
                                                                             .collect(Collectors.joining())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<WebExceptionResponse> illegalArgumentExceptionHandler(IllegalArgumentException e, WebRequest request) {
        return ResponseEntity.badRequest().body(new WebExceptionResponse(Date.from(Instant.now()), request.getDescription(false), e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<WebExceptionResponse> illegalStateExceptionHandler(IllegalStateException e, WebRequest request) {
        return ResponseEntity.badRequest().body(new WebExceptionResponse(Date.from(Instant.now()), request.getDescription(false), e.getMessage()));
    }

    @ExceptionHandler(StringIndexOutOfBoundsException.class)
    public ResponseEntity<WebExceptionResponse> badRegistryExceptionHandler(StringIndexOutOfBoundsException e, WebRequest request) {
        return ResponseEntity.badRequest().body(new WebExceptionResponse(Date.from(Instant.now()), request.getDescription(false), e.getMessage()));
    }
}

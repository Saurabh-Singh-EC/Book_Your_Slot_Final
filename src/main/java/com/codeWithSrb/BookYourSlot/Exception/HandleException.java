package com.codeWithSrb.BookYourSlot.Exception;

import com.codeWithSrb.BookYourSlot.Model.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error("handleExceptionInternal: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                        .withTimeStamp(now().toString())
                        .withHttpStatus(HttpStatus.resolve(statusCode.value()))
                        .withStatusCode(statusCode.value())
                        .withReason(exception.getMessage())
                        .withDevelopersMessage(exception.getMessage())
                        .build(), statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("handleMethodArgumentNotValid: " + exception.getMessage());
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

        return new ResponseEntity<>(HttpResponse.builder()
                .withTimeStamp(now().toString())
                .withHttpStatus(HttpStatus.resolve(status.value()))
                .withStatusCode(status.value())
                .withReason(fieldMessage)
                .withDevelopersMessage(exception.getMessage())
                .build(), status);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        log.error("sQLIntegrityConstraintViolationException: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .withTimeStamp(now().toString())
                .withHttpStatus(BAD_REQUEST)
                .withStatusCode(BAD_REQUEST.value())
                .withReason(exception.getMessage().contains("Duplicate Entry") ? "Information already exists": exception.getMessage())
                .withDevelopersMessage(exception.getMessage())
                .build(), BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> usernameNotFoundException(UsernameNotFoundException exception) {
        log.error("usernameNotFoundException: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .withTimeStamp(now().toString())
                .withHttpStatus(NOT_FOUND)
                .withStatusCode(NOT_FOUND.value())
                .withReason(exception.getMessage())
                .withDevelopersMessage(exception.getMessage())
                .build(), NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> badCredentialsException(BadCredentialsException exception) {
        log.error("badCredentialsException: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .withTimeStamp(now().toString())
                .withHttpStatus(BAD_REQUEST)
                .withStatusCode(BAD_REQUEST.value())
                .withReason("Bad Credential: Incorrect email or password.")
                .withDevelopersMessage(exception.getMessage())
                .build(), BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accessDeniedException(AccessDeniedException exception) {
        log.error("accessDeniedException: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .withTimeStamp(now().toString())
                .withHttpStatus(FORBIDDEN)
                .withStatusCode(FORBIDDEN.value())
                .withReason("Access denied. You dont have access.")
                .withDevelopersMessage(exception.getMessage())
                .build(), FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception exception) {
        log.error("exception: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .withTimeStamp(now().toString())
                .withHttpStatus(INTERNAL_SERVER_ERROR)
                .withStatusCode(INTERNAL_SERVER_ERROR.value())
                .withReason("Something went wrong. Please try again.")
                .withDevelopersMessage(exception.getMessage())
                .build(), INTERNAL_SERVER_ERROR);
    }
}

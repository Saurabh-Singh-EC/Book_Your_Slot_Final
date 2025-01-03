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
        exception.printStackTrace();
        return new ResponseEntity<>(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(HttpStatus.resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .reason(exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .build(), statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("handleMethodArgumentNotValid: " + exception.getMessage());
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .httpStatus(HttpStatus.resolve(status.value()))
                .statusCode(status.value())
                .reason(fieldMessage)
                .developerMessage(exception.getMessage())
                .build(), status);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        log.error("sQLIntegrityConstraintViolationException: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .httpStatus(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .reason(exception.getMessage().contains("Duplicate Entry") ? "Information already exists": exception.getMessage())
                .developerMessage(exception.getMessage())
                .build(), BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> usernameNotFoundException(UsernameNotFoundException exception) {
        log.error("usernameNotFoundException: " + exception.getMessage());
        exception.printStackTrace();
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .httpStatus(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .reason(exception.getMessage())
                .developerMessage(exception.getMessage())
                .build(), NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> badCredentialsException(BadCredentialsException exception) {
        log.error("badCredentialsException: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .httpStatus(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .reason("Bad Credential: Incorrect email or password.")
                .developerMessage(exception.getMessage())
                .build(), BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accessDeniedException(AccessDeniedException exception) {
        log.error("accessDeniedException: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .httpStatus(FORBIDDEN)
                .statusCode(FORBIDDEN.value())
                .reason("Access denied. You dont have access.")
                .developerMessage(exception.getMessage())
                .build(), FORBIDDEN);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> apiException(ApiException exception) {
        log.error("apiException: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .httpStatus(INTERNAL_SERVER_ERROR)
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .reason("Something went wrong. Please try again.")
                .developerMessage(exception.getMessage())
                .build(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception exception) {
        log.error("exception: " + exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .httpStatus(INTERNAL_SERVER_ERROR)
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .reason("Something went wrong. Please try again.")
                .developerMessage(exception.getMessage())
                .build(), INTERNAL_SERVER_ERROR);
    }
}

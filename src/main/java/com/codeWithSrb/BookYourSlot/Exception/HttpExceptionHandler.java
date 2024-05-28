package com.codeWithSrb.BookYourSlot.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Exception handler class to handle the controller exception globally.
 */
@ControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler({InvalidRequestException.class})
    private ResponseEntity<String> handleException(InvalidRequestException e) {
        System.out.println("Unable to process the request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({UsernamePasswordInvalidException.class})
    private ResponseEntity<String> handleException(UsernamePasswordInvalidException e) {
        System.out.println("username or password is not valid");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

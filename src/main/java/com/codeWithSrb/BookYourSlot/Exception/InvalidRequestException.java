package com.codeWithSrb.BookYourSlot.Exception;

public class InvalidRequestException extends RuntimeException{

    public InvalidRequestException() {
    }

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable t) {
        super(message, t);
    }

    public InvalidRequestException(Throwable cause) {
        super(cause);
    }
}

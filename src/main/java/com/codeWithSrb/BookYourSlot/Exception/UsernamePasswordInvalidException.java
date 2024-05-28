package com.codeWithSrb.BookYourSlot.Exception;

public class UsernamePasswordInvalidException extends RuntimeException {

    public UsernamePasswordInvalidException() {
    }

    public UsernamePasswordInvalidException(String message) {
        super(message);
    }

    public UsernamePasswordInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernamePasswordInvalidException(Throwable cause) {
        super(cause);
    }
}

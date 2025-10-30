package com.itdev.exception;

public class DeleteFirstAccountException extends RuntimeException {

    public DeleteFirstAccountException() {
        super("Unable to delete first account.");
    }

    public DeleteFirstAccountException(String message) {
        super(message);
    }
}

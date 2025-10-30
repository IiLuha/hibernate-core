package com.itdev.exception;

public class LoginAlreadyExistException extends RuntimeException {

    public LoginAlreadyExistException(String message) {
        super(message);
    }
}

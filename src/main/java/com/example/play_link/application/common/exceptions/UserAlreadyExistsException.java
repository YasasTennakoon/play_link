package com.example.play_link.application.common.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
        super(message);
    }
}

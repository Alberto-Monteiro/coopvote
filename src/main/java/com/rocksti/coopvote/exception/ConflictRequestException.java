package com.rocksti.coopvote.exception;

public class ConflictRequestException extends RuntimeException {

    public ConflictRequestException(String message) {
        super(message);
    }
}

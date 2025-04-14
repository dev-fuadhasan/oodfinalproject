package com.chatapp.exception;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public AuthenticationException(String message) {
        super(message);
    }
}

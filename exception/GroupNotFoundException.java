package com.chatapp.exception;

/**
 * Exception thrown when a group is not found.
 */
public class GroupNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public GroupNotFoundException(String message) {
        super(message);
    }
}

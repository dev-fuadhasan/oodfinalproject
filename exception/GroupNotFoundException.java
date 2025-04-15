package com.chatapp.exception;

public class GroupNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public GroupNotFoundException(String message) {
        super(message);
    }
}

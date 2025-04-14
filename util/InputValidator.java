package com.chatapp.util;

/**
 * Validates user input for the chat application.
 */
public class InputValidator {
    
    /**
     * Check if a username is valid.
     * Username must be 3-20 characters and contain only letters, numbers, and underscores.
     * 
     * @param username the username to validate
     * @return true if username is valid, false otherwise
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        // Check length
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }
        
        // Check characters
        return username.matches("^[a-zA-Z0-9_]+$");
    }
    
    /**
     * Check if a password is valid.
     * Password must be at least 6 characters.
     * 
     * @param password the password to validate
     * @return true if password is valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Check length
        return password.length() >= 6;
    }
    
    /**
     * Check if a group name is valid.
     * Group name must be 3-30 characters.
     * 
     * @param groupName the group name to validate
     * @return true if group name is valid, false otherwise
     */
    public boolean isValidGroupName(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return false;
        }
        
        // Check length
        return groupName.length() >= 3 && groupName.length() <= 30;
    }
    
    /**
     * Check if a message is valid.
     * Message must not be empty.
     * 
     * @param message the message to validate
     * @return true if message is valid, false otherwise
     */
    public boolean isValidMessage(String message) {
        return message != null && !message.trim().isEmpty();
    }
}

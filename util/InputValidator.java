package com.chatapp.util;

public class InputValidator {

    public boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }

        if (username.length() < 3 || username.length() > 20) {
            return false;
        }

        return username.matches("^[a-zA-Z0-9_]+$");
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        return password.length() >= 6;
    }

    public boolean isValidGroupName(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return false;
        }

        return groupName.length() >= 3 && groupName.length() <= 30;
    }

    public boolean isValidMessage(String message) {
        return message != null && !message.trim().isEmpty();
    }
}

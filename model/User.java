package com.chatapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User implements Serializable {
    private static final long serialVersionUID = 2L; // Updated version number

    private String username;
    private String password;
    private List<String> groupIds; // Groups the user belongs to
    private Set<String> contacts; // Users this user has chatted with
    private Map<String, LocalDateTime> lastReadTimes; // Last read time for each contact
    private Map<String, LocalDateTime> lastReadGroupTimes; // Last read time for each group

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.groupIds = new ArrayList<>();
        this.contacts = new HashSet<>();
        this.lastReadTimes = new HashMap<>();
        this.lastReadGroupTimes = new HashMap<>();
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void addGroup(String groupId) {
        if (!groupIds.contains(groupId)) {
            groupIds.add(groupId);
        }
    }

    public void removeGroup(String groupId) {
        groupIds.remove(groupId);
    }

    public Set<String> getContacts() {
        return contacts;
    }

    public void addContact(String username) {
        contacts.add(username);
    }

    public LocalDateTime getLastReadTime(String contactUsername) {
        return lastReadTimes.get(contactUsername);
    }

    public void updateLastReadTime(String contactUsername) {
        lastReadTimes.put(contactUsername, LocalDateTime.now());
    }

    public LocalDateTime getLastReadGroupTime(String groupId) {
        return lastReadGroupTimes.get(groupId);
    }

    public void updateLastReadGroupTime(String groupId) {
        lastReadGroupTimes.put(groupId, LocalDateTime.now());
    }

    public boolean isPasswordCorrect(String attemptedPassword) {
        return this.password.equals(attemptedPassword);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "User: " + username;
    }
}
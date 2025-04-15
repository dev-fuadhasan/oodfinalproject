package com.chatapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Group implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String groupId;
    private String groupName;
    private String adminUsername;
    private Set<String> members; // Set of usernames
    private List<JoinRequest> pendingRequests;
    private LocalDateTime creationTime;
    
    public Group(String groupId, String groupName, String adminUsername) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.adminUsername = adminUsername;
        this.members = new HashSet<>();
        this.members.add(adminUsername); // Admin is automatically a member
        this.pendingRequests = new ArrayList<>();
        this.creationTime = LocalDateTime.now();
    }

    public String getGroupId() {
        return groupId;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public String getAdminUsername() {
        return adminUsername;
    }
    
    public Set<String> getMembers() {
        return members;
    }
    
    public boolean isMember(String username) {
        return members.contains(username);
    }
    
    public boolean isAdmin(String username) {
        return adminUsername.equals(username);
    }
    
    public void addMember(String username) {
        members.add(username);
    }
    
    public void removeMember(String username) {
        if (!isAdmin(username)) {
            members.remove(username);
        }
    }
    
    public List<JoinRequest> getPendingRequests() {
        return pendingRequests;
    }
    
    public void addJoinRequest(JoinRequest request) {
        if (!isMember(request.getRequestorUsername()) && 
                !hasPendingRequest(request.getRequestorUsername())) {
            pendingRequests.add(request);
        }
    }
    
    public boolean hasPendingRequest(String username) {
        for (JoinRequest request : pendingRequests) {
            if (request.getRequestorUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    public void removeJoinRequest(String username) {
        pendingRequests.removeIf(request -> request.getRequestorUsername().equals(username));
    }
    
    public LocalDateTime getCreationTime() {
        return creationTime;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Group group = (Group) obj;
        return groupId.equals(group.groupId);
    }
    
    @Override
    public int hashCode() {
        return groupId.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Group: %s (ID: %s, Admin: %s, Members: %d)", 
                groupName, groupId, adminUsername, members.size());
    }
}

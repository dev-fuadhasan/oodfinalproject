package com.chatapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a request to join a group in the chat application.
 * Implements Serializable to support file-based persistence.
 */
public class JoinRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String requestId;
    private String groupId;
    private String requestorUsername;
    private LocalDateTime requestTime;
    
    public JoinRequest(String requestId, String groupId, String requestorUsername) {
        this.requestId = requestId;
        this.groupId = groupId;
        this.requestorUsername = requestorUsername;
        this.requestTime = LocalDateTime.now();
    }
    
    // Getters
    public String getRequestId() {
        return requestId;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public String getRequestorUsername() {
        return requestorUsername;
    }
    
    public LocalDateTime getRequestTime() {
        return requestTime;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        JoinRequest request = (JoinRequest) obj;
        return requestId.equals(request.requestId);
    }
    
    @Override
    public int hashCode() {
        return requestId.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Join Request from %s for group %s at %s", 
                requestorUsername, groupId, requestTime);
    }
}

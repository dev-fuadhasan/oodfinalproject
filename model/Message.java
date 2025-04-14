package com.chatapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a message in the chat application.
 * Implements Serializable to support file-based persistence.
 */
public class Message implements Serializable, Comparable<Message> {
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    private String senderId;
    private String receiverId; // Can be a user or a group
    private String content;
    private LocalDateTime timestamp;
    private boolean isGroupMessage;
    
    public Message(String messageId, String senderId, String receiverId, String content, boolean isGroupMessage) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isGroupMessage = isGroupMessage;
    }
    
    // Getters
    public String getMessageId() {
        return messageId;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public String getReceiverId() {
        return receiverId;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public boolean isGroupMessage() {
        return isGroupMessage;
    }
    
    /**
     * Format timestamp for display
     * @return formatted timestamp string
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", 
                getFormattedTimestamp(), 
                senderId, 
                content);
    }
    
    @Override
    public int compareTo(Message other) {
        return this.timestamp.compareTo(other.timestamp);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message message = (Message) obj;
        return messageId.equals(message.messageId);
    }
    
    @Override
    public int hashCode() {
        return messageId.hashCode();
    }
}

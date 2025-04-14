package com.chatapp.service;

import com.chatapp.model.Message;
import com.chatapp.util.DataPersistence;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for message-related operations.
 */
public class MessageService {
    private final DataPersistence dataPersistence;
    
    public MessageService(DataPersistence dataPersistence) {
        this.dataPersistence = dataPersistence;
    }
    
    /**
     * Send a direct message from one user to another.
     * 
     * @param senderId username of sender
     * @param receiverId username of receiver
     * @param content message content
     * @return the created Message object
     */
    public Message sendDirectMessage(String senderId, String receiverId, String content) {
        String messageId = UUID.randomUUID().toString();
        Message message = new Message(messageId, senderId, receiverId, content, false);
        
        // Save message
        List<Message> messages = dataPersistence.loadMessages();
        messages.add(message);
        dataPersistence.saveMessages(messages);
        
        return message;
    }
    
    /**
     * Send a message to a group.
     * 
     * @param senderId username of sender
     * @param groupId id of the group
     * @param content message content
     * @return the created Message object
     */
    public Message sendGroupMessage(String senderId, String groupId, String content) {
        String messageId = UUID.randomUUID().toString();
        Message message = new Message(messageId, senderId, groupId, content, true);
        
        // Save message
        List<Message> messages = dataPersistence.loadMessages();
        messages.add(message);
        dataPersistence.saveMessages(messages);
        
        return message;
    }
    
    /**
     * Get all direct messages between two users.
     * 
     * @param user1 first user's username
     * @param user2 second user's username
     * @return list of messages between the users, ordered by timestamp
     */
    public List<Message> getDirectMessagesBetweenUsers(String user1, String user2) {
        List<Message> allMessages = dataPersistence.loadMessages();
        
        // Filter messages that are between these two users
        List<Message> conversations = allMessages.stream()
                .filter(msg -> !msg.isGroupMessage())
                .filter(msg -> 
                    (msg.getSenderId().equals(user1) && msg.getReceiverId().equals(user2)) ||
                    (msg.getSenderId().equals(user2) && msg.getReceiverId().equals(user1))
                )
                .collect(Collectors.toList());
        
        // Sort by timestamp
        Collections.sort(conversations);
        
        return conversations;
    }
    
    /**
     * Get all messages in a group.
     * 
     * @param groupId the group ID
     * @return list of messages in the group, ordered by timestamp
     */
    public List<Message> getGroupMessages(String groupId) {
        List<Message> allMessages = dataPersistence.loadMessages();
        
        // Filter messages that belong to this group
        List<Message> groupMessages = allMessages.stream()
                .filter(Message::isGroupMessage)
                .filter(msg -> msg.getReceiverId().equals(groupId))
                .collect(Collectors.toList());
        
        // Sort by timestamp
        Collections.sort(groupMessages);
        
        return groupMessages;
    }
    
    /**
     * Delete all messages related to a specific user (for account deletion).
     * 
     * @param username the username of the user being deleted
     */
    public void deleteUserMessages(String username) {
        List<Message> allMessages = dataPersistence.loadMessages();
        
        // Remove messages where user is sender or receiver (for direct messages only)
        List<Message> remainingMessages = allMessages.stream()
                .filter(msg -> !(
                    msg.getSenderId().equals(username) || 
                    (!msg.isGroupMessage() && msg.getReceiverId().equals(username))
                ))
                .collect(Collectors.toList());
        
        dataPersistence.saveMessages(remainingMessages);
    }
    
    /**
     * Delete all messages related to a specific group (for group deletion).
     * 
     * @param groupId the ID of the group being deleted
     */
    public void deleteGroupMessages(String groupId) {
        List<Message> allMessages = dataPersistence.loadMessages();
        
        // Remove messages where group is receiver
        List<Message> remainingMessages = allMessages.stream()
                .filter(msg -> !(msg.isGroupMessage() && msg.getReceiverId().equals(groupId)))
                .collect(Collectors.toList());
        
        dataPersistence.saveMessages(remainingMessages);
    }
}

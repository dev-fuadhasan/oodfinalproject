package com.chatapp.service;

import com.chatapp.model.Message;
import com.chatapp.util.DataPersistence;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MessageService {
    private final DataPersistence dataPersistence;
    
    public MessageService(DataPersistence dataPersistence) {
        this.dataPersistence = dataPersistence;
    }
    

    public Message sendDirectMessage(String senderId, String receiverId, String content) {
        String messageId = UUID.randomUUID().toString();
        Message message = new Message(messageId, senderId, receiverId, content, false);
        
        // Save message
        List<Message> messages = dataPersistence.loadMessages();
        messages.add(message);
        dataPersistence.saveMessages(messages);
        
        return message;
    }

    public Message sendGroupMessage(String senderId, String groupId, String content) {
        String messageId = UUID.randomUUID().toString();
        Message message = new Message(messageId, senderId, groupId, content, true);
        
        // Save message
        List<Message> messages = dataPersistence.loadMessages();
        messages.add(message);
        dataPersistence.saveMessages(messages);
        
        return message;
    }

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

        Collections.sort(conversations);
        
        return conversations;
    }

    public List<Message> getGroupMessages(String groupId) {
        List<Message> allMessages = dataPersistence.loadMessages();

        List<Message> groupMessages = allMessages.stream()
                .filter(Message::isGroupMessage)
                .filter(msg -> msg.getReceiverId().equals(groupId))
                .collect(Collectors.toList());

        Collections.sort(groupMessages);
        
        return groupMessages;
    }

    public void deleteUserMessages(String username) {
        List<Message> allMessages = dataPersistence.loadMessages();

        List<Message> remainingMessages = allMessages.stream()
                .filter(msg -> !(
                    msg.getSenderId().equals(username) || 
                    (!msg.isGroupMessage() && msg.getReceiverId().equals(username))
                ))
                .collect(Collectors.toList());
        
        dataPersistence.saveMessages(remainingMessages);
    }

    public void deleteGroupMessages(String groupId) {
        List<Message> allMessages = dataPersistence.loadMessages();

        List<Message> remainingMessages = allMessages.stream()
                .filter(msg -> !(msg.isGroupMessage() && msg.getReceiverId().equals(groupId)))
                .collect(Collectors.toList());
        
        dataPersistence.saveMessages(remainingMessages);
    }
}

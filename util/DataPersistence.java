package com.chatapp.util;

import com.chatapp.model.Group;
import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataPersistence {
    private static final String USERS_FILE = "users.dat";
    private static final String MESSAGES_FILE = "messages.dat";
    private static final String GROUPS_FILE = "groups.dat";
    

    @SuppressWarnings("unchecked")
    public Map<String, User> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            return (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public void saveUsers(Map<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Message> loadMessages() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MESSAGES_FILE))) {
            return (List<Message>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading messages: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveMessages(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MESSAGES_FILE))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            System.err.println("Error saving messages: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Group> loadGroups() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GROUPS_FILE))) {
            return (Map<String, Group>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading groups: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public void saveGroups(Map<String, Group> groups) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GROUPS_FILE))) {
            oos.writeObject(groups);
        } catch (IOException e) {
            System.err.println("Error saving groups: " + e.getMessage());
        }
    }
}

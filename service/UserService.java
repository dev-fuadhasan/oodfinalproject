package com.chatapp.service;

import com.chatapp.exception.AuthenticationException;
import com.chatapp.exception.UserNotFoundException;
import com.chatapp.model.User;
import com.chatapp.util.DataPersistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserService {
    private final DataPersistence dataPersistence;
    private User currentUser;
    
    public UserService(DataPersistence dataPersistence) {
        this.dataPersistence = dataPersistence;
        this.currentUser = null;
    }

    public boolean registerUser(String username, String password) {

        Map<String, User> users = dataPersistence.loadUsers();
        if (users.containsKey(username)) {
            return false;
        }

        User newUser = new User(username, password);
        users.put(username, newUser);

        dataPersistence.saveUsers(users);
        return true;
    }

    public boolean login(String username, String password) 
            throws UserNotFoundException, AuthenticationException {
        Map<String, User> users = dataPersistence.loadUsers();

        if (!users.containsKey(username)) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }
        
        User user = users.get(username);

        if (!user.isPasswordCorrect(password)) {
            throw new AuthenticationException("Incorrect password.");
        }

        currentUser = user;
        return true;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<String> searchUsers(String searchTerm) {
        Map<String, User> users = dataPersistence.loadUsers();
        
        return users.keySet().stream()
                .filter(username -> username.toLowerCase().contains(searchTerm.toLowerCase()))
                .filter(username -> !username.equals(getCurrentUsername())) // Exclude current user
                .collect(Collectors.toList());
    }

    public boolean deleteAccount() {
        if (!isLoggedIn()) {
            return false;
        }
        
        String username = getCurrentUsername();

        Map<String, User> users = dataPersistence.loadUsers();
        users.remove(username);
        dataPersistence.saveUsers(users);

        logout();
        
        return true;
    }

    public void addContact(String contactUsername) {
        if (isLoggedIn() && !getCurrentUsername().equals(contactUsername)) {
            currentUser.addContact(contactUsername);

            Map<String, User> users = dataPersistence.loadUsers();
            users.put(getCurrentUsername(), currentUser);
            dataPersistence.saveUsers(users);
        }
    }

    public List<String> getContacts() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(currentUser.getContacts());
    }

    public User getUserByUsername(String username) {
        Map<String, User> users = dataPersistence.loadUsers();
        return users.get(username);
    }

    public void saveUser(User user) {
        Map<String, User> users = dataPersistence.loadUsers();
        users.put(user.getUsername(), user);
        dataPersistence.saveUsers(users);

        if (currentUser != null && currentUser.getUsername().equals(user.getUsername())) {
            currentUser = user;
        }
    }
    

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}

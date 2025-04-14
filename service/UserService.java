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

/**
 * Service class for user-related operations.
 */
public class UserService {
    private final DataPersistence dataPersistence;
    private User currentUser;
    
    public UserService(DataPersistence dataPersistence) {
        this.dataPersistence = dataPersistence;
        this.currentUser = null;
    }
    
    /**
     * Register a new user with given username and password.
     * 
     * @param username the username for the new user
     * @param password the password for the new user
     * @return true if registration is successful, false otherwise
     */
    public boolean registerUser(String username, String password) {
        // Check if username already exists
        Map<String, User> users = dataPersistence.loadUsers();
        if (users.containsKey(username)) {
            return false;
        }
        
        // Create new user
        User newUser = new User(username, password);
        users.put(username, newUser);
        
        // Save updated users map
        dataPersistence.saveUsers(users);
        return true;
    }
    
    /**
     * Login a user with given username and password.
     * 
     * @param username the username to login
     * @param password the password to verify
     * @return true if login is successful, false otherwise
     * @throws UserNotFoundException if username doesn't exist
     * @throws AuthenticationException if password is incorrect
     */
    public boolean login(String username, String password) 
            throws UserNotFoundException, AuthenticationException {
        Map<String, User> users = dataPersistence.loadUsers();
        
        // Check if user exists
        if (!users.containsKey(username)) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }
        
        User user = users.get(username);
        
        // Check if password is correct
        if (!user.isPasswordCorrect(password)) {
            throw new AuthenticationException("Incorrect password.");
        }
        
        // Set current user
        currentUser = user;
        return true;
    }
    
    /**
     * Logout the current user.
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Check if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get the username of the currently logged in user.
     * 
     * @return username of current user, or null if not logged in
     */
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
    
    /**
     * Get the current logged in User.
     * 
     * @return current User object, or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Search for users by a partial username.
     * 
     * @param searchTerm the partial username to search for
     * @return list of matching usernames
     */
    public List<String> searchUsers(String searchTerm) {
        Map<String, User> users = dataPersistence.loadUsers();
        
        return users.keySet().stream()
                .filter(username -> username.toLowerCase().contains(searchTerm.toLowerCase()))
                .filter(username -> !username.equals(getCurrentUsername())) // Exclude current user
                .collect(Collectors.toList());
    }
    
    /**
     * Delete the current user's account.
     * 
     * @return true if deletion is successful
     */
    public boolean deleteAccount() {
        if (!isLoggedIn()) {
            return false;
        }
        
        String username = getCurrentUsername();
        
        // Remove user from users map
        Map<String, User> users = dataPersistence.loadUsers();
        users.remove(username);
        dataPersistence.saveUsers(users);
        
        // Logout the user
        logout();
        
        return true;
    }
    
    /**
     * Update the contact list for the current user.
     * 
     * @param contactUsername the username to add to contacts
     */
    public void addContact(String contactUsername) {
        if (isLoggedIn() && !getCurrentUsername().equals(contactUsername)) {
            currentUser.addContact(contactUsername);
            
            // Save updated users map
            Map<String, User> users = dataPersistence.loadUsers();
            users.put(getCurrentUsername(), currentUser);
            dataPersistence.saveUsers(users);
        }
    }
    
    /**
     * Get all contacts for the current user.
     * 
     * @return list of contact usernames
     */
    public List<String> getContacts() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(currentUser.getContacts());
    }
    
    /**
     * Get user by username.
     * 
     * @param username the username to lookup
     * @return the User object, or null if not found
     */
    public User getUserByUsername(String username) {
        Map<String, User> users = dataPersistence.loadUsers();
        return users.get(username);
    }
    
    /**
     * Save the updated user object to storage.
     * 
     * @param user the user to save
     */
    public void saveUser(User user) {
        Map<String, User> users = dataPersistence.loadUsers();
        users.put(user.getUsername(), user);
        dataPersistence.saveUsers(users);
        
        // Update current user if we're saving the current user
        if (currentUser != null && currentUser.getUsername().equals(user.getUsername())) {
            currentUser = user;
        }
    }
    
    /**
     * Generate a unique identifier.
     * 
     * @return a new UUID string
     */
    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}

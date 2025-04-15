package com.chatapp.ui;

import com.chatapp.exception.AuthenticationException;
import com.chatapp.exception.GroupNotFoundException;
import com.chatapp.exception.UserNotFoundException;
import com.chatapp.model.Group;
import com.chatapp.model.JoinRequest;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.service.GroupService;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;
import com.chatapp.util.DataPersistence;
import com.chatapp.util.InputValidator;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI implements UserInterface {
    private final Scanner scanner;
    private final UserService userService;
    private final MessageService messageService;
    private final GroupService groupService;
    private final InputValidator validator;
    private final DataPersistence dataPersistence;
    private boolean running;
    
    public ConsoleUI(UserService userService, MessageService messageService, GroupService groupService) {
        this.scanner = new Scanner(System.in);
        this.userService = userService;
        this.messageService = messageService;
        this.groupService = groupService;
        this.validator = new InputValidator();
        this.dataPersistence = new DataPersistence();
        this.running = false;
    }
    
    @Override
    public void start() {
        running = true;
        showWelcomeScreen();
        
        while (running) {
            if (userService.isLoggedIn()) {
                showMainMenu();
            } else {
                showAuthMenu();
            }
        }

        scanner.close();
    }
    
    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }
    
    @Override
    public String getInput(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine().trim();
    }
    
    @Override
    public void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    
    private void showWelcomeScreen() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("         WELCOME TO CHAT APP       ");
        displayMessage("===================================");
        displayMessage("A terminal-based chat application");
        displayMessage("-----------------------------------");
        getInput("Press Enter to continue...");
    }
    
    @Override
    public void showAuthMenu() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("            AUTHENTICATION         ");
        displayMessage("===================================");
        displayMessage("1. Login");
        displayMessage("2. Register");
        displayMessage("3. Exit");
        displayMessage("-----------------------------------");
        
        String choice = getInput("Choose an option (1-3):");
        
        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                handleRegistration();
                break;
            case "3":
                handleExit();
                break;
            default:
                displayMessage("Invalid option. Please try again.");
                getInput("Press Enter to continue...");
        }
    }
    
    @Override
    public void showMainMenu() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("              MAIN MENU            ");
        displayMessage("===================================");
        displayMessage("Logged in as: " + userService.getCurrentUsername());

        showUnreadMessageNotification();
        
        displayMessage("-----------------------------------");
        displayMessage("1. Message a User");
        displayMessage("2. Group Management");
        displayMessage("3. View My Contacts");
        displayMessage("4. Account Settings");
        displayMessage("5. Logout");
        displayMessage("-----------------------------------");
        
        String choice = getInput("Choose an option (1-5):");
        
        switch (choice) {
            case "1":
                showMessagingMenu();
                break;
            case "2":
                showGroupMenu();
                break;
            case "3":
                handleViewContacts();
                break;
            case "4":
                showAccountSettingsMenu();
                break;
            case "5":
                handleLogout();
                break;
            default:
                displayMessage("Invalid option. Please try again.");
                getInput("Press Enter to continue...");
        }
    }
    
    @Override
    public void showMessagingMenu() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("          MESSAGING MENU           ");
        displayMessage("===================================");
        displayMessage("1. Message a Contact");
        displayMessage("2. Search for a User");
        displayMessage("3. Return to Main Menu");
        displayMessage("-----------------------------------");
        
        String choice = getInput("Choose an option (1-3):");
        
        switch (choice) {
            case "1":
                handleMessageContact();
                break;
            case "2":
                handleSearchUser();
                break;
            case "3":
                // Return to main menu
                break;
            default:
                displayMessage("Invalid option. Please try again.");
                getInput("Press Enter to continue...");
                showMessagingMenu();
        }
    }
    
    @Override
    public void showGroupMenu() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("            GROUP MENU             ");
        displayMessage("===================================");
        displayMessage("1. View My Groups");
        displayMessage("2. Create a New Group");
        displayMessage("3. Search for Groups");
        displayMessage("4. Return to Main Menu");
        displayMessage("-----------------------------------");
        
        String choice = getInput("Choose an option (1-4):");
        
        switch (choice) {
            case "1":
                handleViewMyGroups();
                break;
            case "2":
                handleCreateGroup();
                break;
            case "3":
                handleSearchGroups();
                break;
            case "4":
                // Return to main menu
                break;
            default:
                displayMessage("Invalid option. Please try again.");
                getInput("Press Enter to continue...");
                showGroupMenu();
        }
    }
    
    private void showAccountSettingsMenu() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("         ACCOUNT SETTINGS          ");
        displayMessage("===================================");
        displayMessage("1. Delete Account");
        displayMessage("2. Return to Main Menu");
        displayMessage("-----------------------------------");
        
        String choice = getInput("Choose an option (1-2):");
        
        switch (choice) {
            case "1":
                handleDeleteAccount();
                break;
            case "2":
                // Return to main menu
                break;
            default:
                displayMessage("Invalid option. Please try again.");
                getInput("Press Enter to continue...");
                showAccountSettingsMenu();
        }
    }
    
    private void handleLogin() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("                LOGIN              ");
        displayMessage("===================================");
        
        String username = getInput("Enter username:");
        String password = getInput("Enter password:");
        
        try {
            boolean success = userService.login(username, password);
            if (success) {
                displayMessage("Login successful!");
            } else {
                displayMessage("Login failed.");
            }
        } catch (UserNotFoundException | AuthenticationException e) {
            displayMessage("Error: " + e.getMessage());
        }

        getInput("Press Enter to continue...");
    }
    
    private void handleRegistration() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("             REGISTER              ");
        displayMessage("===================================");
        
        String username = getInput("Enter username (3-20 characters):");
        
        // Validate username
        if (!validator.isValidUsername(username)) {
            displayMessage("Error: Username must be 3-20 characters and contain only letters, numbers, and underscores.");
            getInput("Press Enter to continue...");
            return;
        }
        
        String password = getInput("Enter password (6+ characters):");
        String confirmPassword = getInput("Confirm password:");
        
        // Validate password
        if (!validator.isValidPassword(password)) {
            displayMessage("Error: Password must be at least 6 characters.");
            getInput("Press Enter to continue...");
            return;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            displayMessage("Error: Passwords do not match.");
            getInput("Press Enter to continue...");
            return;
        }
        
        boolean success = userService.registerUser(username, password);
        if (success) {
            displayMessage("Registration successful! You can now login.");
        } else {
            displayMessage("Registration failed. Username may already exist.");
        }
        
        getInput("Press Enter to continue...");
    }
    
    private void handleLogout() {
        userService.logout();
        displayMessage("You have been logged out.");
        getInput("Press Enter to continue...");
    }
    
    private void handleExit() {
        displayMessage("Thank you for using Chat App!");
        running = false;
    }
    
    private void handleViewContacts() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("             MY CONTACTS           ");
        displayMessage("===================================");
        
        List<String> contacts = userService.getContacts();
        
        if (contacts.isEmpty()) {
            displayMessage("You don't have any contacts yet.");
        } else {
            for (int i = 0; i < contacts.size(); i++) {
                displayMessage((i + 1) + ". " + contacts.get(i));
            }
            
            displayMessage("-----------------------------------");
            displayMessage("Enter the number to message a contact, or 0 to return:");
            
            try {
                int choice = Integer.parseInt(getInput(""));
                if (choice > 0 && choice <= contacts.size()) {
                    String contactUsername = contacts.get(choice - 1);
                    handleChatWithUser(contactUsername);
                }
            } catch (NumberFormatException e) {
                displayMessage("Invalid input.");
            }
        }
        
        getInput("Press Enter to continue...");
    }
    
    private void handleMessageContact() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("          MESSAGE CONTACT          ");
        displayMessage("===================================");
        
        List<String> contacts = userService.getContacts();
        
        if (contacts.isEmpty()) {
            displayMessage("You don't have any contacts yet.");
            displayMessage("Use 'Search for a User' to find users to message.");
            getInput("Press Enter to continue...");
            return;
        }
        
        for (int i = 0; i < contacts.size(); i++) {
            displayMessage((i + 1) + ". " + contacts.get(i));
        }
        
        displayMessage("-----------------------------------");
        displayMessage("Enter the number to message a contact, or 0 to return:");
        
        try {
            int choice = Integer.parseInt(getInput(""));
            if (choice > 0 && choice <= contacts.size()) {
                String contactUsername = contacts.get(choice - 1);
                handleChatWithUser(contactUsername);
            }
        } catch (NumberFormatException e) {
            displayMessage("Invalid input.");
            getInput("Press Enter to continue...");
        }
    }
    
    private void handleSearchUser() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("           SEARCH USERS            ");
        displayMessage("===================================");
        
        String searchTerm = getInput("Enter username to search for:");
        
        if (searchTerm.isEmpty()) {
            displayMessage("Search term cannot be empty.");
            getInput("Press Enter to continue...");
            return;
        }
        
        List<String> users = userService.searchUsers(searchTerm);
        
        if (users.isEmpty()) {
            displayMessage("No users found matching '" + searchTerm + "'.");
        } else {
            displayMessage("Users found:");
            for (int i = 0; i < users.size(); i++) {
                displayMessage((i + 1) + ". " + users.get(i));
            }
            
            displayMessage("-----------------------------------");
            displayMessage("Enter the number to chat with a user, or 0 to return:");
            
            try {
                int choice = Integer.parseInt(getInput(""));
                if (choice > 0 && choice <= users.size()) {
                    String selectedUsername = users.get(choice - 1);
                    handleChatWithUser(selectedUsername);
                }
            } catch (NumberFormatException e) {
                displayMessage("Invalid input.");
            }
        }
        
        getInput("Press Enter to continue...");
    }

    private void showUnreadMessageNotification() {
        if (!userService.isLoggedIn()) {
            return;
        }

        User currentUser = userService.getCurrentUser();
        String currentUsername = currentUser.getUsername();
        List<Message> allMessages = dataPersistence.loadMessages();
        Map<String, Integer> unreadMessageCounts = new HashMap<>();
        Map<String, Integer> unreadGroupMessageCounts = new HashMap<>();

        for (Message message : allMessages) {
            if (!message.isGroupMessage() && message.getReceiverId().equals(currentUsername)) {
                String senderId = message.getSenderId();
                LocalDateTime lastReadTime = currentUser.getLastReadTime(senderId);

                if (lastReadTime == null || message.getTimestamp().isAfter(lastReadTime)) {
                    unreadMessageCounts.put(senderId, unreadMessageCounts.getOrDefault(senderId, 0) + 1);
                }
            }
            else if (message.isGroupMessage()) {
                try {
                    String groupId = message.getReceiverId();
                    Group group = groupService.getGroupById(groupId);

                    if (group.isMember(currentUsername) && !message.getSenderId().equals(currentUsername)) {
                        LocalDateTime lastReadGroupTime = currentUser.getLastReadGroupTime(groupId);

                        if (lastReadGroupTime == null || message.getTimestamp().isAfter(lastReadGroupTime)) {
                            unreadGroupMessageCounts.put(groupId, unreadGroupMessageCounts.getOrDefault(groupId, 0) + 1);
                        }
                    }
                } catch (GroupNotFoundException e) {
                }
            }
        }

        if (!unreadMessageCounts.isEmpty()) {
            displayMessage("\n*** UNREAD MESSAGES ***");
            for (Map.Entry<String, Integer> entry : unreadMessageCounts.entrySet()) {
                displayMessage("- " + entry.getValue() + " message(s) from " + entry.getKey());
            }
        }

        if (!unreadGroupMessageCounts.isEmpty()) {
            if (unreadMessageCounts.isEmpty()) {
                displayMessage("\n*** UNREAD MESSAGES ***");
            }
            for (Map.Entry<String, Integer> entry : unreadGroupMessageCounts.entrySet()) {
                try {
                    Group group = groupService.getGroupById(entry.getKey());
                    displayMessage("- " + entry.getValue() + " message(s) in group " + group.getGroupName());
                } catch (GroupNotFoundException e) {
                }
            }
        }
    }

    private void handleChatWithUser(String username) {
        clearScreen();
        displayMessage("===================================");
        displayMessage("        CHAT WITH " + username);
        displayMessage("===================================");

        userService.addContact(username);

        List<Message> messages = messageService.getDirectMessagesBetweenUsers(
                userService.getCurrentUsername(), username);

        if (messages.isEmpty()) {
            displayMessage("No previous messages. Start chatting!");
        } else {
            displayMessage("Message History:");
            for (Message message : messages) {
                String prefix = message.getSenderId().equals(userService.getCurrentUsername()) ? "You" : username;
                displayMessage("[" + message.getFormattedTimestamp() + "] " + prefix + ": " + message.getContent());
            }
        }

        User currentUser = userService.getCurrentUser();
        currentUser.updateLastReadTime(username);
        userService.saveUser(currentUser);

        displayMessage("-----------------------------------");
        displayMessage("Type your message or enter 'EXIT' to return:");

        while (true) {
            String messageContent = getInput("");

            if (messageContent.equalsIgnoreCase("EXIT")) {
                currentUser = userService.getCurrentUser();
                currentUser.updateLastReadTime(username);
                userService.saveUser(currentUser);
                break;
            }

            if (!messageContent.isEmpty()) {
                Message sentMessage = messageService.sendDirectMessage(
                        userService.getCurrentUsername(),
                        username,
                        messageContent
                );

                displayMessage("[" + sentMessage.getFormattedTimestamp() + "] You: " + sentMessage.getContent());
            }
        }
    }


    
    private void handleViewMyGroups() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("             MY GROUPS             ");
        displayMessage("===================================");
        
        List<Group> groups = groupService.getUserGroups(userService.getCurrentUsername());
        
        if (groups.isEmpty()) {
            displayMessage("You are not a member of any groups yet.");
            displayMessage("Use 'Create a New Group' or 'Search for Groups' to find groups to join.");
        } else {
            for (int i = 0; i < groups.size(); i++) {
                Group group = groups.get(i);
                String adminIndicator = group.isAdmin(userService.getCurrentUsername()) ? " (Admin)" : "";
                displayMessage((i + 1) + ". " + group.getGroupName() + adminIndicator);
            }
            
            displayMessage("-----------------------------------");
            displayMessage("Enter the number to select a group, or 0 to return:");
            
            try {
                int choice = Integer.parseInt(getInput(""));
                if (choice > 0 && choice <= groups.size()) {
                    Group selectedGroup = groups.get(choice - 1);
                    handleGroupDetails(selectedGroup);
                }
            } catch (NumberFormatException e) {
                displayMessage("Invalid input.");
            }
        }
        
        getInput("Press Enter to continue...");
    }
    
    private void handleCreateGroup() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("          CREATE NEW GROUP         ");
        displayMessage("===================================");
        
        String groupName = getInput("Enter group name (3-30 characters):");
        
        if (!validator.isValidGroupName(groupName)) {
            displayMessage("Error: Group name must be 3-30 characters.");
            getInput("Press Enter to continue...");
            return;
        }
        
        Group newGroup = groupService.createGroup(groupName, userService.getCurrentUsername());
        
        if (newGroup != null) {
            displayMessage("Group '" + groupName + "' created successfully!");
            displayMessage("You are the admin of this group.");
        } else {
            displayMessage("Failed to create group.");
        }
        
        getInput("Press Enter to continue...");
    }
    
    private void handleSearchGroups() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("           SEARCH GROUPS           ");
        displayMessage("===================================");
        
        String searchTerm = getInput("Enter group name to search for:");
        
        if (searchTerm.isEmpty()) {
            displayMessage("Search term cannot be empty.");
            getInput("Press Enter to continue...");
            return;
        }
        
        List<Group> groups = groupService.searchGroups(searchTerm);
        
        if (groups.isEmpty()) {
            displayMessage("No groups found matching '" + searchTerm + "'.");
        } else {
            displayMessage("Groups found:");
            for (int i = 0; i < groups.size(); i++) {
                Group group = groups.get(i);
                String membership = group.isMember(userService.getCurrentUsername()) ? " (Member)" : "";
                displayMessage((i + 1) + ". " + group.getGroupName() + membership);
            }
            
            displayMessage("-----------------------------------");
            displayMessage("Enter the number to select a group, or 0 to return:");
            
            try {
                int choice = Integer.parseInt(getInput(""));
                if (choice > 0 && choice <= groups.size()) {
                    Group selectedGroup = groups.get(choice - 1);
                    
                    if (selectedGroup.isMember(userService.getCurrentUsername())) {
                        handleGroupDetails(selectedGroup);
                    } else {
                        handleNonMemberGroupOptions(selectedGroup);
                    }
                }
            } catch (NumberFormatException e) {
                displayMessage("Invalid input.");
            }
        }
        
        getInput("Press Enter to continue...");
    }
    
    private void handleGroupDetails(Group group) {
        boolean isAdmin = group.isAdmin(userService.getCurrentUsername());
        
        while (true) {
            clearScreen();
            displayMessage("===================================");
            displayMessage("         GROUP: " + group.getGroupName());
            displayMessage("===================================");
            displayMessage("Admin: " + group.getAdminUsername());
            displayMessage("Members: " + group.getMembers().size());
            displayMessage("-----------------------------------");
            displayMessage("1. View Group Chat");
            displayMessage("2. View Members");
            
            if (isAdmin) {
                displayMessage("3. View Join Requests");
                displayMessage("4. Remove Member");
                displayMessage("5. Delete Group");
                displayMessage("6. Return");
            } else {
                displayMessage("3. Leave Group");
                displayMessage("4. Return");
            }
            
            displayMessage("-----------------------------------");
            
            String choice = getInput("Choose an option:");
            
            try {
                if (isAdmin) {
                    switch (choice) {
                        case "1":
                            handleGroupChat(group);
                            break;
                        case "2":
                            handleViewGroupMembers(group);
                            break;
                        case "3":
                            handleViewJoinRequests(group);
                            break;
                        case "4":
                            handleRemoveMember(group);
                            break;
                        case "5":
                            if (handleDeleteGroup(group)) {
                                return; // Group deleted, return to previous menu
                            }
                            break;
                        case "6":
                            return;
                        default:
                            displayMessage("Invalid option.");
                            getInput("Press Enter to continue...");
                    }
                } else {
                    switch (choice) {
                        case "1":
                            handleGroupChat(group);
                            break;
                        case "2":
                            handleViewGroupMembers(group);
                            break;
                        case "3":
                            if (handleLeaveGroup(group)) {
                                return; // Left group, return to previous menu
                            }
                            break;
                        case "4":
                            return;
                        default:
                            displayMessage("Invalid option.");
                            getInput("Press Enter to continue...");
                    }
                }
            } catch (GroupNotFoundException e) {
                displayMessage("Error: Group not found. It may have been deleted.");
                getInput("Press Enter to continue...");
                return;
            }
        }
    }
    
    private void handleNonMemberGroupOptions(Group group) {
        clearScreen();
        displayMessage("===================================");
        displayMessage("         GROUP: " + group.getGroupName());
        displayMessage("===================================");
        displayMessage("Admin: " + group.getAdminUsername());
        displayMessage("Members: " + group.getMembers().size());
        displayMessage("-----------------------------------");
        
        try {
            boolean hasPendingRequest = group.hasPendingRequest(userService.getCurrentUsername());
            
            if (hasPendingRequest) {
                displayMessage("You have a pending request to join this group.");
                displayMessage("Wait for the admin to accept your request.");
            } else {
                displayMessage("1. Request to Join Group");
                displayMessage("2. Return");
                
                String choice = getInput("Choose an option:");
                
                if (choice.equals("1")) {
                    boolean success = groupService.requestToJoinGroup(
                            group.getGroupId(), 
                            userService.getCurrentUsername()
                    );
                    
                    if (success) {
                        displayMessage("Join request sent. Wait for the admin to accept your request.");
                    } else {
                        displayMessage("Failed to send join request.");
                    }
                }
            }
        } catch (GroupNotFoundException e) {
            displayMessage("Error: Group not found. It may have been deleted.");
        }
        
        getInput("Press Enter to continue...");
    }

    private void handleGroupChat(Group group) throws GroupNotFoundException {
        clearScreen();
        displayMessage("===================================");
        displayMessage("      GROUP CHAT: " + group.getGroupName());
        displayMessage("===================================");

        List<Message> messages = messageService.getGroupMessages(group.getGroupId());

        if (messages.isEmpty()) {
            displayMessage("No previous messages. Start chatting!");
        } else {
            for (Message message : messages) {
                String sender = message.getSenderId().equals(userService.getCurrentUsername()) ? "You" : message.getSenderId();
                displayMessage("[" + message.getFormattedTimestamp() + "] " + sender + ": " + message.getContent());
            }
        }

        User currentUser = userService.getCurrentUser();
        currentUser.updateLastReadGroupTime(group.getGroupId());
        userService.saveUser(currentUser);

        displayMessage("-----------------------------------");
        displayMessage("Type your message or enter 'EXIT' to return:");

        while (true) {
            String messageContent = getInput("");

            if (messageContent.equalsIgnoreCase("EXIT")) {
                // Update the last read time again when exiting to capture any new messages
                currentUser = userService.getCurrentUser();
                currentUser.updateLastReadGroupTime(group.getGroupId());
                userService.saveUser(currentUser);
                break;
            }

            if (!messageContent.isEmpty()) {
                Message sentMessage = messageService.sendGroupMessage(
                        userService.getCurrentUsername(),
                        group.getGroupId(),
                        messageContent
                );

                displayMessage("[" + sentMessage.getFormattedTimestamp() + "] You: " + sentMessage.getContent());
            }
        }
    }
    
    private void handleViewGroupMembers(Group group) {
        clearScreen();
        displayMessage("===================================");
        displayMessage("    MEMBERS OF " + group.getGroupName());
        displayMessage("===================================");
        
        int count = 1;
        for (String memberUsername : group.getMembers()) {
            String adminIndicator = group.isAdmin(memberUsername) ? " (Admin)" : "";
            displayMessage(count + ". " + memberUsername + adminIndicator);
            count++;
        }
        
        getInput("Press Enter to continue...");
    }
    
    private void handleViewJoinRequests(Group group) throws GroupNotFoundException {
        clearScreen();
        displayMessage("===================================");
        displayMessage("  JOIN REQUESTS FOR " + group.getGroupName());
        displayMessage("===================================");
        
        List<JoinRequest> requests = groupService.getPendingJoinRequests(
                group.getGroupId(), 
                userService.getCurrentUsername()
        );
        
        if (requests.isEmpty()) {
            displayMessage("No pending join requests.");
        } else {
            for (int i = 0; i < requests.size(); i++) {
                JoinRequest request = requests.get(i);
                displayMessage((i + 1) + ". " + request.getRequestorUsername());
            }
            
            displayMessage("-----------------------------------");
            displayMessage("Enter the number to accept/reject a request, or 0 to return:");
            
            try {
                int choice = Integer.parseInt(getInput(""));
                if (choice > 0 && choice <= requests.size()) {
                    JoinRequest selectedRequest = requests.get(choice - 1);
                    handleJoinRequestResponse(group, selectedRequest);
                }
            } catch (NumberFormatException e) {
                displayMessage("Invalid input.");
            }
        }
        
        getInput("Press Enter to continue...");
    }
    
    private void handleJoinRequestResponse(Group group, JoinRequest request) throws GroupNotFoundException {
        clearScreen();
        displayMessage("===================================");
        displayMessage("        JOIN REQUEST FROM          ");
        displayMessage("        " + request.getRequestorUsername());
        displayMessage("===================================");
        displayMessage("1. Accept Request");
        displayMessage("2. Reject Request");
        displayMessage("3. Return");
        
        String choice = getInput("Choose an option:");
        
        switch (choice) {
            case "1":
                boolean accepted = groupService.acceptJoinRequest(
                        group.getGroupId(),
                        request.getRequestorUsername(),
                        userService.getCurrentUsername()
                );
                
                if (accepted) {
                    displayMessage("Request accepted. " + request.getRequestorUsername() + " is now a member.");
                } else {
                    displayMessage("Failed to accept request.");
                }
                break;
            case "2":
                boolean rejected = groupService.rejectJoinRequest(
                        group.getGroupId(),
                        request.getRequestorUsername(),
                        userService.getCurrentUsername()
                );
                
                if (rejected) {
                    displayMessage("Request rejected.");
                } else {
                    displayMessage("Failed to reject request.");
                }
                break;
            default:
                // Return to previous menu
                break;
        }
    }
    
    private void handleRemoveMember(Group group) throws GroupNotFoundException {
        clearScreen();
        displayMessage("===================================");
        displayMessage("     REMOVE MEMBER FROM GROUP      ");
        displayMessage("===================================");
        
        List<String> members = new ArrayList<>(group.getMembers());
        members.remove(userService.getCurrentUsername()); // Don't show admin
        
        if (members.isEmpty()) {
            displayMessage("No members to remove (besides yourself as admin).");
        } else {
            for (int i = 0; i < members.size(); i++) {
                displayMessage((i + 1) + ". " + members.get(i));
            }
            
            displayMessage("-----------------------------------");
            displayMessage("Enter the number to remove a member, or 0 to return:");
            
            try {
                int choice = Integer.parseInt(getInput(""));
                if (choice > 0 && choice <= members.size()) {
                    String memberToRemove = members.get(choice - 1);
                    
                    displayMessage("Are you sure you want to remove " + memberToRemove + " from the group? (y/n)");
                    String confirm = getInput("").toLowerCase();
                    
                    if (confirm.equals("y") || confirm.equals("yes")) {
                        boolean removed = groupService.removeMemberFromGroup(
                                group.getGroupId(),
                                memberToRemove,
                                userService.getCurrentUsername()
                        );
                        
                        if (removed) {
                            displayMessage(memberToRemove + " has been removed from the group.");
                        } else {
                            displayMessage("Failed to remove member.");
                        }
                    } else {
                        displayMessage("Member removal cancelled.");
                    }
                }
            } catch (NumberFormatException e) {
                displayMessage("Invalid input.");
            }
        }
        
        getInput("Press Enter to continue...");
    }
    
    private boolean handleDeleteGroup(Group group) throws GroupNotFoundException {
        clearScreen();
        displayMessage("===================================");
        displayMessage("           DELETE GROUP            ");
        displayMessage("===================================");
        displayMessage("Are you sure you want to delete the group '" + group.getGroupName() + "'?");
        displayMessage("This action cannot be undone!");
        displayMessage("Type 'DELETE' to confirm, or anything else to cancel:");
        
        String confirm = getInput("");
        
        if (confirm.equals("DELETE")) {
            boolean deleted = groupService.deleteGroup(
                    group.getGroupId(),
                    userService.getCurrentUsername()
            );
            
            if (deleted) {
                // Delete all group messages
                messageService.deleteGroupMessages(group.getGroupId());
                
                displayMessage("Group '" + group.getGroupName() + "' has been deleted.");
                getInput("Press Enter to continue...");
                return true;
            } else {
                displayMessage("Failed to delete group.");
                getInput("Press Enter to continue...");
                return false;
            }
        } else {
            displayMessage("Group deletion cancelled.");
            getInput("Press Enter to continue...");
            return false;
        }
    }
    
    private boolean handleLeaveGroup(Group group) throws GroupNotFoundException {
        clearScreen();
        displayMessage("===================================");
        displayMessage("            LEAVE GROUP            ");
        displayMessage("===================================");
        displayMessage("Are you sure you want to leave the group '" + group.getGroupName() + "'? (y/n)");
        
        String confirm = getInput("").toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            Group updatedGroup = groupService.getGroupById(group.getGroupId());
            updatedGroup.removeMember(userService.getCurrentUsername());

            User user = userService.getCurrentUser();
            user.removeGroup(group.getGroupId());
            userService.saveUser(user);

            Map<String, Group> groups = dataPersistence.loadGroups();
            groups.put(group.getGroupId(), updatedGroup);
            dataPersistence.saveGroups(groups);
            
            displayMessage("You have left the group '" + group.getGroupName() + "'.");
            getInput("Press Enter to continue...");
            return true;
        } else {
            displayMessage("Group exit cancelled.");
            getInput("Press Enter to continue...");
            return false;
        }
    }
    
    private void handleDeleteAccount() {
        clearScreen();
        displayMessage("===================================");
        displayMessage("          DELETE ACCOUNT           ");
        displayMessage("===================================");
        displayMessage("Are you sure you want to delete your account?");
        displayMessage("This action cannot be undone!");
        displayMessage("Type 'DELETE' to confirm, or anything else to cancel:");
        
        String confirm = getInput("");
        
        if (confirm.equals("DELETE")) {
            messageService.deleteUserMessages(userService.getCurrentUsername());

            List<Group> userGroups = groupService.getUserGroups(userService.getCurrentUsername());
            for (Group group : userGroups) {
                try {
                    if (group.isAdmin(userService.getCurrentUsername())) {
                        groupService.deleteGroup(group.getGroupId(), userService.getCurrentUsername());
                        messageService.deleteGroupMessages(group.getGroupId());
                    } else {
                        group.removeMember(userService.getCurrentUsername());

                        Map<String, Group> groups = dataPersistence.loadGroups();
                        groups.put(group.getGroupId(), group);
                        dataPersistence.saveGroups(groups);
                    }
                } catch (GroupNotFoundException e) {
                }
            }

            boolean deleted = userService.deleteAccount();
            
            if (deleted) {
                displayMessage("Your account has been deleted.");
                getInput("Press Enter to continue...");
            } else {
                displayMessage("Failed to delete account.");
                getInput("Press Enter to continue...");
            }
        } else {
            displayMessage("Account deletion cancelled.");
            getInput("Press Enter to continue...");
        }
    }
}

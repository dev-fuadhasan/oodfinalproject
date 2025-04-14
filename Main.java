package com.chatapp;

import com.chatapp.service.GroupService;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;
import com.chatapp.ui.ConsoleUI;
import com.chatapp.ui.UserInterface;
import com.chatapp.util.DataPersistence;

/**
 * Main entry point for the Chat Application.
 * Initializes services and UI, then starts the application.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize data persistence
        DataPersistence dataPersistence = new DataPersistence();

        // Initialize services
        UserService userService = new UserService(dataPersistence);
        MessageService messageService = new MessageService(dataPersistence);
        GroupService groupService = new GroupService(dataPersistence, userService);

        // Initialize UI
        UserInterface ui = new ConsoleUI(userService, messageService, groupService);

        // Start the application
        System.out.println("Welcome to Chat App!");
        ui.start();
    }
}

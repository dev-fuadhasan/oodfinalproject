package com.chatapp;

import com.chatapp.service.GroupService;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;
import com.chatapp.ui.ConsoleUI;
import com.chatapp.ui.UserInterface;
import com.chatapp.util.DataPersistence;

public class Main {
    public static void main(String[] args) {
        DataPersistence dataPersistence = new DataPersistence();

        UserService userService = new UserService(dataPersistence);
        MessageService messageService = new MessageService(dataPersistence);
        GroupService groupService = new GroupService(dataPersistence, userService);

        UserInterface ui = new ConsoleUI(userService, messageService, groupService);

        System.out.println("Welcome to Chat App!");
        ui.start();
    }
}

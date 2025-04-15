package com.chatapp.ui;

public interface UserInterface {

    void start();
    void displayMessage(String message);
    String getInput(String prompt);
    void clearScreen();
    void showMainMenu();
    void showAuthMenu();
    void showMessagingMenu();
    void showGroupMenu();
}

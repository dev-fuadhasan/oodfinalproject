package com.chatapp.ui;

/**
 * Interface for the user interface of the chat application.
 * Defines methods that any UI implementation must provide.
 */
public interface UserInterface {
    /**
     * Start the user interface.
     */
    void start();
    
    /**
     * Display a message to the user.
     * 
     * @param message the message to display
     */
    void displayMessage(String message);
    
    /**
     * Get input from the user.
     * 
     * @param prompt the prompt to display
     * @return the user's input
     */
    String getInput(String prompt);
    
    /**
     * Clear the screen or provide visual separation.
     */
    void clearScreen();
    
    /**
     * Show the main menu.
     */
    void showMainMenu();
    
    /**
     * Show the authentication menu.
     */
    void showAuthMenu();
    
    /**
     * Show the messaging menu.
     */
    void showMessagingMenu();
    
    /**
     * Show the group menu.
     */
    void showGroupMenu();
}

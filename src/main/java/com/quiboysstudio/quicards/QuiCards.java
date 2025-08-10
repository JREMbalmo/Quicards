package com.quiboysstudio.quicards;

public class QuiCards {
    
    //variables
    private static boolean activeStatus = true;

    public static void main(String[] args) {
        init();
        run();
    }

    private static void init() {
        
        //initialize states
        State.exitState = new ExitState();
        State.startScreen = new StartScreen();                      //loading screen
        
        State.serverMenu = new ServerMenu();                        //server menu
        State.hostServerMenu = new HostServerMenu();                //host server menu
        State.joinServerMenu = new JoinServerMenu();                //join server menu
        State.loginMenu = new LoginMenu();                          //login account menu
        State.registerMenu = new RegisterMenu();                    //register account menu
        
        State.mainMenu = new MainMenu();                            // main menu
        
        //setup current state after opening app
        State.currentState = State.startScreen;
    }

    private static void run() {
        
        //run app
        while (activeStatus) {
            State.currentState.enter();
            State.currentState.update();
        }
    }
    
    public static void setActiveStatus(boolean status) {
        activeStatus = status;
    }
}

/*
MySQL commands

-- Delete the old database
DROP DATABASE Server;

-- Create the database
CREATE DATABASE Server;

-- Use the newly created database
USE Server;

-- Create serverdetails table
CREATE TABLE ServerDetails (
    name VARCHAR(32) UNIQUE,
    serverOwner VARCHAR(32) UNIQUE,
    serverIP VARCHAR(64) UNIQUE,
    isMasterServer TINYINT(1) NOT NULL DEFAULT 0,
    masterServerIP VARCHAR(64) UNIQUE
);

-- Create the users table
CREATE TABLE Users (
    ID INT PRIMARY KEY,
    name VARCHAR(32) UNIQUE,
    password TEXT
);
*/
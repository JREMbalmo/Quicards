package com.quiboysstudio.quicards.QuiCards;

import com.quiboysstudio.quicards.managers.StateManager;

public class QuiCards {
    public static void main(String[] args) {
        StateManager.init();
        StateManager.run();
    }
}
/*
MySQL server setup

bind address to 0.0.0.0
grant all privileges on *.* to 'root'@'%' identified by 'root';
go to router login page
go to port forwarding
enable rule


MySQL commands

-- Delete the old database
DROP DATABASE Server;

-- Create the database
CREATE DATABASE Server;

-- Use the newly created database
USE Server;

-- Create serverdetails table
CREATE TABLE ServerDetails (
    ID TINYINT(1) NOT NULL PRIMARY KEY DEFAULT 1,
    ServerName VARCHAR(32),
    CurrentHost VARCHAR(128),
    CONSTRAINT ck_single_row CHECK (ID = 1)
);

-- Create users table
CREATE TABLE Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(16) UNIQUE NOT NULL,
    Password TEXT NOT NULL,
    Seed BIGINT NOT NULL,
    FOREIGN KEY (Username) REFERENCES AccountCreation(Username),
    FOREIGN KEY (Password) REFERENCES AccountCreation(Password)
) AUTO_INCREMENT = 100000;

-- Create actions table
CREATE TABLE Actions (
    ActionsID INT PRIMARY KEY AUTO_INCREMENT,
    UserID(16) NOT NULL,
    Action VARCHAR(128) NOT NULL,
    Status TINYINT(1) NOT NULL,
    FOREIGN KEY (User) REFERENCES Users(Username)
) AUTO_INCREMENT = 1;

-- Create account creation table
CREATE TABLE AccountCreation (
    CreationID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(16) UNIQUE NOT NULL,
    Password TEXT NOT NULL
) AUTO_INCREMENT = 1;
*/
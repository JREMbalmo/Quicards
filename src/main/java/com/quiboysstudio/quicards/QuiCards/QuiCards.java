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
    ID TINYINT(1) PRIMARY KEY AUTO_INCREMENT,
    ServerName VARCHAR(128),
    CurrentHost VARCHAR(128),
    CONSTRAINT ck_single_row CHECK (ID = 1)
) AUTO_INCREMENT = 1;

-- Create users table
CREATE TABLE Users (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(32) UNIQUE NOT NULL,
    Password TEXT NOT NULL,
    Seed BIGINT NOT NULL
) AUTO_INCREMENT = 100000;

-- Create actions table
CREATE TABLE Actions (
    ActionsID INT PRIMARY KEY AUTO_INCREMENT,
    User VARCHAR(32) NOT NULL,
    Action VARCHAR(128) NOT NULL,
    Status TINYINT(1) NOT NULL,
    FOREIGN KEY (User) REFERENCES Users(Username)
) AUTO_INCREMENT = 1;
*/
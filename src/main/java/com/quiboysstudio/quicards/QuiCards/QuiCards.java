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
    serverName VARCHAR(32) UNIQUE,
    host VARCHAR(32) UNIQUE,
    serverIP VARCHAR(64) UNIQUE
);

-- Create peerservers table
CREATE TABLE PeerServers (
    serverIP VARCHAR(64) UNIQUE,
    isActive TINYINT(1) NOT NULL DEFAULT 0
);

-- Create the users table
CREATE TABLE Users (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(32) UNIQUE,
    password TEXT,
    seed BIGINT NOT NULL
) AUTO_INCREMENT = 100000;
*/
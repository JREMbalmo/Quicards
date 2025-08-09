package com.quiboysstudio.quicards;

public class QuiCards {

    public static void main(String[] args) {
        
        //initialize states
        State.startScreen = new StartScreen();
        State.serverMenu = new ServerMenu();
        State.loginMenu = new LoginMenu();
        State.mainMenu = new MainMenu();
        
        //start app
        State.currentState = State.startScreen;
        
        while (true) {
            State.currentState.enter();
            State.currentState.update();
        }
        
        
    }
}

/*
-- Delete the old database
DROP DATABASE `1stSem_SY2025_2026`;

-- Create the database
CREATE DATABASE `1stSem_SY2025_2026`;

-- Use the newly created database
USE `1stSem_SY2025_2026`;

-- Create the students table
CREATE TABLE Students (
    studentID INT PRIMARY KEY,
    name TEXT,
    address TEXT,
    contact TEXT,
    email TEXT,
    course TEXT,
    gender TEXT,
    year TEXT
);

-- Create the subjects table
CREATE TABLE Subjects (
    subjectID INT PRIMARY KEY,
    subjectUnits INT,
    subjectCode TEXT,
    subjectSchedule TEXT,
    subjectDescription TEXT
);

-- Create the teachers table
CREATE TABLE Teachers (
    teacherID INT PRIMARY KEY,
    teacherName TEXT,
    teacherAddress TEXT,
    teacherContact TEXT,
    teacherEmail TEXT,
    teacherDepartment TEXT
);

-- Create the enrolled table
CREATE TABLE Enroll (
    eid INT PRIMARY KEY,
    studid INT,
    subjid INT
);

-- Create the assign table
CREATE TABLE Assign (
    subid INT,
    tid INT
);

-- Delete the old database
DROP DATABASE `2ndSem_SY2025_2026`;

-- Create the database
CREATE DATABASE `2ndSem_SY2025_2026`;

-- Use the newly created database
USE `2ndSem_SY2025_2026`;

-- Create the students table
CREATE TABLE Students (
    studentID INT PRIMARY KEY,
    name TEXT,
    address TEXT,
    contact TEXT,
    email TEXT,
    course TEXT,
    gender TEXT,
    year TEXT
);

-- Create the subjects table
CREATE TABLE Subjects (
    subjectID INT PRIMARY KEY,
    subjectUnits INT,
    subjectCode TEXT,
    subjectSchedule TEXT,
    subjectDescription TEXT
);

-- Create the teachers table
CREATE TABLE Teachers (
    teacherID INT PRIMARY KEY,
    teacherName TEXT,
    teacherAddress TEXT,
    teacherContact TEXT,
    teacherEmail TEXT,
    teacherDepartment TEXT
);


-- Create the enrolled table
CREATE TABLE Enroll (
    eid INT PRIMARY KEY,
    studid INT,
    subjid INT
);

-- Create the assign table
CREATE TABLE Assign (
    subid INT,
    tid INT
);
*/
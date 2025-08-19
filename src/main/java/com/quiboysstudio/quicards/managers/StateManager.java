package com.quiboysstudio.quicards.managers;

//imports
import com.quiboysstudio.quicards.states.*;
import java.awt.BorderLayout;
import javax.swing.JFrame;

public class StateManager {
    //variables
    private static boolean activeStatus = true;

    public static void init() {
        //initialize states
        State.exitState = new ExitState();
        State.startScreen = new StartScreen();                      //loading screen
        
        State.serverMenu = new ServerMenu();                        //server menu
        State.hostServerMenu = new HostServerMenu();                //host server menu
        State.joinServerMenu = new JoinServerMenu();                //join server menu
        State.loginMenu = new LoginMenu();                          //login account menu
        State.registerMenu = new RegisterMenu();                    //register account menu
        
        State.mainMenu = new MainMenu();                            // main menu
        
        //setup frame
        State.frame.setSize(1920,1080); //standard 1080p
        State.frame.setResizable(false);
        State.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        State.frame.setLayout(new BorderLayout());
        State.frame.setLocationRelativeTo(null);
        State.frame.setTitle("QuiCards");
        
        State.frame.setVisible(true);
        
        //setup initial current state after opening app
        State.currentState = State.startScreen;
    }

    public static void run() {
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

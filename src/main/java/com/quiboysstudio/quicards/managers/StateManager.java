package com.quiboysstudio.quicards.managers;

//imports
import com.quiboysstudio.quicards.configs.FrameConfig;
import com.quiboysstudio.quicards.states.*;

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
        
        //setup initial current state after opening app
        State.currentState = State.serverMenu; //State.startScreen;
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
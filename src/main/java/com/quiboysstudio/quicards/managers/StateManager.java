package com.quiboysstudio.quicards.managers;

//imports
import com.quiboysstudio.quicards.states.*;
import java.util.Timer;
import java.util.TimerTask;

public class StateManager {
    //objects
    private static Timer mainLoop;
    private static TimerTask task;
    
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
        State.currentState = State.startScreen;
        
        //setup timer
        task = new TimerTask() {
            @Override
            public void run() {
                State.currentState.enter();
                State.currentState.update();
            }
        };
        
        mainLoop = new Timer();
    }

    public static void run() {
        //run main loop every 1ms
        mainLoop.scheduleAtFixedRate(task, 0, 1);
    }
    
    public static void off() {
        mainLoop.cancel();
    }
}
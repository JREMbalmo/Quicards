package com.quiboysstudio.quicards.managers;

//imports
import com.quiboysstudio.quicards.states.misc.*;
import com.quiboysstudio.quicards.states.postlaunchmenu.*;
import com.quiboysstudio.quicards.states.prelaunchmenu.*;
import com.quiboysstudio.quicards.states.*;
import com.quiboysstudio.quicards.states.matchmaking.*;
import com.quiboysstudio.quicards.states.store.*;
import java.util.Timer;
import java.util.TimerTask;

public class StateManager {
    //objects
    private static final Timer mainLoop = new Timer();
    private static TimerTask task;
    
    public static void init() {
        //initialize states
        
        //misc states
        State.exitState = new ExitState();
        State.wipState = new WIPState();
        
        //loading screen
        State.startScreen = new StartScreen();                              //loading screen
        
        //pre-launch page
        State.serverMenu = new ServerMenu();                                //server menu
        State.hostServerMenu = new HostServerMenu();                        //host server menu
        State.joinServerMenu = new JoinServerMenu();                        //join server menu
        State.accountAuthenticationMenu = new AccountAuthenticationMenu();  //authentication menu
        State.loginMenu = new LoginMenu();                                  //login account menu
        State.registerMenu = new RegisterMenu();                            //register account menu
        
        //post-launch page
        State.mainMenu = new MainMenu();                                    // main menu
        State.inventoryMenu = new InventoryMenu();                          //inventory menu
        State.settingsMenu = new SettingsMenu();                            //settings menu
        
        //store
        State.storeMenu = new StoreMenu();                                  //store
        
        //matchmaking
        State.createRoomMenu = new CreateRoomMenu();                        //create room
        State.joinRoomMenu = new JoinRoomMenu();                            //join room
        
        //setup initial current state after opening app
        State.currentState = State.serverMenu;
        
        //setup timer
        task = new TimerTask() {
            @Override
            public void run() {
                State.currentState.enter();
                State.currentState.update();
            }
        };
    }

    public static void run() {
        //run main loop every 200ms
        mainLoop.scheduleAtFixedRate(task, 0, 200);
    }
    
    public static void off() {
        mainLoop.cancel();
    }
}
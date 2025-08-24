package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.configs.FrameConfig;
import javax.swing.JFrame;

public abstract class State{
    //frame
    public static JFrame frame = FrameConfig.initFrame();
    
    //valid states
    public static State startScreen, serverMenu, loginMenu, mainMenu,
            hostServerMenu, joinServerMenu, registerMenu, exitState, wipState,
            settingsMenu, storeMenu, inventoryMenu, createRoomMenu, joinRoomMenu;
    public static State currentState;
    
    //main methods
    public void enter() {}
    public void update() {}
    public void exit() {}
}
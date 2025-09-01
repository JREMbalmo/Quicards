package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.components.FrameConfig;
import java.awt.CardLayout;
import java.awt.Container;
import javax.swing.JFrame;

public abstract class State{
    //card layout
    public static final CardLayout cardLayout = new CardLayout();
    
    //frame
    public static final JFrame frame = FrameConfig.initFrame();
    public static final Container cardPanel = FrameConfig.getCardPanel();
    
    //valid states
    public static State startScreen, serverMenu, loginMenu, mainMenu,
            hostServerMenu, joinServerMenu, registerMenu, exitState, wipState,
            settingsMenu, storeMenu, inventoryMenu, createRoomMenu, joinRoomMenu;
    public static State currentState, previousState;
    
    //main methods
    public void enter() {}
    public void update() {}
    public void exit() {}
    
    //state changer
    public static void changeState(State state) {
        previousState = currentState;
        currentState = state;
    }
}
package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import java.awt.CardLayout;
import java.awt.Container;
import javax.swing.JFrame;

public abstract class State{
    //card layout
    public static final CardLayout cardLayout = new CardLayout();
    
    //frame
    public static final JFrame frame = FrameUtil.initFrame();
    public static final Container cardPanel = FrameUtil.getCardPanel(cardLayout);
    
    //valid states
    public static State startScreen, serverMenu, loginMenu, mainMenu,
            hostServerMenu, joinServerMenu, accountAuthenticationMenu, registerMenu, exitState,
            wipState, settingsMenu, storeMenu, inventoryMenu, createRoomMenu, joinRoomMenu,
            gachaResultsMenu, packContentsMenu, waitingRoom, room;
    public static State currentState, previousState;
    
    //abstract methods
    public abstract void enter();
    public abstract void update();
    public abstract void exit(State nextState);
    
}
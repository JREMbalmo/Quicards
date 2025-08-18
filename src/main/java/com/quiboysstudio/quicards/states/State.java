package com.quiboysstudio.quicards.states;

import javax.swing.JFrame;

public abstract class State extends JFrame{
    //frame
    public static JFrame frame = new JFrame();
    
    //valid states
    public static State startScreen, serverMenu, loginMenu, mainMenu, currentState,
            hostServerMenu, joinServerMenu, registerMenu, exitState;
    
    //main methods
    public void enter() {}
    public void update() {}
}

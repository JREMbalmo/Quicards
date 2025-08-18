package com.quiboysstudio.quicards;

import javax.swing.JFrame;

public abstract class State extends JFrame{
    //frame
    static JFrame frame = new JFrame();
    
    //valid states
    static State startScreen, serverMenu, loginMenu, mainMenu, currentState,
            hostServerMenu, joinServerMenu, registerMenu, exitState;
    
    //main methods
    public void enter() {}
    public void update() {}
}

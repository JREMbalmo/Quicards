package com.quiboysstudio.quicards;

import java.util.Scanner;
import javax.swing.JFrame;

public abstract class State extends JFrame{
    //valid states
    static State startScreen, serverMenu, loginMenu, mainMenu, currentState,
            hostServerMenu, joinServerMenu, registerMenu, exitState;
    
    //main methods
    public void enter() {}
    public void update() {}
    public void exit() {}
}

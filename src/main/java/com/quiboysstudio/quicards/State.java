package com.quiboysstudio.quicards;

import java.util.Scanner;

public abstract class State {
    //valid states
    static State startScreen, serverMenu, loginMenu, mainMenu, currentState;
    
    public void enter() {}
    public void update() {}
    public void exit() {}
}

package com.quiboysstudio.quicards.states.misc;

import com.quiboysstudio.quicards.states.State;

public class ExitState extends State{
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        exitApp();
    }

    private void exitApp() {
        System.out.println("Preparing to exit app");
        exit();
        frame.dispose();
        System.exit(0);
    }
    
    private void init() {
        System.out.println("Initializing elements from Exit state");
        System.out.println("Entering Exit state");
    }

    @Override
    public void exit() {
        System.out.println("Removing elements from Exit");
        System.out.println("Exiting app");
    }
}

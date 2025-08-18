package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.managers.StateManager;

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
        StateManager.setActiveStatus(false);
        exit();
    }
    
    private void init() {
        System.out.println("Initializing elements from Exit state");
        System.out.println("Entering Exit state");
    }

    private void exit() {
        System.out.println("Removing elements from Exit");
        System.out.println("Exiting app");
    }
}

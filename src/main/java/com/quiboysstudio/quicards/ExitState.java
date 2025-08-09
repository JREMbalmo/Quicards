package com.quiboysstudio.quicards;

public class ExitState extends State{
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        exitApp();
    }
    
    @Override
    public void exit() {
        cleanUp();
    }

    private void exitApp() {
        System.out.println("Preparing to exit app");
        QuiCards.setActiveStatus(false);
    }
    
    private void init() {
        System.out.println("Initializing elements from Exit state");
        System.out.println("Entering Exit state");
    }

    private void cleanUp() {
        System.out.println("Removing elements from Exit");
        System.out.println("Exiting app");
    }
}

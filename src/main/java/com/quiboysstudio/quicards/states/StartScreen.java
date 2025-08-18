package com.quiboysstudio.quicards.states;

public class StartScreen extends State {

    @Override
    public void enter() {
        init();
    }

    @Override
    public void update() {
        showStartScreen();
    }
    
    private void showStartScreen() {
        System.out.println("Showing start screen");
        
        if (currentState == startScreen) {
            currentState = serverMenu;
            exit();
        }
    }
    
    private void init() {
        System.out.println("Initializing JFrame");
        System.out.println("Initializing elements from StartScreen state");
        System.out.println("Entering StartScreen State");
    }
    
    private void exit() {
        System.out.println("Removing elements from StartScreen state");
        System.out.println("Preparing to transition to next state");
    }
}

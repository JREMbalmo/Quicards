package com.quiboysstudio.quicards;

public class MainMenu extends State{
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showMainMenu();
    }
    
    private void showMainMenu() {
        
    }
    
    private void init() {
        System.out.println("Initializing elements from MainMenu state");
        System.out.println("Entering MainMenu state");
    }
    
    private void exit() {
        System.out.println("Removing elements from MainMenu");
        System.out.println("Preparing to transition to next state");
    }


}

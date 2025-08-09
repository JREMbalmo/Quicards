package com.quiboysstudio.quicards;

public class StartScreen extends State {

    @Override
    public void enter() {
        System.out.println("Initializing JPanel");
        System.out.println("Entering StartScreen State");
    }

    @Override
    public void update() {
        while (true) {
            System.out.println("Showing loading screen");

            if (currentState == startScreen) {
                currentState = serverMenu;
                exit();
                return;
            }
        }
    }

    @Override
    public void exit() {
        System.out.println("Preparing to transition to next state");
    }
}

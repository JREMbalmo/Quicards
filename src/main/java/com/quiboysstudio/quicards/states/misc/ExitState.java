package com.quiboysstudio.quicards.states.misc;

import com.quiboysstudio.quicards.states.State;

public class ExitState extends State{
    @Override
    public void update() {
        exitApp();
    }

    private void exitApp() {
        System.out.println("Preparing to exit app");
        frame.dispose();
        System.exit(0);
    }
}

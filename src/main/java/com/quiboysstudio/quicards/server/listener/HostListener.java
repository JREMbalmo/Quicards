package com.quiboysstudio.quicards.server.listener;

import com.quiboysstudio.quicards.server.handlers.AccountCreationHandler;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.concurrent.*;

public class HostListener {
    
    //variables
    private volatile boolean running = false;
    
    //sql
    private ResultSet result;
    private Statement statement;
    
    //handlers
    private AccountCreationHandler accountCreation;
    
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> taskHandle;
    
    public HostListener (ResultSet result, Statement statement) {
        this.result = result;
        this.statement = statement;
        
        //initialize handlers
        initHandlers();
    }
    
    public void start() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "HostListenerThread");
                t.setDaemon(true); // optional: ends with the app
                return t;
            });

            running = true;

            // Schedule the loop every 1 second
            taskHandle = scheduler.scheduleAtFixedRate(() -> {
                try {
                    if (running) {
                        checkForActions();
                    }
                } catch (Exception e) {
                    System.out.println("Error in HostListener: " + e.getMessage());
                    e.printStackTrace();
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        running = false;

        if (taskHandle != null && !taskHandle.isCancelled()) {
            taskHandle.cancel(true);
        }

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }
    
    public boolean isRunning() {
        return running;
    }

    private void checkForActions() {
        
        System.out.println("Checking for new player actions...");
        
        //check account registrations
        accountCreation.checkActions();
        
        //check for general actions
        
        
    }
    
    private void initHandlers() {
        accountCreation = new AccountCreationHandler(result, statement);
    }
}
package com.quiboysstudio.quicards.states.prelaunchmenu;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.states.store.GachaResultsMenu;
import com.quiboysstudio.quicards.states.store.GachaResultsMenu;
import com.quiboysstudio.quicards.states.store.PackContentsMenu;
import com.quiboysstudio.quicards.states.store.PackContentsMenu;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class StoreMenu extends State{
    //variables
    private boolean running = false;
    private boolean initialized = false;
    private int playerCurrency = 999999; 
    
    //objects
    private JLayeredPane layeredPane;
    private JScrollPane scrollPane;
    private JPanel mainPanel;
    private JPanel firstLayerPanel;
    private JPanel topBarPanel;
    private JPanel navigationPanel;
    private JPanel categoryPanel;
    private JPanel contentPanel;
    private JLabel currencyLabel;
    private JButton backButton;
    
    // For gacha and pack contents logic
    private List<File> cardImages;
    private Random random = new Random();
    
    private String currentTab = "Cards"; 

    @Override
    public void enter() {
        init();
    }

    @Override
    public void update() {
        showMenu();
    }
    
    private void showMenu() {
        if (running) return;
        running = true;
        
        System.out.println("Showing Store menu");
        
        layeredPane.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Store Menu");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from StoreMenu state");
        
        // Load card images on init
        loadCardImages(); 
        
        layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        topBarPanel = new JPanel();
        topBarPanel.setOpaque(false);
        topBarPanel.setLayout(new BorderLayout());
        topBarPanel.setPreferredSize(new Dimension(frame.getWidth(), FrameUtil.scale(frame, 150)));
        
        navigationPanel = new JPanel();
        navigationPanel.setOpaque(false);
        navigationPanel.setLayout(new FlowLayout(FlowLayout.LEFT, FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 20)));
        
        backButton = ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 150, previousState);
        backButton.setVerticalAlignment(JButton.CENTER);
        backButton.setHorizontalAlignment(JButton.CENTER);
        
        currencyLabel = ComponentFactory.createTextLabel(formatCurrency(playerCurrency) + " $", FrameConfig.SATOSHI_BOLD);
        
        navigationPanel.add(backButton);
        navigationPanel.add(currencyLabel);
        
        categoryPanel = new JPanel();
        categoryPanel.setOpaque(false);
        categoryPanel.setLayout(new FlowLayout(FlowLayout.CENTER, FrameUtil.scale(frame, 10), FrameUtil.scale(frame, 20)));
        
        topBarPanel.add(navigationPanel, BorderLayout.WEST);
        topBarPanel.add(categoryPanel, BorderLayout.CENTER);
        
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 30)));
        contentPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50)));
        
        populateCardPacks();
        
        scrollPane = new CustomScrollPane(contentPanel);
        
        firstLayerPanel.add(topBarPanel, BorderLayout.NORTH);
        firstLayerPanel.add(scrollPane, BorderLayout.CENTER);
        
        layeredPane.add(firstLayerPanel, Integer.valueOf(1));
        
        cardPanel.add("Store Menu", layeredPane);
        
        initialized = true;
        
        System.out.println("Entering StoreMenu state");
    }
    
    /**
     * Loads all .png files from the specified card pack directory.
     */
    private void loadCardImages() {
        if (cardImages != null) return; // Only load once
        cardImages = new ArrayList<>();
        try {
            // Use a relative path from the project's root
            File cardDir = new File("resources/cards/Fantasy Card Pack"); 
            if (cardDir.exists() && cardDir.isDirectory()) {
                File[] files = cardDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
                if (files != null) {
                    for (File file : files) {
                        cardImages.add(file);
                    }
                    System.out.println("Loaded " + cardImages.size() + " card images.");
                }
            } else {
                 System.err.println("Could not find card directory: " + cardDir.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void populateCardPacks() {
        String[] packNames = {"Fantasy Card Pack"};
        int[] packPrices = {1500}; 
        
        for (int i = 0; i < packNames.length; i++) {
            JPanel packCard = createPackCard(packNames[i], packPrices[i], true);
            contentPanel.add(packCard);
        }
    }
    
    /**
     * MODIFIED: The "Check Cards" button now transitions to the PackContentsMenu state.
     */
    private JPanel createPackCard(String packName, int price, boolean showBuyButton) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(FrameUtil.scale(frame, 250), FrameUtil.scale(frame, 650))); 
        
        JPanel imagePanel = ComponentFactory.createRoundedPanel(FrameUtil.scale(frame, 230), FrameUtil.scale(frame, 520), FrameConfig.BLACK);
        
        JLabel nameLabel = ComponentFactory.createTextLabel(packName, FrameConfig.SATOSHI_BOLD);
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        JLabel priceLabel = ComponentFactory.createTextLabel(formatCurrency(price) + " $", FrameConfig.SATOSHI_BOLD);
        priceLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        card.add(imagePanel);
        card.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
        card.add(nameLabel);
        
        if (showBuyButton) {
            card.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 5))));
            card.add(priceLabel);
            
            JButton buyButton = ComponentFactory.createCustomButton("Buy Pack", FrameConfig.SATOSHI_BOLD, 250, () -> {
                handlePurchase(packName, price); 
            });
            buyButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
            
            // --- MODIFICATION HERE ---
            JButton checkButton = ComponentFactory.createCustomButton("Check Cards", FrameConfig.SATOSHI_BOLD, 250, () -> {
                handleCheckCards(packName); // Call new handler method
            });
            // --- END MODIFICATION ---
            
            checkButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
            
            card.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
            card.add(buyButton);
            card.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 5))));
            card.add(checkButton);
        }
        
        return card;
    }
    
    private void handlePurchase(String packName, int price) {
        if (playerCurrency >= price) {
            System.out.println("Purchasing: " + packName + " for " + price + " $");
            
            playerCurrency -= price;
            updateCurrencyDisplay();
            
            openCardPack(packName);
        } else {
            System.out.println("Not enough currency to purchase: " + packName);
        }
    }
    
    /**
     * NEW: Handles the "Check Cards" button click.
     * Transitions to the PackContentsMenu state with *all* card images.
     */
    private void handleCheckCards(String packName) {
        System.out.println("Checking cards in: " + packName);

        if (cardImages == null || cardImages.isEmpty()) {
            System.err.println("Card list not loaded! Cannot check cards.");
            return; // Don't do anything if cards aren't loaded
        }
        
        // Convert List<File> to String[] of filenames
        String[] allCardFileNames = new String[cardImages.size()];
        for (int i = 0; i < cardImages.size(); i++) {
            allCardFileNames[i] = cardImages.get(i).getName();
        }
        
        // Sort the array by name to make the display organized
        Arrays.sort(allCardFileNames);
        
        // Create and transition to the new state
        PackContentsMenu contentsState = new PackContentsMenu(allCardFileNames);
        exit(contentsState);
        contentsState.enter();
        contentsState.update();
    }
    
    private void openCardPack(String packName) {
        System.out.println("Opened pack: " + packName);
        
        String[] pulledCardFileNames = new String[10];
        String[] pulledCardUniqueIDs = new String[10]; // For saving
        
        if (cardImages == null || cardImages.isEmpty()) {
            System.err.println("No card images loaded. Filling with dummies.");
            for (int i = 0; i < 10; i++) {
                pulledCardFileNames[i] = "Error - No Cards.png"; 
                pulledCardUniqueIDs[i] = "Error - No Cards.png#" + UUID.randomUUID().toString();
            }
        } else {
            for (int i = 0; i < 10; i++) {
                File randomCardFile = cardImages.get(random.nextInt(cardImages.size()));
                String fileName = randomCardFile.getName();
                pulledCardFileNames[i] = fileName;
                // Create unique ID
                pulledCardUniqueIDs[i] = fileName + "#" + UUID.randomUUID().toString();
            }
        }
        
        // Save the UNIQUE IDs to the inventory file
        saveCardsToInventory(pulledCardUniqueIDs);
        
        // Pass the simple FILENAMES to the results screen for display
        GachaResultsMenu resultsState = new GachaResultsMenu(pulledCardFileNames);
        
        exit(resultsState);
        resultsState.enter();
        resultsState.update();
    }
    
    /**
     * NEW: Appends an array of card filenames to the player's inventory file.
     */
    private void saveCardsToInventory(String[] uniqueCardIDs) {
        // use true flag to append to file
        try (FileWriter fw = new FileWriter("player_inventory.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            for (String uniqueID : uniqueCardIDs) {
                bw.write(uniqueID);
                bw.newLine(); // Write each card on a new line
            }
            System.out.println("Saved 10 cards to player_inventory.txt");
            
        } catch (IOException e) {
            System.err.println("Error writing to inventory file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateCurrencyDisplay() {
        currencyLabel.setText(formatCurrency(playerCurrency) + " $");
        currencyLabel.repaint();
    }
    
    private String formatCurrency(int amount) {
        return String.format("%,d", amount);
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from StoreMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}
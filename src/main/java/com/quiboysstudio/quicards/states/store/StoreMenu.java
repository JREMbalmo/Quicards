package com.quiboysstudio.quicards.states.store;

//SYNC STORE MENU TO DATABASE

import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.states.store.GachaResultsMenu;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class StoreMenu extends State{
    //variables
    private boolean running = false;
    private boolean initialized = false;
    private int playerCurrency = User.getMoney(); 
    
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
                handlePurchase(packName); 
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
    
    private void handlePurchase(String packName) {
        System.out.println("Purchasing: " + packName);
        
        //variables
        int requestID = 0;
        
        //objects
        TimerTask task = new TimerTask(){
            @Override
            public void run() {}
        };
        Timer timer = new Timer();
        
        try {
            //request to gacha from server
            Server.statement.executeUpdate(
                String.format("INSERT INTO Request(UserID, Password, ActionID) VALUES(%d, '%s', %d);", User.getUserID(), User.getPassword(), 1)
            );
            
            //get request id
            Server.result = Server.statement.executeQuery(
                    String.format("""
                    SELECT RequestID, Valid 
                    FROM Request 
                    WHERE UserID = %d 
                    ORDER BY RequestID DESC;
                    """, User.getUserID())
            );   
            if (Server.result.next()) {
                requestID = Server.result.getInt("RequestID");
            }
            
            //wait for results
            ArrayList<Integer> gachaResults = new ArrayList<>();
            
            while (true) {
                //delay
                timer.schedule(task, 1000);

                //check request results
                Server.result = Server.statement.executeQuery(
                        String.format("""
                        SELECT *
                        FROM Result
                        WHERE RequestID = %d;
                        """, requestID)
                );
                
                //check if results are valid
                if (Server.result.next()) {
                    //abort if invalid/illegal request
                    if (Server.result.getInt("Valid") == 0) {
                        System.out.println("Request Denied");
                        return;
                    }
                    
                    //store cards obtained from gacha
                    do {
                        gachaResults.add(Server.result.getInt("NumResult"));
                    } while (Server.result.next());
                    System.out.println("obtained result cards");
                    
                    openCardPack(gachaResults);
                    
                } else {
                    System.out.println("Waiting for results");
                }
            }
        
        } catch (Exception e) {
            System.out.println("failed to purchase pack: " + e);
        }
            
        updateCurrencyDisplay();
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
    
    private void openCardPack(ArrayList<Integer> pulledCardIDs) {
        System.out.println("Opening card pack with provided card IDs...");

        String[] pulledCardFileNames = new String[pulledCardIDs.size()];
        String[] pulledCardUniqueIDs = new String[pulledCardIDs.size()];

        try {

            for (int i = 0; i < pulledCardIDs.size(); i++) {

                int cardID = pulledCardIDs.get(i);

                // ✅ 1. Query MySQL to get the card name
                String cardQuery = "SELECT Name FROM Cards WHERE CardID = " + cardID;
                Server.result = Server.statement.executeQuery(cardQuery);

                if (!Server.result.next()) {
                    System.err.println("Card not found for ID: " + cardID);
                    pulledCardFileNames[i] = "Error - Missing Card.png";
                    pulledCardUniqueIDs[i] = "Error - Missing Card.png";
                    continue;
                }

                String cardName = Server.result.getString("Name");

                // ✅ 2. Build the PNG filename (new format)
                String fileName = cardName + ".png";
                pulledCardFileNames[i] = fileName;

                // ✅ 3. The unique ID is now simply the cardID
                // (server/database controls uniqueness)
                pulledCardUniqueIDs[i] = cardID + "";

                System.out.println("Pulled card: " + fileName + " (CardID " + cardID + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // ✅ 4. Save pulled cards (IDs only)
        saveCardsToInventory(pulledCardUniqueIDs);

        // ✅ 5. Display results
        GachaResultsMenu resultsState = new GachaResultsMenu(pulledCardFileNames);
        exit(resultsState);
        resultsState.enter();
        resultsState.update();
    }
    
    /**
     * NEW: Appends an array of card filenames to the player's inventory file.
     */
    private void saveCardsToInventory(String[] uniqueCardIDs) {

        int userID = User.getUserID();  // ✅ get logged-in user ID

        try {

            for (String cardIDString : uniqueCardIDs) {

                int cardID = Integer.parseInt(cardIDString);

                // ✅ Insert into OwnedCards table
                String sql = "INSERT INTO OwnedCards (UserID, CardID) VALUES (" 
                             + userID + ", " + cardID + ")";

                Server.statement.executeUpdate(sql);
            }

            System.out.println("Saved " + uniqueCardIDs.length + " cards to OwnedCards table (UserID = " + userID + ")");

        } catch (Exception e) {
            System.err.println("Error saving cards to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateCurrencyDisplay() {
        User.updateMoney();
        playerCurrency = User.getMoney();
        currencyLabel.setText("$ " + playerCurrency);
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
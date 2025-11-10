package com.quiboysstudio.quicards.states.matchmaking;

import com.quiboysstudio.quicards.Composites.*; // Import the new core classes
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.proxies.CardImageProxy;
import com.quiboysstudio.quicards.server.Server;
// --- ADDED IMPORT ---
import com.quiboysstudio.quicards.server.handlers.GameHandler;
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.states.matchmaking.utils.CardAnimationSystem;
import com.quiboysstudio.quicards.states.matchmaking.utils.CardSlot;
import com.quiboysstudio.quicards.states.matchmaking.utils.HandPanel;
import com.quiboysstudio.quicards.states.matchmaking.utils.PlayerArea;
import com.quiboysstudio.quicards.strategies.ICardEffectStrategy;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.border.EmptyBorder;

/**
 * Refactored from Room.java (JFrame) to RoomState.java (State).
 * This class is now the CLIENT-SIDE renderer for the game board.
 * It polls the database for the game state and sends user actions as Requests.
 */
public class Room extends State {


    // State management
    private boolean running = false;
    private boolean initialized = false;
    
    // Game components
    private PlayerArea topPlayerArea;
    private PlayerArea bottomPlayerArea;
    private HandPanel topHandPanel;
    private HandPanel bottomHandPanel;
    private CardAnimationSystem animationSystem;
    private JPanel animationLayer;
    private JButton endTurnButton; // Added
    private JPanel mainPanel; // <-- ADDED THIS LINE

    // Game State Data
    private static int currentRoomID = -1;
    private int playerUserID;
    private int opponentUserID;
    private int currentTurn = -1;
    
    // Composites for holding game objects
    private CardComposite playerDeck;
    private CardComposite playerHand;
    private CardComposite opponentDeck;
    private CardComposite opponentHand;
    
    private Timer gameLoopTimer; // Polls the database for updates

    /**
     * Set the RoomID before entering this state.
     */
    public static void setRoomID(int roomID) {
        currentRoomID = roomID;
    }

    @Override
    public void enter() {
        if (currentRoomID == -1) {
            System.err.println("RoomState: No RoomID set. Returning to main menu.");
            JOptionPane.showMessageDialog(frame, "Error: No RoomID set.", "Room Error", JOptionPane.ERROR_MESSAGE);
            exit(mainMenu); // Go to main menu as a safety fallback
            return;
        }
        
        // Initialize game data
        this.playerUserID = User.getUserID();
        this.opponentUserID = getOpponentUserIDFromDB(currentRoomID, playerUserID);

        // Initialize UI components
        init();
        
        // Load initial deck and animate card draw
        loadDecksFromDB();
        SwingUtilities.invokeLater(() -> {
            Timer delayTimer = new Timer(500, e -> {
                ((Timer)e.getSource()).stop();
                animateInitialCardDraw();
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        });

        // Start the game loop poller
        if (gameLoopTimer != null) {
            gameLoopTimer.start();
        }
    }

    @Override
    public void update() {
        showMenu(); // This state's update is driven by the gameLoopTimer
    }
    
    private void showMenu() {
        if (running) return;
        running = true;
        
        System.out.println("Showing Room State (Game Board)");
        
        // We don't add backgroundPanel here, as the Room's UI is custom
        
        cardLayout.show(cardPanel, "Room State");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;

        // --- Create Main Panel (replaces JFrame content pane) ---
        mainPanel = new JPanel(new BorderLayout()) { // <-- REMOVED 'JPanel'
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // TODO: Paint background image here
                g.setColor(new Color(50, 20, 20)); // Placeholder dark background
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // --- Animation Layer ---
        animationLayer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (animationSystem != null) {
                    animationSystem.paintAnimations((Graphics2D) g);
                }
            }
        };
        animationLayer.setOpaque(false);
        animationLayer.setLayout(new BorderLayout()); // Use layout to hold End Turn button
        
        animationSystem = new CardAnimationSystem(animationLayer);

        // --- Initialize Composites ---
        playerDeck = new CardComposite("PlayerDeck");
        playerHand = new CardComposite("PlayerHand");
        opponentDeck = new CardComposite("OpponentDeck");
        opponentHand = new CardComposite("OpponentHand");

        // --- Create Player Areas ---
        topPlayerArea = new PlayerArea(true);
        bottomPlayerArea = new PlayerArea(false);
        
        // --- Create Hand Panels ---
        // We pass the COMPOSITE (which is empty) to the hand panel
        topHandPanel = new HandPanel(false, new ArrayList<>()); // <-- FIXED
        bottomHandPanel = new HandPanel(true, new ArrayList<>()); // <-- FIXED
        
        // --- Setup Field Slot Click Listeners ---
        setupSlotClickListeners();
        
        // --- End Turn Button ---
        endTurnButton = ComponentFactory.createCustomButton("End Turn", FrameConfig.SATOSHI_BOLD, 150, this::handleEndTurnRequest);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(endTurnButton);
        animationLayer.add(buttonPanel, BorderLayout.EAST); // Add to animation layer to float

        // --- Assemble UI ---
        JPanel playArea = new JPanel(new BorderLayout());
        playArea.setOpaque(false);
        playArea.add(animationLayer, BorderLayout.CENTER); // Add animation layer to center

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(topHandPanel, BorderLayout.NORTH);
        topContainer.add(topPlayerArea, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);
        bottomContainer.add(bottomPlayerArea, BorderLayout.CENTER);
        bottomContainer.add(bottomHandPanel, BorderLayout.SOUTH);

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(playArea, BorderLayout.CENTER);
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);
        
        // --- Game Loop Timer (Polls DB) ---
        gameLoopTimer = new Timer(2000, e -> pollDatabaseForUpdates()); // Poll every 2 seconds
        gameLoopTimer.setRepeats(true);

        // --- Add to CardPanel ---
        cardPanel.add("Room State", mainPanel); // Add our main panel to the app's cardPanel
        
        initialized = true;
    }
    
    /**
     * Queries DB to build the Deck composites for both players.
     */
    private void loadDecksFromDB() {
        playerDeck = loadDeckForUser(playerUserID);
        opponentDeck = loadDeckForUser(opponentUserID);

        playerDeck.shuffle();
        opponentDeck.shuffle();

        // Update UI deck counts
        bottomPlayerArea.updateDeckCount(playerDeck.getCardCount());
        topPlayerArea.updateDeckCount(opponentDeck.getCardCount());
    }

    /**
     * DB Helper: Loads a single user's deck.
     */
    private CardComposite loadDeckForUser(int userID) {
        CardComposite deck = new CardComposite("Deck");
        
        // 1. Find the DeckID this user has in this room
        String sqlDeckID = "SELECT DeckID FROM PlayersInRoom WHERE RoomID = ? AND UserID = ?";
        int deckID = -1;
        
        try (PreparedStatement ps = Server.connection.prepareStatement(sqlDeckID)) {
            ps.setInt(1, currentRoomID);
            ps.setInt(2, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) deckID = rs.getInt("DeckID");
            }
            
            if (deckID == -1) throw new SQLException("Could not find DeckID for user " + userID);

            // 2. Load all cards in that deck
            String sqlCards = "SELECT " +
                              "T3.CardID, T2.OwnershipID, T3.Name, " +
                              "T4.Attack, T4.Health, T4.StrategyID " +
                              "FROM DeckContents T1 " +
                              "JOIN OwnedCards T2 ON T1.OwnershipID = T2.OwnershipID " +
                              "JOIN Cards T3 ON T2.CardID = T3.CardID " +
                              "JOIN CardStats T4 ON T3.CardID = T4.CardID " +
                              "WHERE T1.DeckID = ?";

            try (PreparedStatement psCards = Server.connection.prepareStatement(sqlCards)) {
                psCards.setInt(1, deckID);
                try (ResultSet rsCards = psCards.executeQuery()) {
                    while (rsCards.next()) {
                        // Use the Strategy Map to get the correct strategy object
                        int strategyID = rsCards.getInt("StrategyID");
                        // --- MODIFIED ---
                        // This call is now valid because we added the public static method
                        ICardEffectStrategy strategy = GameHandler.getStrategy(strategyID);
                        
                        GameCard card = new GameCard(
                            rsCards.getInt("CardID"),
                            rsCards.getInt("OwnershipID"),
                            rsCards.getString("Name"),
                            rsCards.getInt("Attack"),
                            rsCards.getInt("Health"),
                            strategy
                        );
                        deck.add(card);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Fatal Error: Could not load deck for user " + userID, "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return deck;
    }
    
    /**
     * Polls the database to get the latest game state.
     */
    private void pollDatabaseForUpdates() {
        try {
            // 1. Check current turn
            String sqlTurn = "SELECT Turn FROM BoardTurn WHERE RoomID = ?";
            try (PreparedStatement ps = Server.connection.prepareStatement(sqlTurn)) {
                ps.setInt(1, currentRoomID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int newTurn = rs.getInt("Turn");
                        if (currentTurn == -1) { // First poll
                            currentTurn = newTurn;
                        } else if (newTurn > currentTurn) {
                            // TURN HAS CHANGED!
                            System.out.println("RoomState: Detected Turn change to " + newTurn);
                            currentTurn = newTurn;
                            
                            // Redraw the entire board state
                            redrawBoardState();
                            
                            // Check whose turn it is
                            // TODO: Add logic to determine who is P1/P2
                            // For now, assume player turn if even, opponent if odd
                            boolean isPlayerTurn = (newTurn % 2 == 0); 
                            endTurnButton.setEnabled(isPlayerTurn);
                            
                            if (isPlayerTurn) {
                                // TODO: Player draws a card
                                // drawCardFromDeck(playerDeck, playerHand, bottomPlayerArea);
                            } else {
                                // TODO: Opponent draws a card
                                // drawCardFromDeck(opponentDeck, opponentHand, topPlayerArea);
                            }
                        }
                    }
                }
            }
            
            // 2. TODO: Update card health on board
            
            // 3. TODO: Update player health
            
        } catch (SQLException e) {
            System.err.println("GameLoop Error: " + e.getMessage());
            gameLoopTimer.stop(); // Stop loop on error
        }
    }
    
    /**
     * Fetches the entire board state from DB and updates the UI.
     */
    private void redrawBoardState() {
        // TODO:
        // 1. Clear all slots: bottomPlayerArea.getCardSlots().values().forEach(slot -> slot.setDeployedCard(null));
        // 2. Clear all slots: topPlayerArea.getCardSlots().values().forEach(slot -> slot.setDeployedCard(null));
        // 3. Get player board: Map<String, Integer> playerBoard = getBoardStateFromDB(currentRoomID, playerUserID);
        // 4. Get opponent board: Map<String, Integer> oppBoard = getBoardStateFromDB(currentRoomID, opponentUserID);
        // 5. For each entry in playerBoard, load the GameCard (loadGameCardFromDB(stateID))
        // 6. Set the card in the correct slot: bottomPlayerArea.getCardSlots().get(slotName).setDeployedCard(card.getName());
        // 7. Repeat for opponent board.
        System.out.println("RoomState: Redrawing board state...");
    }
    
    /**
     * Sets up click listeners for the player's 3 field slots.
     */
    private void setupSlotClickListeners() {
        // --- MODIFIED ---
        // Create a mapping from UI slot name to DB slot name
        Map<String, String> slotNameMapping = new HashMap<>();
        slotNameMapping.put("Field 1", "LeftCard");
        slotNameMapping.put("Field 2", "MidCard");
        slotNameMapping.put("Field 3", "RightCard");

        for (Map.Entry<String, CardSlot> entry : bottomPlayerArea.getCardSlots().entrySet()) {
            String uiSlotName = entry.getKey(); // "Field 1", "Field 2", etc.
            CardSlot slot = entry.getValue();

            if (!slot.isFieldSlot()) continue;
            
            slot.setOnSlotClicked(clickedSlot -> {
                if (clickedSlot.hasCard()) {
                    System.out.println("Slot already occupied!");
                    return;
                }
                
                String selectedCardIdentifier = bottomHandPanel.getSelectedCard();
                if (selectedCardIdentifier == null) {
                    System.out.println("No card selected!");
                    return;
                }
                
                // Get the GameCard object from the hand composite
                GameCard cardToPlay = (GameCard) playerHand.getCardByOwnershipID(Integer.parseInt(selectedCardIdentifier)); // This HACK is correct based on animateInitialCardDraw
                
                // --- MODIFIED ---
                // Get the correct DB slot name (e.g., "LeftCard")
                String dbSlotName = slotNameMapping.get(uiSlotName);
                
                if (cardToPlay != null && dbSlotName != null) {
                    handlePlayCardRequest(cardToPlay, dbSlotName); // Send "LeftCard" etc.
                    // Optimistic UI update
                    clickedSlot.setDeployedCard(cardToPlay.getName());
                    bottomHandPanel.removeCard(selectedCardIdentifier);
    }
            }
            );
                    }
    }

    /**
     * Sends a Request to the server to play a card.
     */
    private void handlePlayCardRequest(GameCard card, String slotName) { // slotName is now "LeftCard", etc.
        System.out.println("RoomState: Requesting to play card " + card.getOwnershipID() + " in " + slotName);
        try {
            String sql = "INSERT INTO Request (UserID, Password, ActionID, Var1, Var2) VALUES (?, ?, 3, ?, ?)";
            try (PreparedStatement ps = Server.connection.prepareStatement(sql)) {
                ps.setInt(1, playerUserID);
                ps.setString(2, User.getPassword()); // Password required by table
                ps.setString(3, String.valueOf(card.getOwnershipID()));
                ps.setString(4, slotName);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error playing card: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Sends a Request to the server to end the turn.
     */
    private void handleEndTurnRequest() {
        System.out.println("RoomState: Requesting to end turn.");
        endTurnButton.setEnabled(false); // Disable until server confirms next turn
        
        try {
            String sql = "INSERT INTO Request (UserID, Password, ActionID) VALUES (?, ?, 2)";
            try (PreparedStatement ps = Server.connection.prepareStatement(sql)) {
                ps.setInt(1, playerUserID);
                ps.setString(2, User.getPassword());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error ending turn: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            endTurnButton.setEnabled(true); // Re-enable on error
        }
    }

    // --- Animation & DB Helpers (Adapted from old Room.java) ---

    private void animateInitialCardDraw() {
        // ... (This logic remains largely the same as your Room.java)
        // ... (BUT it must pull GameCard objects from playerDeck/opponentDeck composites)
        // ... (and add them to playerHand/opponentHand composites)
        
        System.out.println("RoomState: Animating initial draw...");
        // Example for player:
        for (int i = 0; i < 5; i++) {
            GameCard card = playerDeck.drawCard();
            if (card == null) break;
            
            // HACK: Use OwnershipID as the "name" for HandPanel to track
            String cardIdentifier = String.valueOf(card.getOwnershipID()); 
            
            // TODO: Calculate endX, endY, delay
            int endX = 600 + i * 40;
            int endY = 800;
            int delay = i * 150;
            
            animationSystem.animateCardDraw(
                card.getName() + ".png", // This needs to be the FILENAME
                0, 0, // TODO: Get deck position
                endX, endY,
                delay,
                () -> {
                    playerHand.add(card);
                    // HACK: Pass list of OwnershipIDs as strings
                    List<String> handIDs = new ArrayList<>();
                    for(GameCard c : playerHand.getCards()) {
                        handIDs.add(String.valueOf(c.getOwnershipID()));
                    }
                    bottomHandPanel.setCards(handIDs);
                    bottomPlayerArea.updateDeckCount(playerDeck.getCardCount());
                }
            );
        }
        // TODO: Repeat for opponent
    }

    private int getOpponentUserIDFromDB(int roomID, int playerUserID) {
        try {
            String sql = "SELECT UserID FROM PlayersInRoom WHERE RoomID = ? AND UserID != ?";
            try (PreparedStatement ps = Server.connection.prepareStatement(sql)) {
                ps.setInt(1, roomID);
                ps.setInt(2, playerUserID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("UserID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from RoomState");
        
        if (gameLoopTimer != null) {
            gameLoopTimer.stop();
        }
        
        // Remove the game panel from the card layout
        if(initialized) {
            cardPanel.remove(mainPanel.getParent()); // Assumes mainPanel is in firstLayerPanel
        }
        
        running = false;
        initialized = false; // Allow re-init
        currentRoomID = -1; // Reset room
        
        previousState = currentState;
        currentState = nextState;
    }
}
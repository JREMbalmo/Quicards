package com.quiboysstudio.quicards.states.matchmaking;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;

// --- IMPORTS ---
import com.quiboysstudio.quicards.account.User; // For getting UserID
import com.quiboysstudio.quicards.server.Server; // For DB connection
import java.awt.BorderLayout;
import java.awt.Component; // For alignment
import java.awt.Dimension;
import java.awt.FlowLayout;
// import java.io.File; // No longer needed
import java.sql.PreparedStatement; // For safe queries
import java.sql.ResultSet; // For getting results
import java.sql.SQLException; // For errors
// import java.sql.Statement; // No longer needed
import java.util.ArrayList; // To store card names
import java.util.HashMap; // To map DeckName -> DeckID
import java.util.List; // To store card names
import java.util.Map; // To map DeckName -> DeckID
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane; // For error messages
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities; // For thread-safe UI updates
import javax.swing.border.EmptyBorder;
// --- END IMPORTS ---

/**
 * This state allows the player to select one of their saved decks
 * before starting a match.
 */
public class WaitingRoom extends State {
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JLayeredPane layeredPane;
    private JScrollPane scrollPane;
    private JPanel mainPanel;
    private JPanel firstLayerPanel;
    private JPanel topBarPanel;
    private JPanel navigationPanel;
    private JPanel contentPanel;
    private JPanel sidePanel;
    private JButton backButton;
    private JButton readyButton; // Made a class member to disable it
    
    // Stores the selected deck name and ID
    private int selectedDeckID = -1;
    private String selectedDeckName = null;
    
    // Stores the current room
    private static int currentRoomID = -1;

    /**
     * IMPORTANT: This method must be called from the previous state
     * (e.g., JoinRoomMenu) before transitioning here.
     * @param roomID The ID of the room the player has joined.
     */
    public static void setRoomID(int roomID) {
        WaitingRoom.currentRoomID = roomID;
    }

    @Override
    public void enter() {
        init();
        
        // Safety check
        if (currentRoomID == -1) {
            JOptionPane.showMessageDialog(frame, "Error: No RoomID set. Returning to previous menu.", "Room Error", JOptionPane.ERROR_MESSAGE);
            // We must exit to the previous state, not mainMenu
            if (previousState != null) {
                exit(previousState);
            } else {
                exit(mainMenu); // Fallback
            }
        }
    }

    @Override
    public void update() {
        showMenu();
    }
    
    private void showMenu() {
        if (running) return;
        running = true;
        
        System.out.println("Showing Waiting Room");
        
        layeredPane.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        // Reset selections
        selectedDeckID = -1;
        selectedDeckName = null;
        if (readyButton != null) readyButton.setEnabled(false);
        if (backButton != null) backButton.setEnabled(true);
        
        // Load decks every time to get a fresh list
        populateDecks();
        updateSidePanel(null); // Start with no deck selected
        
        cardLayout.show(cardPanel, "Waiting Room"); // Renamed card
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from WaitingRoom state");
        
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
        
        // --- MODIFIED BACK BUTTON ---
        // Changed from createStateChangerButton to createCustomButton
        backButton = ComponentFactory.createCustomButton("Back", FrameConfig.SATOSHI_BOLD, 150, () -> {
            handleLeaveRoom();
        });
        backButton.setVerticalAlignment(JButton.CENTER);
        backButton.setHorizontalAlignment(JButton.CENTER);
        
        navigationPanel.add(backButton);
        
        topBarPanel.add(navigationPanel, BorderLayout.WEST);
        
        mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        
        sidePanel = new JPanel();
        sidePanel.setOpaque(false);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(FrameUtil.scale(frame, 320), frame.getHeight()));
        sidePanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 30)));
        
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        // Set layout to stack rows vertically
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); 
        contentPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50)));
        
        scrollPane = new CustomScrollPane(contentPanel);
        
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        firstLayerPanel.add(topBarPanel, BorderLayout.NORTH);
        firstLayerPanel.add(mainPanel, BorderLayout.CENTER);
        
        layeredPane.add(firstLayerPanel, Integer.valueOf(1));
        
        cardPanel.add("Waiting Room", layeredPane); // Renamed card
        
        initialized = true;
        
        System.out.println("Entering WaitingRoom state");
    }
    
    /**
     * Loads deck names from the database for the current user.
     * @return A Map where Key is the Deck Name (String) and Value is the DeckID (Integer).
     */
    private Map<String, Integer> loadPlayerDecks() {
        Map<String, Integer> deckMap = new HashMap<>();
        int userID = User.getUserID();
        
        String query = "SELECT DeckID, Name FROM Decks WHERE UserID = " + userID;
        
        // Using Statement here as UserID is internal and less of an injection risk
        // Swapping to PreparedStatement is safer if you have time.
        try {
            Server.result = Server.statement.executeQuery(query);
            
            while (Server.result.next()) {
                deckMap.put(Server.result.getString("Name"), Server.result.getInt("DeckID"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading decks: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return deckMap;
    }
    
    /**
     * Populates the contentPanel with 5-deck rows from the database.
     */
    private void populateDecks() {
        contentPanel.removeAll(); // Clear old decks
        Map<String, Integer> deckMap = loadPlayerDecks();
        
        if (deckMap.isEmpty()) {
            contentPanel.add(ComponentFactory.createTextLabel("No decks found. Go to Inventory to create one.", FrameConfig.SATOSHI_BOLD));
            selectedDeckID = -1;
            selectedDeckName = null;
        } else {
            // Gacha-style row population
            JPanel currentRowPanel = createRowPanel();
            contentPanel.add(currentRowPanel);

            int i = 0;
            for (Map.Entry<String, Integer> entry : deckMap.entrySet()) {
                String deckName = entry.getKey();
                int deckID = entry.getValue();

                if (i > 0 && i % 5 == 0) {
                    currentRowPanel = createRowPanel();
                    contentPanel.add(currentRowPanel);
                }
                JPanel deckItem = createDeckItem(deckName, deckID);
                currentRowPanel.add(deckItem);
                
                // Set first deck as selected by default
                if (selectedDeckID == -1) {
                    selectedDeckID = deckID;
                    selectedDeckName = deckName;
                }
                i++;
            }
        }
    }
    
    /**
     * Helper method to create an invisible row panel.
     */
    private JPanel createRowPanel() {
        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 30)));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FrameUtil.scale(frame, 360)));
        return rowPanel;
    }

    /**
     * Creates a placeholder deck item.
     * @param deckName The name of the deck.
     * @param deckID The ID of the deck.
     */
    private JPanel createDeckItem(String deckName, int deckID) {
        JPanel deck = new JPanel();
        deck.setOpaque(false);
        deck.setLayout(new BoxLayout(deck, BoxLayout.Y_AXIS));
        deck.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 350)));
        deck.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Deck image placeholder
        JPanel imagePanel = ComponentFactory.createRoundedPanel(FrameUtil.scale(frame, 230), FrameUtil.scale(frame, 520), FrameConfig.BLACK);
        // TODO: Load actual deck image here
        
        // Deck name label
        JLabel nameLabel = ComponentFactory.createTextLabel(deckName, FrameConfig.SATOSHI_BOLD);
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        deck.add(imagePanel);
        deck.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
        deck.add(nameLabel);
        
        // Add click listener to update side panel
        deck.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedDeckID = deckID;
                selectedDeckName = deckName;
                updateSidePanel(deckName);
            }
        });
        
        return deck;
    }
    
    /**
     * Updates the side panel to show the selected deck and the "Ready" button.
     */
    private void updateSidePanel(String deckName) {
        sidePanel.removeAll();
        sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 50))));
        
        if (deckName == null) {
            // Show placeholder text if no deck is selected
            JLabel emptyLabel = ComponentFactory.createTextLabel("Select a deck", FrameConfig.SATOSHI_BOLD);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidePanel.add(emptyLabel);
        } else {
            // --- Show Selected Deck Info ---
            
            // Large deck image
            JPanel largeImagePanel = ComponentFactory.createRoundedPanel(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 390), FrameConfig.BLACK);
            largeImagePanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
            // TODO: Load actual deck image here
            
            // Deck name
            JLabel nameLabel = ComponentFactory.createTextLabel(deckName, FrameConfig.SATOSHI_BOLD);
            nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            
            sidePanel.add(largeImagePanel);
            sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 20))));
            sidePanel.add(nameLabel);
        }
        
        sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 50))));
        
        // --- Ready Button ---
        readyButton = ComponentFactory.createCustomButton("Ready", FrameConfig.SATOSHI_BOLD, 200, () -> {
            handleReadyUp();
        });
        readyButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        // Enable the button only if a deck is selected
        readyButton.setEnabled(deckName != null);
        
        sidePanel.add(readyButton);
        sidePanel.add(Box.createVerticalGlue());
        
        sidePanel.revalidate();
        sidePanel.repaint();
    }
    
    /**
     * NEW: Sets the player's status to 'Ready' in the database.
     */
    private void handleReadyUp() {
        if (selectedDeckID == -1) {
            JOptionPane.showMessageDialog(frame, "You must select a deck first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentRoomID == -1) {
            JOptionPane.showMessageDialog(frame, "Error: Not in a valid room.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable buttons to prevent double-clicks
        readyButton.setEnabled(false);
        backButton.setEnabled(false);
        readyButton.setText("Waiting...");

        // Run DB update on a new thread
        new Thread(() -> {
            int userID = User.getUserID();
            String query = "UPDATE PlayersInRoom SET Status = 1, DeckID = ? WHERE UserID = ? AND RoomID = ?";
            
            try (PreparedStatement ps = Server.connection.prepareStatement(query)) {
                ps.setInt(1, selectedDeckID);
                ps.setInt(2, userID);
                ps.setInt(3, currentRoomID);
                
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("User " + userID + " is ready in room " + currentRoomID);
                    // Player is now locked in.
                    // The server will handle what happens next.
                    // We just disable the UI.
                    SwingUtilities.invokeLater(() -> {
                        readyButton.setText("Ready!");
                    });
                } else {
                    throw new SQLException("Player not found in room, or update failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Re-enable buttons if something went wrong
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "Error readying up: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    readyButton.setEnabled(true);
                    backButton.setEnabled(true);
                    readyButton.setText("Ready");
                });
            }
        }).start();
    }

    /**
     * NEW: Removes the player from the current room in the database and goes back.
     */
    private void handleLeaveRoom() {
        if (currentRoomID == -1) {
            JOptionPane.showMessageDialog(frame, "Error: Not in a valid room.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Run DB delete on a new thread
        new Thread(() -> {
            int userID = User.getUserID();
            String query = "DELETE FROM PlayersInRoom WHERE UserID = ? AND RoomID = ?";
            
            try (PreparedStatement ps = Server.connection.prepareStatement(query)) {
                ps.setInt(1, userID);
                ps.setInt(2, currentRoomID);
                
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("User " + userID + " left room " + currentRoomID);
                } else {
                    // This can happen if the user was already kicked or left
                    System.out.println("User " + userID + " was already not in room " + currentRoomID);
                }
                
                // Always transition back
                SwingUtilities.invokeLater(() -> {
                    // Reset room ID for next time
                    currentRoomID = -1;
                    exit(previousState);
                });
                
            } catch (SQLException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "Error leaving room: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from WaitingRoom");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}
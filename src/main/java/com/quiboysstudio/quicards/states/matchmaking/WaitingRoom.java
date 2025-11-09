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
import java.io.File; // For file handling
import java.io.IOException; // For error handling
import java.sql.PreparedStatement; // For safe queries
import java.sql.ResultSet; // For getting results
import java.sql.SQLException; // For errors
import java.sql.Statement; // For getting generated keys
import java.util.ArrayList; // To store card names
import java.util.List; // To store card names
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane; // For error messages
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
    
    // Stores the selected deck name (e.g., "My First Deck")
    private String selectedDeck = null;

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
        
        System.out.println("Showing Create Room menu");
        
        layeredPane.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        // Load decks every time to get a fresh list
        populateDecks();
        updateSidePanel(selectedDeck);
        
        cardLayout.show(cardPanel, "Create Room Menu");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from CreateRoomMenu state");
        
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
        
        navigationPanel.add(backButton);
        
        // Removed the categoryPanel
        
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
        contentPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50)));
        
        scrollPane = new CustomScrollPane(contentPanel);
        
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        firstLayerPanel.add(topBarPanel, BorderLayout.NORTH);
        firstLayerPanel.add(mainPanel, BorderLayout.CENTER);
        
        layeredPane.add(firstLayerPanel, Integer.valueOf(1));
        
        cardPanel.add("Create Room Menu", layeredPane);
        
        initialized = true;
        
        System.out.println("Entering CreateRoomMenu state");
    }
    
    /**
     * Loads deck names from the /decks/ folder.
     */
    private List<String> loadPlayerDecks() {
        List<String> deckNames = new ArrayList<>();
        File deckDir = new File("decks");

        // Ensure the directory exists
        if (!deckDir.exists()) {
            deckDir.mkdir();
        }
        
        File[] files = deckDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        
        if (files != null) {
            for (File file : files) {
                deckNames.add(file.getName().replace(".txt", ""));
            }
        }
        return deckNames;
    }
    
    /**
     * Populates the contentPanel with 5-deck rows.
     */
    private void populateDecks() {
        contentPanel.removeAll(); // Clear old decks
        List<String> deckNames = loadPlayerDecks();
        
        if (deckNames.isEmpty()) {
            contentPanel.add(ComponentFactory.createTextLabel("No decks found. Go to Inventory to create one.", FrameConfig.SATOSHI_BOLD));
            selectedDeck = null; // No deck to select
        } else {
            // Gacha-style row population
            JPanel currentRowPanel = createRowPanel();
            contentPanel.add(currentRowPanel);

            for (int i = 0; i < deckNames.size(); i++) {
                if (i > 0 && i % 5 == 0) {
                    currentRowPanel = createRowPanel();
                    contentPanel.add(currentRowPanel);
                }
                JPanel deckItem = createDeckItem(deckNames.get(i));
                currentRowPanel.add(deckItem);
            }
            
            // Set first deck as selected by default
            if (selectedDeck == null) {
                selectedDeck = deckNames.get(0);
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
     */
    private JPanel createDeckItem(String deckName) {
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
                selectedDeck = deckName;
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
        sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 300))));
        
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
        JButton readyButton = ComponentFactory.createCustomButton("Ready", FrameConfig.SATOSHI_BOLD, 200, () -> {
            // This is where you would transition to the lobby or start the game
            System.out.println("Player is Ready with deck: " + selectedDeck);
            createRoom(); // Call the new database method
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
     * NEW: Creates a new room in the Rooms table.
     */
    private void createRoom() {
        if (selectedDeck == null) {
            JOptionPane.showMessageDialog(frame, "You must select a deck first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- MODIFICATION ---
        // Get UserID from the static User class instead of querying the DB
        int userID = User.getUserID();
        if (userID == -1) {
            JOptionPane.showMessageDialog(frame, "Error: Could not find user. Please log in again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // --- END MODIFICATION ---
        
        int deckID = getDeckID(selectedDeck, userID);
        if (deckID == -1) {
            JOptionPane.showMessageDialog(frame, "Error: Could not find selected deck in database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // This query inserts a new room, setting User2 = User1 as a placeholder
        // to satisfy the NOT NULL constraint.
        String query = "INSERT INTO Rooms (User1, Deck1, User2) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = Server.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userID);
            ps.setInt(2, deckID);
            ps.setInt(3, userID); // User2 placeholder
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int roomID = rs.getInt(1);
                        System.out.println("Successfully created room " + roomID);
                        // TODO: Transition to the lobby/waiting state
                        // Example: exit(new LobbyState(roomID));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error creating room: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getDeckID(String deckName, int userID) {
        String query = "SELECT DeckID FROM Decks WHERE Name = ? AND UserID = ?";
        try (PreparedStatement ps = Server.connection.prepareStatement(query)) {
            ps.setString(1, deckName);
            ps.setInt(2, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("DeckID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found or error
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from CreateRoomMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}
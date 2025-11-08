package com.quiboysstudio.quicards.states.postlaunchmenu;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.proxies.CardImageProxy;
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.account.User; // NEW IMPORT
import com.quiboysstudio.quicards.server.Server; // NEW IMPORT
import java.awt.BorderLayout;
import java.awt.Component; // For alignment
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image; // For scaling
import java.io.BufferedReader; // For reading file
import java.io.File; // For file handling
import java.io.FileReader; // For reading file
import java.io.IOException; // For error handling
import java.sql.PreparedStatement; // NEW IMPORT
import java.sql.ResultSet; // NEW IMPORT
import java.sql.SQLException; // NEW IMPORT
import java.util.ArrayList; // To store card names
import java.util.List; // To store card names
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon; // For proxy
import javax.swing.ImageIcon; // For scaling
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane; // For Create Deck prompt
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
// --- END IMPORTS ---

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * InventoryMenu (DB-driven)
 * - Option A: Uses DB (OwnedCards + Cards + PackContents + Packs) for card inventory
 * - Deck creation/deletion updates DB: Decks and DeckContents
 *
 * Note: This class follows the structure of your legacy class but replaces the
 * text-file-based inventory with DB queries and uses Pack.Name as the folder name.
 */
public class InventoryMenu extends State {
    // variables
    private boolean running = false;
    private boolean initialized = false;

    // objects
    private JLayeredPane layeredPane;
    private JScrollPane scrollPane;
    private JPanel mainPanel;
    private JPanel firstLayerPanel;
    private JPanel topBarPanel;
    private JPanel navigationPanel;
    private JPanel categoryPanel;
    private JPanel contentPanel;
    private JPanel sidePanel;
    private JButton backButton;
    private JButton cardsTabButton;
    private JButton decksTabButton;

    // Current selected tab
    private String currentTab = "Cards";

    // Selections (separate for clarity)
    private CardEntry selectedCard = null;
    private String selectedDeck = null;

    // DB-driven owned cards
    private List<CardEntry> ownedCards = new ArrayList<>();

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

        System.out.println("Showing Inventory menu");

        layeredPane.add(FrameConfig.backgroundPanel, Integer.valueOf(0));

        // Reload DB-backed content every time menu is shown to refresh inventory
        loadCategoryContent(currentTab);

        cardLayout.show(cardPanel, "Inventory Menu");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;

        System.out.println("Initializing elements from InventoryMenu state");

        // All component initializations are the same as your file-based version
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

        categoryPanel = new JPanel();
        categoryPanel.setOpaque(false);
        categoryPanel.setLayout(new FlowLayout(FlowLayout.CENTER, FrameUtil.scale(frame, 10), FrameUtil.scale(frame, 20)));

        cardsTabButton = createCategoryButton("Cards");
        decksTabButton = createCategoryButton("Decks");

        categoryPanel.add(cardsTabButton);
        categoryPanel.add(decksTabButton);

        topBarPanel.add(navigationPanel, BorderLayout.WEST);
        topBarPanel.add(categoryPanel, BorderLayout.CENTER);

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
        // Layout is set dynamically in loadCategoryContent()
        contentPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50)));

        scrollPane = new CustomScrollPane(contentPanel);

        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        firstLayerPanel.add(topBarPanel, BorderLayout.NORTH);
        firstLayerPanel.add(mainPanel, BorderLayout.CENTER);

        layeredPane.add(firstLayerPanel, Integer.valueOf(1));

        cardPanel.add("Inventory Menu", layeredPane);

        initialized = true;

        System.out.println("Entering InventoryMenu state");
    }

    private JButton createCategoryButton(String category) {
        JButton button = ComponentFactory.createCustomButton(category, FrameConfig.SATOSHI_BOLD, 200,
                () -> {
                    currentTab = category;
                    // clear selections
                    selectedCard = null;
                    selectedDeck = null;
                    loadCategoryContent(category);
                });

        return button;
    }

    /**
     * Dynamically changes the layout of contentPanel based on the tab.
     */
    private void loadCategoryContent(String category) {
        contentPanel.removeAll();
        sidePanel.removeAll();

        switch (category) {
            case "Cards":
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                populateCards(); // DB-driven
                break;
            case "Decks":
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                populateDecks(); // DB-driven + legacy file support
                break;
        }

        contentPanel.revalidate();
        contentPanel.repaint();
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    /**
     * DB method: loads owned cards for the current user into ownedCards list.
     * Uses OwnedCards -> Cards -> PackContents -> Packs to determine pack folder name.
     */
    private void loadPlayerInventoryFromDB() {
        ownedCards.clear();

        int userID = getUserID();
        if (userID == -1) return;

        String query =
                "SELECT OC.OwnershipID, OC.CardID, C.Name AS CardName, " +
                " (SELECT P.Name FROM Packs P JOIN PackContents PC ON P.PackID = PC.PackID WHERE PC.CardID = C.CardID LIMIT 1) AS PackName " +
                "FROM OwnedCards OC " +
                "JOIN Cards C ON OC.CardID = C.CardID " +
                "WHERE OC.UserID = ?;";

        try (PreparedStatement ps = Server.connection.prepareStatement(query)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int ownershipID = rs.getInt("OwnershipID");
                    int cardID = rs.getInt("CardID");
                    String cardName = rs.getString("CardName");
                    String packName = rs.getString("PackName");

                    if (packName == null) {
                        // fallback: if no pack found, attempt empty or default folder
                        packName = "";
                    }

                    // Build image path as requested: resources/cards/<PackName>/<CardName>.png
                    String imagePath;
                    if (packName.isEmpty()) {
                        imagePath = "resources/cards/" + cardName + ".png";
                    } else {
                        imagePath = "resources/cards/" + packName + "/" + cardName + ".png";
                    }

                    CardEntry entry = new CardEntry(ownershipID, cardID, cardName, packName, imagePath);
                    ownedCards.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load player inventory from DB: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Populates contentPanel with DB-driven owned cards (5 per row).
     */
    private void populateCards() {
        loadPlayerInventoryFromDB(); // Load/refresh ownedCards

        if (ownedCards.isEmpty()) {
            updateSidePanelForCard(null); // Clear side panel
        } else {
            // Row population (5 items per row)
            JPanel currentRowPanel = createRowPanel();
            contentPanel.add(currentRowPanel);

            for (int i = 0; i < ownedCards.size(); i++) {
                if (i > 0 && i % 5 == 0) {
                    currentRowPanel = createRowPanel();
                    contentPanel.add(currentRowPanel);
                }
                JPanel cardItem = createCardItem(ownedCards.get(i));
                currentRowPanel.add(cardItem);
            }

            // Initially show first card in side panel
            selectedCard = ownedCards.get(0);
            updateSidePanelForCard(selectedCard);
        }
    }

    /**
     * Helper to load deck names from the Decks table (DB-driven) but still keep the 'decks' folder for
     * backward compatibility with the .txt files (we keep both).
     */
    private List<String> loadPlayerDecksFromDB() {
        List<String> deckNames = new ArrayList<>();
        int userID = getUserID();
        if (userID == -1) return deckNames;

        String query = "SELECT Name FROM Decks WHERE UserID = ?;";

        try (PreparedStatement ps = Server.connection.prepareStatement(query)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    deckNames.add(rs.getString("Name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load decks from DB: " + e);
            e.printStackTrace();
        }

        // Also ensure folder presence as before for legacy .txt usage
        File deckDir = new File("decks");
        if (!deckDir.exists()) deckDir.mkdir();

        return deckNames;
    }

    /**
     * Populates contentPanel with decks (5 per row).
     */
    private void populateDecks() {
        List<String> deckNames = loadPlayerDecksFromDB();

        if (deckNames.isEmpty()) {
            updateSidePanelForDeck(null); // Show "Create Deck" button
        } else {
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

            // Initially show first deck in side panel
            selectedDeck = deckNames.get(0);
            updateSidePanelForDeck(selectedDeck);
        }
    }

    /**
     * Helper method to create an invisible row panel.
     */
    private JPanel createRowPanel() {
        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 30)));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FrameUtil.scale(frame, 360))); // card height + gaps
        return rowPanel;
    }

    /**
     * Creates a card item UI from a CardEntry.
     */
    private JPanel createCardItem(CardEntry entry) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 320)));
        card.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        String imagePath = entry.imagePath;
        String cardName = entry.cardName;

        // Card image (using the same CardImageProxy you had)
        int imgWidth = FrameUtil.scale(frame, 180);
        int imgHeight = FrameUtil.scale(frame, 260);
        Icon proxyIcon = new CardImageProxy(imagePath, imgWidth, imgHeight);

        JLabel imageLabel = new JLabel(proxyIcon);
        imageLabel.setPreferredSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setMaximumSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        // Card name label
        JLabel nameLabel = ComponentFactory.createTextLabel(cardName, FrameConfig.SATOSHI_BOLD);
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        card.add(imageLabel);
        card.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
        card.add(nameLabel);

        // Add click listener to update side panel
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedCard = entry;
                updateSidePanelForCard(entry);
            }
        });

        return card;
    }

    /**
     * Side panel: displays selected card's scaled image and name from DB-built imagePath.
     */
    private void updateSidePanelForCard(CardEntry entry) {
        sidePanel.removeAll();

        if (entry == null) {
            JLabel emptyLabel = ComponentFactory.createTextLabel("Select a card", FrameConfig.SATOSHI_BOLD);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidePanel.add(Box.createVerticalGlue());
            sidePanel.add(emptyLabel);
            sidePanel.add(Box.createVerticalGlue());
        } else {
            String imagePath = entry.imagePath;
            String cardName = entry.cardName;

            // --- Large Image ---
            ImageIcon cardIcon = new ImageIcon(imagePath);
            int targetWidth = FrameUtil.scale(frame, 260);
            int originalWidth = Math.max(1, cardIcon.getIconWidth());
            int originalHeight = Math.max(1, cardIcon.getIconHeight());
            int targetHeight = (int) ((double) targetWidth / originalWidth * originalHeight);
            Image scaledImage = cardIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            JLabel largeImageLabel = new JLabel(new ImageIcon(scaledImage));
            largeImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // --- Name Label ---
            JLabel nameLabel = ComponentFactory.createTextLabel(cardName, FrameConfig.SATOSHI_BOLD);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            sidePanel.add(Box.createVerticalGlue());
            sidePanel.add(largeImageLabel);
            sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 20))));
            sidePanel.add(nameLabel);
            sidePanel.add(Box.createVerticalGlue());
        }

        sidePanel.revalidate();
        sidePanel.repaint();
    }

    /**
     * Side panel for decks; includes Create Deck and Delete Deck DB logic.
     */
    private void updateSidePanelForDeck(String deckName) {
        sidePanel.removeAll();
        sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 50))));

        // "Create Deck" button
        JButton createButton = ComponentFactory.createCustomButton("Create Deck", FrameConfig.SATOSHI_BOLD, 200, () -> {
            String newDeckName = JOptionPane.showInputDialog(frame, "Enter deck name:", "Create Deck", JOptionPane.PLAIN_MESSAGE);
            if (newDeckName != null && !newDeckName.trim().isEmpty()) {

                // 1. Create the .txt file (legacy)
                File deckFolder = new File("decks");
                if (!deckFolder.exists()) deckFolder.mkdir();
                File newFile = new File("decks/" + newDeckName + ".txt");
                try {
                    if (newFile.createNewFile()) {
                        System.out.println("Deck file created: " + newDeckName);

                        // 2. Add to database
                        addDeckToDatabase(newDeckName);

                        // 3. Refresh UI
                        loadCategoryContent("Decks");
                    } else {
                        JOptionPane.showMessageDialog(frame, "A deck with this name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        createButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        sidePanel.add(createButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 50))));

        if (deckName != null) {
            // Large deck image (placeholder)
            JPanel largeImagePanel = ComponentFactory.createRoundedPanel(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 390), FrameConfig.BLACK);
            largeImagePanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

            // Deck name
            JLabel nameLabel = ComponentFactory.createTextLabel(deckName, FrameConfig.SATOSHI_BOLD);
            nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

            // Edit button
            JButton editButton = ComponentFactory.createCustomButton("Edit Deck", FrameConfig.SATOSHI_BOLD, 200, () -> {
                System.out.println("Editing deck: " + deckName);
                // Transition to DeckBuilder
                exit(new DeckBuilderMenu(deckName));
                currentState.enter();
                currentState.update();
            });
            editButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

            // Delete button
            JButton deleteButton = ComponentFactory.createCustomButton("Delete Deck", FrameConfig.SATOSHI_BOLD, 200, () -> {
                int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete '" + deckName + "'?", "Delete Deck", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // 1. Delete .txt file (legacy)
                    File deckFile = new File("decks/" + deckName + ".txt");
                    if (deckFile.exists() && deckFile.delete()) {
                        System.out.println("Deleted deck file: " + deckName);
                    } else {
                        // If it doesn't exist, that's fine — continue with DB deletion
                        System.out.println("Deck file not found (or could not be deleted): " + deckName);
                    }

                    // 2. Delete from database (DeckContents then Decks)
                    deleteDeckFromDatabase(deckName);

                    // 3. Refresh UI
                    loadCategoryContent("Decks");
                }
            });
            deleteButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

            sidePanel.add(largeImagePanel);
            sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 20))));
            sidePanel.add(nameLabel);
            sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 50))));
            sidePanel.add(editButton);
            sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 15))));
            sidePanel.add(deleteButton);
        }

        sidePanel.add(Box.createVerticalGlue());

        sidePanel.revalidate();
        sidePanel.repaint();
    }

    /**
     * Parses a user-facing card name from a DB cardName if needed (keeps old behavior compatible).
     */
    private String parseCardName(String cardName) {
        // CardName from DB assumed to be the display name already.
        // Keep this method for compatibility with the rest of UI code.
        try {
            if (cardName == null) return "Unknown Card";
            return cardName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    // --- NEW / UPDATED DATABASE METHODS ---

    /**
     * Gets the UserID from the Users table based on User.getUsername()
     */
    private int getUserID() {
        String username = User.getUsername();
        if (username == null) return -1;

        String q = "SELECT UserID FROM Users WHERE Username = ?";
        try (PreparedStatement ps = Server.connection.prepareStatement(q)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("UserID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found or error
    }

    /**
     * Inserts a new deck into the Decks table (DB).
     */
    private void addDeckToDatabase(String deckName) {
        int userID = getUserID();
        if (userID == -1) {
            System.err.println("Could not create deck: UserID not found.");
            return;
        }

        String insert = "INSERT INTO Decks (UserID, Name) VALUES (?, ?)";
        try (PreparedStatement ps = Server.connection.prepareStatement(insert)) {
            ps.setInt(1, userID);
            ps.setString(2, deckName);
            ps.executeUpdate();
            System.out.println("Added deck to database: " + deckName);
        } catch (SQLException e) {
            System.err.println("Failed to add deck to database: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Deletes a deck and its contents from DeckContents and Decks tables (DB).
     */
    private void deleteDeckFromDatabase(String deckName) {
        int userID = getUserID();
        if (userID == -1) {
            System.err.println("Could not delete deck: UserID not found.");
            return;
        }

        // 1) find DeckID
        String findDeck = "SELECT DeckID FROM Decks WHERE UserID = ? AND Name = ?";
        Integer deckID = null;
        try (PreparedStatement ps = Server.connection.prepareStatement(findDeck)) {
            ps.setInt(1, userID);
            ps.setString(2, deckName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    deckID = rs.getInt("DeckID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to query deck id: " + e);
            e.printStackTrace();
        }

        if (deckID == null) {
            System.out.println("Deck not found in DB (nothing to delete): " + deckName);
            return;
        }

        // 2) delete from DeckContents for that DeckID
        String deleteContents = "DELETE FROM DeckContents WHERE DeckID = ?";
        try (PreparedStatement ps = Server.connection.prepareStatement(deleteContents)) {
            ps.setInt(1, deckID);
            int removed = ps.executeUpdate();
            System.out.println("Deleted " + removed + " cards from DeckContents for DeckID=" + deckID);
        } catch (SQLException e) {
            System.err.println("Failed to delete deck contents: " + e);
            e.printStackTrace();
        }

        // 3) delete the Deck row
        String deleteDeck = "DELETE FROM Decks WHERE DeckID = ?";
        try (PreparedStatement ps = Server.connection.prepareStatement(deleteDeck)) {
            ps.setInt(1, deckID);
            int removed = ps.executeUpdate();
            System.out.println("Deleted deck row (rows removed: " + removed + ") for DeckID=" + deckID);
        } catch (SQLException e) {
            System.err.println("Failed to delete deck row: " + e);
            e.printStackTrace();
        }
    }
    
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
                updateSidePanelForDeck(deckName);
            }
        });
        
        return deck;
    }

    // --- END DATABASE METHODS ---

    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from InventoryMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }

    // --- Helper inner class: represents a DB-loaded owned card ---
    private static class CardEntry {
        final int ownershipID;
        final int cardID;
        final String cardName;
        final String packName;
        final String imagePath;

        CardEntry(int ownershipID, int cardID, String cardName, String packName, String imagePath) {
            this.ownershipID = ownershipID;
            this.cardID = cardID;
            this.cardName = cardName;
            this.packName = packName;
            this.imagePath = imagePath;
        }

        @Override
        public String toString() {
            return "CardEntry{" +
                    "ownershipID=" + ownershipID +
                    ", cardID=" + cardID +
                    ", cardName='" + cardName + '\'' +
                    ", packName='" + packName + '\'' +
                    ", imagePath='" + imagePath + '\'' +
                    '}';
        }
    }
}

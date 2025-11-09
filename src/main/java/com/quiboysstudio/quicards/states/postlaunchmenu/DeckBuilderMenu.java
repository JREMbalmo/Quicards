package com.quiboysstudio.quicards.states.postlaunchmenu;

import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.proxies.CardImageProxy;
import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// (Assuming imports for ComponentFactory, FrameConfig, FrameUtil, Server, State, User, etc.)

public class DeckBuilderMenu extends State {
    //variables
    private boolean running = false;
    private boolean initialized = false;

    // Deck info
    private String deckName;
    private int deckID; // Storing deck ID instead of file path
    private List<Card> cardsInDeck;
    private List<Card> ownedCardsList; // Renamed from ownedCards

    //objects
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
    private JButton contentsTabButton;
    private JButton cardsTabButton; // Renamed from builderTabButton
    private String currentTab = "Contents";

    // NEW: Define deck limit
    private static final int DECK_LIMIT = 30;

    /**
     * Constructor.
     * @param deckName The name of the deck to edit (e.g., "My First Deck").
     * @param deckID The ID of the deck in the database.
     */
    public DeckBuilderMenu(String deckName, int deckID) {
        this.deckName = deckName;
        this.deckID = deckID;
    }

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
        System.out.println("Showing Deck Builder menu");
        layeredPane.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        // Load content
        loadCategoryContent(currentTab);
        updateSidePanel();
        cardLayout.show(cardPanel, "Deck Builder Menu");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;
        System.out.println("Initializing elements from DeckBuilderMenu state");
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

        // The previousState will be InventoryMenu
        backButton = ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 150, previousState);
        backButton.setVerticalAlignment(JButton.CENTER);
        backButton.setHorizontalAlignment(JButton.CENTER);
        navigationPanel.add(backButton);

        categoryPanel = new JPanel();
        categoryPanel.setOpaque(false);
        categoryPanel.setLayout(new FlowLayout(FlowLayout.CENTER, FrameUtil.scale(frame, 10), FrameUtil.scale(frame, 20)));

        contentsTabButton = createCategoryButton("Contents");
        cardsTabButton = createCategoryButton("Cards"); // Renamed

        categoryPanel.add(contentsTabButton);
        categoryPanel.add(cardsTabButton); // Renamed

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
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Always stack rows
        contentPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50)));

        scrollPane = new CustomScrollPane(contentPanel);

        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        firstLayerPanel.add(topBarPanel, BorderLayout.NORTH);
        firstLayerPanel.add(mainPanel, BorderLayout.CENTER);

        layeredPane.add(firstLayerPanel, Integer.valueOf(1));
        cardPanel.add("Deck Builder Menu", layeredPane);

        initialized = true;
        System.out.println("Entering DeckBuilderMenu state");
    }

    private JButton createCategoryButton(String category) {
        JButton button = ComponentFactory.createCustomButton(category, FrameConfig.SATOSHI_BOLD, 200, () -> {
            currentTab = category;
            loadCategoryContent(category);
        });
        return button;
    }

    private void loadCategoryContent(String category) {
        contentPanel.removeAll();
        switch (category) {
            case "Contents":
                populateDeckContents();
                break;
            case "Cards": // Renamed
                populateCards(); // Renamed
                break;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Loads cards in the current deck from the database.
     */
    private void loadDeckContentsFromDatabase() {
        cardsInDeck = new ArrayList<>();
        try {
            // <-- CHANGED: Query now joins through DeckContents -> OwnedCards -> Cards
            String sql = "SELECT c.CardID, oc.OwnershipID, c.Name, p.Name AS PackName " +
                         "FROM DeckContents dc " +
                         "JOIN OwnedCards oc ON dc.OwnershipID = oc.OwnershipID " +
                         "JOIN Cards c ON oc.CardID = c.CardID " +
                         "JOIN PackContents pc ON c.CardID = pc.CardID " +
                         "JOIN Packs p ON pc.PackID = p.PackID " +
                         "WHERE dc.DeckID = " + deckID;

            Server.result = Server.statement.executeQuery(sql);
            while (Server.result.next()) {
                // <-- CHANGED: Pass OwnershipID to Card constructor
                cardsInDeck.add(new Card(
                        Server.result.getInt("CardID"),
                        Server.result.getInt("OwnershipID"),
                        Server.result.getString("Name"),
                        Server.result.getString("PackName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading deck contents: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads all owned cards for the current user from the database.
     */
    private void loadOwnedCardsFromDatabase() {
        ownedCardsList = new ArrayList<>();
        try {
            // <-- CHANGED: Query now selects OwnershipID from OwnedCards
            String sql = "SELECT c.CardID, oc.OwnershipID, c.Name, p.Name AS PackName " +
                         "FROM OwnedCards oc " +
                         "JOIN Cards c ON oc.CardID = c.CardID " +
                         "JOIN PackContents pc ON c.CardID = pc.CardID " +
                         "JOIN Packs p ON pc.PackID = p.PackID " +
                         "WHERE oc.UserID = " + User.getUserID(); // Assuming User.getUserID() is available

            Server.result = Server.statement.executeQuery(sql);
            while (Server.result.next()) {
                // <-- CHANGED: Pass OwnershipID to Card constructor
                ownedCardsList.add(new Card(
                        Server.result.getInt("CardID"),
                        Server.result.getInt("OwnershipID"),
                        Server.result.getString("Name"),
                        Server.result.getString("PackName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading owned cards: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills the panel with cards currently in the deck.
     */
    private void populateDeckContents() {
        loadDeckContentsFromDatabase();
        if (!cardsInDeck.isEmpty()) {
            JPanel currentRowPanel = createRowPanel();
            contentPanel.add(currentRowPanel);
            for (int i = 0; i < cardsInDeck.size(); i++) {
                if (i > 0 && i % 5 == 0) {
                    currentRowPanel = createRowPanel();
                    contentPanel.add(currentRowPanel);
                }
                JPanel cardItem = createDeckCardItem(cardsInDeck.get(i));
                currentRowPanel.add(cardItem);
            }
        }
    }

    /**
     * Fills the panel with *available* cards (owned - in_deck).
     */
    private void populateCards() {
        loadOwnedCardsFromDatabase();
        loadDeckContentsFromDatabase();

        List<Integer> cardsInDeckOwnershipIDs = cardsInDeck.stream()
                .map(Card::getOwnershipID)
                .collect(Collectors.toList());
                
        List<Card> availableCards = ownedCardsList.stream()
                .filter(card -> !cardsInDeckOwnershipIDs.contains(card.getOwnershipID()))
                .collect(Collectors.toList());

        if (!availableCards.isEmpty()) {
            JPanel currentRowPanel = createRowPanel();
            contentPanel.add(currentRowPanel);
            for (int i = 0; i < availableCards.size(); i++) {
                if (i > 0 && i % 5 == 0) {
                    currentRowPanel = createRowPanel();
                    contentPanel.add(currentRowPanel);
                }
                JPanel cardItem = createCardItem(availableCards.get(i)); // Renamed from createBuilderCardItem
                currentRowPanel.add(cardItem);
            }
        }
    }

    private JPanel createRowPanel() {
        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 30)));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FrameUtil.scale(frame, 450))); // card height + button + gaps
        return rowPanel;
    }

    /**
     * Creates a card item for the "Contents" tab (with a "Remove" button).
     * Now refreshes the content panel on click.
     */
    private JPanel createDeckCardItem(Card card) {
        JPanel cardWrapper = createCardVisual(card);
        JButton removeButton = ComponentFactory.createCustomButton("Remove", FrameConfig.SATOSHI_BOLD, 150, () -> {
            removeCardFromDeck(card.getOwnershipID()); // <-- CHANGED: Use OwnershipID
            updateSidePanel(); // Update count
            // FIX: Refresh the "Contents" tab UI
            loadCategoryContent(currentTab);
        });
        removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardWrapper.add(Box.createRigidArea(new Dimension(0, 5)));
        cardWrapper.add(removeButton);
        return cardWrapper;
    }

    /**
     * Creates a card item for the "Cards" tab (with an "Add" button).
     * Now checks limit, adds card, and refreshes the panel.
     */
    private JPanel createCardItem(Card card) {
        JPanel cardWrapper = createCardVisual(card);
        JButton addButton = ComponentFactory.createCustomButton("Add", FrameConfig.SATOSHI_BOLD, 150, () -> {
            // The addCardToDeck method now handles the limit check
            addCardToDeck(card.getOwnershipID());
            updateSidePanel(); // Update count
            // FIX: Refresh the "Cards" tab UI to remove this card
            loadCategoryContent(currentTab);
        });
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardWrapper.add(Box.createRigidArea(new Dimension(0, 5)));
        cardWrapper.add(addButton);
        return cardWrapper;
    }

    /**
     * Base method to create the visual part of the card (image + name).
     */
    private JPanel createCardVisual(Card card) {
        JPanel cardPanel = new JPanel(); // Renamed to avoid conflict with class field
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 600))); // Taller for button

        // Card image (using Proxy)
        String imagePath = "resources/cards/" + card.getPackName() + "/" + card.getName() + ".png";
        int imgWidth = FrameUtil.scale(frame, 180);
        int imgHeight = FrameUtil.scale(frame, 260);
        Icon proxyIcon = new CardImageProxy(imagePath, imgWidth, imgHeight);
        JLabel imageLabel = new JLabel(proxyIcon);
        imageLabel.setPreferredSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setMaximumSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        // Card name label
        JLabel nameLabel = ComponentFactory.createTextLabel(card.getName(), FrameConfig.SATOSHI_BOLD);
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        cardPanel.add(imageLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
        cardPanel.add(nameLabel);
        return cardPanel;
    }

    /**
     * Updates the side panel with deck name and card count.
     */
    private void updateSidePanel() {
        loadDeckContentsFromDatabase(); // Get fresh data
        sidePanel.removeAll();

        JLabel deckNameLabel = ComponentFactory.createTextLabel(deckName, FrameConfig.SATOSHI_BOLD);
        deckNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // MODIFIED: Show count / DECK_LIMIT
        JLabel cardCountLabel = ComponentFactory.createTextLabel("Cards: " + cardsInDeck.size() + " / " + DECK_LIMIT, FrameConfig.SATOSHI_BOLD);
        cardCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(deckNameLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidePanel.add(cardCountLabel);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    // --- Data Modification Methods ---
    /**
     * Added deck limit check and database insertion.
     */
    private void addCardToDeck(int ownershipID) { // <-- CHANGED: Parameter is now ownershipID
        // First, check if deck is full
        loadDeckContentsFromDatabase(); // Get current deck size
        if (cardsInDeck.size() >= DECK_LIMIT) {
            System.out.println("Deck is full. Cannot add card.");
            JOptionPane.showMessageDialog(frame, "Deck is full! Cannot add more than " + DECK_LIMIT + " cards.", "Deck Full", JOptionPane.WARNING_MESSAGE);
            return; // Stop the method
        }

        // If not full, add the card to the database
        try {
            // <-- CHANGED: Insert OwnershipID, not CardID
            String sql = "INSERT INTO DeckContents (DeckID, OwnershipID) VALUES (" + deckID + ", " + ownershipID + ")";
            Server.statement.executeUpdate(sql);
            System.out.println("Added card (OwnershipID: " + ownershipID + ") to deck (ID: " + deckID + ")"); // <-- Log updated
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding card to deck: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Removes a card from the deck in the database.
     */
    private void removeCardFromDeck(int ownershipID) { // <-- CHANGED: Parameter is now ownershipID
        System.out.println("Removing card (OwnershipID: " + ownershipID + ") from deck (ID: " + deckID + ")"); // <-- Log updated
        try {
            // <-- CHANGED: Delete by OwnershipID
            // LIMIT 1 is no longer needed if (DeckID, OwnershipID) is your primary key
            String sql = "DELETE FROM DeckContents WHERE DeckID = " + deckID + " AND OwnershipID = " + ownershipID;
            int affectedRows = Server.statement.executeUpdate(sql);
            if (affectedRows == 0) {
                System.out.println("Card not found in deck or already removed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error removing card from deck: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from DeckBuilderMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }

    // <-- CHANGED: Helper class now includes ownershipID
    private static class Card {
        private int cardID;
        private int ownershipID; // <-- NEW
        private String name;
        private String packName;

        public Card(int cardID, int ownershipID, String name, String packName) { // <-- Constructor updated
            this.cardID = cardID;
            this.ownershipID = ownershipID; // <-- NEW
            this.name = name;
            this.packName = packName;
        }

        public int getCardID() {
            return cardID;
        }
        
        public int getOwnershipID() { // <-- NEW
            return ownershipID;
        }

        public String getName() {
            return name;
        }

        public String getPackName() {
            return packName;
        }
    }
}
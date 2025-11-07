package com.quiboysstudio.quicards.states.postlaunchmenu;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.proxies.CardImageProxy;
import com.quiboysstudio.quicards.states.State;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane; // NEW IMPORT for pop-up messages
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * This state allows editing a deck by adding cards from the player's
 * inventory and removing cards from the deck.
 */
public class DeckBuilderMenu extends State {
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    // Deck info
    private String deckName;
    private String deckFilePath;
    private List<String> cardsInDeck;
    private List<String> ownedCards;

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
    private JButton builderTabButton;
    
    private String currentTab = "Contents";
    
    // NEW: Define deck limit
    private static final int DECK_LIMIT = 30;

    /**
     * Constructor.
     * @param deckName The name of the deck to edit (e.g., "My First Deck").
     */
    public DeckBuilderMenu(String deckName) {
        this.deckName = deckName;
        this.deckFilePath = "decks/" + deckName + ".txt";
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
        builderTabButton = createCategoryButton("Builder");
        
        categoryPanel.add(contentsTabButton);
        categoryPanel.add(builderTabButton);
        
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
        JButton button = ComponentFactory.createCustomButton(category, FrameConfig.SATOSHI_BOLD, 200,
            () -> {
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
            case "Builder":
                populateBuilder();
                break;
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Reads the deck file and populates the card list.
     */
    private void loadDeckContents() {
        cardsInDeck = new ArrayList<>();
        File deckFile = new File(deckFilePath);
        
        // Ensure file exists
        if (!deckFile.exists()) {
            try {
                deckFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try (FileReader fr = new FileReader(deckFile);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    cardsInDeck.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reads the main inventory file.
     */
    private void loadPlayerInventory() {
        ownedCards = new ArrayList<>();
        File inventoryFile = new File("player_inventory.txt");
        
        if (!inventoryFile.exists()) return; // Nothing to load
        
        try (FileReader fr = new FileReader(inventoryFile);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    ownedCards.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Fills the panel with cards currently in the deck.
     */
    private void populateDeckContents() {
        loadDeckContents();
        
        if (cardsInDeck.isEmpty()) {
            contentPanel.add(ComponentFactory.createTextLabel("Deck is empty. Go to the Builder tab to add cards!", FrameConfig.SATOSHI_BOLD));
        } else {
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
     * MODIFIED: Fills the panel with *available* cards (owned - in_deck).
     */
    private void populateBuilder() {
        loadPlayerInventory();
        loadDeckContents();

        // Create a list of cards available to be added
        // This is the core logic fix for duplicates
        List<String> availableCards = new ArrayList<>(ownedCards);
        availableCards.removeAll(cardsInDeck);

        if (availableCards.isEmpty()) {
            if (ownedCards.isEmpty()) {
                contentPanel.add(ComponentFactory.createTextLabel("No cards in inventory. Go to the Store!", FrameConfig.SATOSHI_BOLD));
            } else {
                contentPanel.add(ComponentFactory.createTextLabel("All your cards are in this deck.", FrameConfig.SATOSHI_BOLD));
            }
        } else {
            JPanel currentRowPanel = createRowPanel();
            contentPanel.add(currentRowPanel);

            for (int i = 0; i < availableCards.size(); i++) {
                if (i > 0 && i % 5 == 0) {
                    currentRowPanel = createRowPanel();
                    contentPanel.add(currentRowPanel);
                }
                JPanel cardItem = createBuilderCardItem(availableCards.get(i));
                currentRowPanel.add(cardItem);
            }
        }
    }
    
    private JPanel createRowPanel() {
        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 30)));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FrameUtil.scale(frame, 400))); // card height + button + gaps
        return rowPanel;
    }
    
    /**
     * MODIFIED: Creates a card item for the "Contents" tab (with a "Remove" button).
     * Now refreshes the content panel on click.
     */
    private JPanel createDeckCardItem(String uniqueCardID) {
        JPanel cardWrapper = createCardVisual(uniqueCardID);
        
        JButton removeButton = ComponentFactory.createCustomButton("Remove", FrameConfig.SATOSHI_BOLD, 150, () -> {
            removeCardFromDeck(uniqueCardID);
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
     * MODIFIED: Creates a card item for the "Builder" tab (with an "Add" button).
     * Now checks limit, adds card, and refreshes the panel.
     */
    private JPanel createBuilderCardItem(String uniqueCardID) {
        JPanel cardWrapper = createCardVisual(uniqueCardID);
        
        JButton addButton = ComponentFactory.createCustomButton("Add", FrameConfig.SATOSHI_BOLD, 150, () -> {
            // The addCardToDeck method now handles the limit check
            addCardToDeck(uniqueCardID);
            updateSidePanel(); // Update count
            
            // FIX: Refresh the "Builder" tab UI to remove this card
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
    private JPanel createCardVisual(String uniqueCardID) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 370))); // Taller for button
        
        // Parse filename from unique ID
        String cardFileName = uniqueCardID.split("#")[0];
        String imagePath = "resources/cards/Fantasy Card Pack/" + cardFileName;
        String cardName = parseCardName(cardFileName);

        // Card image (using Proxy)
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
        
        return card;
    }
    
    /**
     * Updates the side panel with deck name and card count.
     */
    private void updateSidePanel() {
        loadDeckContents(); // Get fresh data
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
     * MODIFIED: Added deck limit check.
     */
    private void addCardToDeck(String uniqueCardID) {
        // First, check if deck is full
        loadDeckContents();
        if (cardsInDeck.size() >= DECK_LIMIT) {
            System.out.println("Deck is full. Cannot add card.");
            JOptionPane.showMessageDialog(frame, "Deck is full! Cannot add more than " + DECK_LIMIT + " cards.", "Deck Full", JOptionPane.WARNING_MESSAGE);
            return; // Stop the method
        }
        
        // If not full, add the card
        try (FileWriter fw = new FileWriter(deckFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(uniqueCardID);
            bw.newLine();
            System.out.println("Added card to deck: " + uniqueCardID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void removeCardFromDeck(String uniqueCardID) {
        System.out.println("Removing card from deck: " + uniqueCardID);
        List<String> currentCards = new ArrayList<>();
        File deckFile = new File(deckFilePath);
        
        // Read all cards
        try (FileReader fr = new FileReader(deckFile);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    currentCards.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return; // Don't proceed if read fails
        }
        
        // Remove the first instance of this card
        currentCards.remove(uniqueCardID);
        
        // Rewrite the file
        try (FileWriter fw = new FileWriter(deckFile, false); // false = overwrite
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (String cardID : currentCards) {
                bw.write(cardID);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseCardName(String cardFileName) {
        try {
            String displayName = cardFileName.substring(0, cardFileName.lastIndexOf('.'));
            String[] parts = displayName.split(" - ");
            return parts.length > 1 ? parts[1] : displayName; 
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
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
}
package com.quiboysstudio.quicards.states.postlaunchmenu;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.proxies.CardImageProxy;
import com.quiboysstudio.quicards.states.State;

// --- IMPORTS ---
import com.quiboysstudio.quicards.account.User; // NEW IMPORT
import com.quiboysstudio.quicards.prototype.Card;
import com.quiboysstudio.quicards.prototype.CardRegistry;
import com.quiboysstudio.quicards.server.Server; // NEW IMPORT
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component; // For alignment
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image; // For scaling
import java.awt.RenderingHints;
import java.io.BufferedReader; // For reading file
import java.io.File; // For file handling
import java.io.FileReader; // For reading file
import java.io.IOException; // For error handling
import java.sql.PreparedStatement; // NEW IMPORT
import java.sql.ResultSet; // NEW IMPORT
import java.sql.SQLException; // NEW IMPORT
import java.util.ArrayList; // To store card names
import java.util.LinkedHashMap;
import java.util.List; // To store card names
import java.util.Map;
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

public class InventoryMenu extends State {
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
    private JPanel categoryPanel;
    private JPanel contentPanel;
    private JPanel sidePanel;
    private JButton backButton;
    private JButton cardsTabButton;
    private JButton decksTabButton;
    
    // Current selected tab
    private String currentTab = "Cards";
    
    // MODIFIED: Will store the unique ID (e.g., "Common - Bandit.png#uuid...")
    private String selectedItem = null;
    
    // NEW: To store the loaded card list
    private List<String> ownedCardFileNames;

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
        
        // NEW: Reload content every time menu is shown to refresh inventory
        loadCategoryContent(currentTab);
        
        cardLayout.show(cardPanel, "Inventory Menu");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from InventoryMenu state");
        
        // All component initializations are the same as your file
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
        // MODIFIED: Layout is now set dynamically in loadCategoryContent()
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
            selectedItem = null; // Clear selection when switching tabs
            loadCategoryContent(category);
            });
        
        return button;
    }
    
    /**
     * MODIFIED: Dynamically changes the layout of contentPanel based on the tab.
     */
    private void loadCategoryContent(String category) {
        contentPanel.removeAll();
        sidePanel.removeAll();
        
        switch (category) {
            case "Cards":
                // SET LAYOUT for vertical stacking of rows
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                populateCards(); // Load from file
                break;
            case "Decks":
                // SET LAYOUT for vertical stacking of rows
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                populateDecks(); // Load placeholders
                break;
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
        sidePanel.revalidate();
        sidePanel.repaint();
    }
    
    /**
     * MODIFIED: Helper method to read inventory file.
     */
    private void loadPlayerInventory() {
        ownedCardFileNames = new ArrayList<>();
        File inventoryFile = new File("player_inventory.txt");

        if (!inventoryFile.exists()) {
            try {
                inventoryFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try (FileReader fr = new FileReader(inventoryFile);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    ownedCardFileNames.add(line.trim()); // Add the full unique ID
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * MODIFIED: Populates contentPanel with 5-card rows.
     */
    private void populateCards() {
        loadPlayerInventory(); // Load/refresh the card list

        if (ownedCardFileNames.isEmpty()) {
            contentPanel.add(ComponentFactory.createTextLabel("No cards in inventory. Go to the Store!", FrameConfig.SATOSHI_BOLD));
            updateSidePanelForCard(null); // Clear side panel
        } else {
            // Group cards by base filename
            Map<String, List<String>> groupedCards = groupCardsByBaseName();
            List<String> uniqueCardNames = new ArrayList<>(groupedCards.keySet());

            // Gacha-style row population
            JPanel currentRowPanel = createRowPanel();
            contentPanel.add(currentRowPanel);

            for (int i = 0; i < uniqueCardNames.size(); i++) {
                if (i > 0 && i % 5 == 0) {
                    currentRowPanel = createRowPanel();
                    contentPanel.add(currentRowPanel);
                }
                String baseCardName = uniqueCardNames.get(i);
                int count = groupedCards.get(baseCardName).size();
                JPanel cardItem = createCardItemWithCount(baseCardName, count);
                currentRowPanel.add(cardItem);
            }

            // Initially show first card in side panel
            selectedItem = uniqueCardNames.get(0);
            updateSidePanelForCard(selectedItem);
        }
    }
    
    /**
     * NEW: Helper to load deck names from /decks/ folder.
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
     * MODIFIED: Populates contentPanel with 5-deck rows.
     */
    private void populateDecks() {
        List<String> deckNames = loadPlayerDecks();
        
        if (deckNames.isEmpty()) {
            contentPanel.add(ComponentFactory.createTextLabel("No decks created.", FrameConfig.SATOSHI_BOLD));
            updateSidePanelForDeck(null); // Show "Create Deck" button
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
            
            // Initially show first deck in side panel
            selectedItem = deckNames.get(0);
            updateSidePanelForDeck(deckNames.get(0));
        }
    }
    
    /**
     * NEW: Helper method to create an invisible row panel.
     */
    private JPanel createRowPanel() {
        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 30)));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FrameUtil.scale(frame, 360))); // card height + gaps
        return rowPanel;
    }
    
    /**
     * MODIFIED: Creates a real card item using CardImageProxy from a unique ID.
     */
    private JPanel createCardItem(String uniqueCardID) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 320))); 
        card.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
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
        
        // Add click listener to update side panel
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedItem = uniqueCardID;
                updateSidePanelForCard(uniqueCardID);
            }
        });
        
        return card;
    }
    
    private JPanel createCardItemWithCount(String baseCardFileName, int count) {
        // Add this debug line at the start
        System.out.println("Creating card: " + baseCardFileName + " with count: " + count);

        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 320))); 
        card.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        String imagePath = "resources/cards/Fantasy Card Pack/" + baseCardFileName;
        String cardName = parseCardName(baseCardFileName);

        // Card image container with counter badge
        JPanel imageContainer = new JPanel();
        imageContainer.setOpaque(false);
        imageContainer.setLayout(null); // Absolute positioning for badge
        int imgWidth = FrameUtil.scale(frame, 180);
        int imgHeight = FrameUtil.scale(frame, 260);
        imageContainer.setPreferredSize(new Dimension(imgWidth, imgHeight));
        imageContainer.setMaximumSize(new Dimension(imgWidth, imgHeight));
        imageContainer.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        // Card image (using Proxy)
        Icon proxyIcon = new CardImageProxy(imagePath, imgWidth, imgHeight);
        JLabel imageLabel = new JLabel(proxyIcon);
        imageLabel.setBounds(0, 0, imgWidth, imgHeight);
        imageContainer.add(imageLabel);

        // Counter badge (only show if count > 1)
        if (count > 1) {
            System.out.println("Adding badge for card: " + baseCardFileName); // Debug
            JPanel badge = createCounterBadge(count);
            int badgeSize = FrameUtil.scale(frame, 40);
            badge.setBounds(imgWidth - badgeSize - 5, 5, badgeSize, badgeSize);
            imageContainer.add(badge);
            imageContainer.setComponentZOrder(badge, 0); // Make sure badge is on top
        }

        // Card name label
        JLabel nameLabel = ComponentFactory.createTextLabel(cardName, FrameConfig.SATOSHI_BOLD);
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        card.add(imageContainer);
        card.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
        card.add(nameLabel);

        // Add click listener to update side panel
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedItem = baseCardFileName;
                updateSidePanelForCard(baseCardFileName);
            }
        });

        return card;
    }

/**
 * NEW: Creates a circular counter badge.
 */
    private JPanel createCounterBadge(int count) {
        int badgeSize = FrameUtil.scale(frame, 40);

        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw circle background
                g2d.setColor(new Color(255, 100, 100)); // Red badge
                g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);

                // Draw white border
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(1, 1, getWidth() - 3, getHeight() - 3);

                // Draw count text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, FrameUtil.scale(frame, 18)));
                FontMetrics fm = g2d.getFontMetrics();
                String text = String.valueOf(count);
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(badgeSize, badgeSize);
            }
        };

        badge.setOpaque(false);
        badge.setSize(badgeSize, badgeSize);

        return badge;
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
                selectedItem = deckName;
                updateSidePanelForDeck(deckName);
            }
        });
        
        return deck;
    }
    
    /**
     * MODIFIED: Displays the selected card's scaled image and name from a unique ID.
     */
    private void updateSidePanelForCard(String uniqueCardID) {
    sidePanel.removeAll();
    
    if (uniqueCardID == null) {
        JLabel emptyLabel = ComponentFactory.createTextLabel("Select a card", FrameConfig.SATOSHI_BOLD);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(emptyLabel);
        sidePanel.add(Box.createVerticalGlue());
    } else {
        // Get card clone using Prototype pattern
        Card card = getCardFromPrototype(uniqueCardID);
        
        if (card == null) {
            sidePanel.add(ComponentFactory.createTextLabel("Card not found", FrameConfig.SATOSHI_BOLD));
        } else {
            // --- Large Image ---
            ImageIcon cardIcon = new ImageIcon(card.getImagePath());
            int targetWidth = FrameUtil.scale(frame, 260);
            int originalWidth = cardIcon.getIconWidth();
            int originalHeight = cardIcon.getIconHeight();
            int targetHeight = (int) ((double) targetWidth / originalWidth * originalHeight);
            Image scaledImage = cardIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            JLabel largeImageLabel = new JLabel(new ImageIcon(scaledImage));
            largeImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // --- Card Info Panel ---
            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Name
            JLabel nameLabel = ComponentFactory.createTextLabel(card.getName(), FrameConfig.SATOSHI_BOLD);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Rarity
            JLabel rarityLabel = ComponentFactory.createTextLabel("Rarity: " + card.getRarity(), FrameConfig.SATOSHI_BOLD);
            rarityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Attack
            JLabel attackLabel = ComponentFactory.createTextLabel("ATK: " + card.getAttack(), FrameConfig.SATOSHI_BOLD);
            attackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Defense
            JLabel defenseLabel = ComponentFactory.createTextLabel("DEF: " + card.getDefense(), FrameConfig.SATOSHI_BOLD);
            defenseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            infoPanel.add(nameLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
            infoPanel.add(rarityLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 5))));
            infoPanel.add(attackLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 5))));
            infoPanel.add(defenseLabel);

            sidePanel.add(Box.createVerticalGlue());
            sidePanel.add(largeImageLabel);
            sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 20))));
            sidePanel.add(infoPanel);
            sidePanel.add(Box.createVerticalGlue());
        }
    }
    
    sidePanel.revalidate();
    sidePanel.repaint();
}
    
    /**
     * MODIFIED: Side panel for decks, now includes "Create Deck" and "Delete Deck" DB logic.
     */
    private void updateSidePanelForDeck(String deckName) {
        sidePanel.removeAll();
        sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 50))));
        
        // "Create Deck" button
        JButton createButton = ComponentFactory.createCustomButton("Create Deck", FrameConfig.SATOSHI_BOLD, 200, () -> {
            String newDeckName = JOptionPane.showInputDialog(frame, "Enter deck name:", "Create Deck", JOptionPane.PLAIN_MESSAGE);
            if (newDeckName != null && !newDeckName.trim().isEmpty()) {
                
                // --- MODIFIED LOGIC ---
                // 1. Create the .txt file
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
                // --- END MODIFICATION ---
            }
        });
        createButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        sidePanel.add(createButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 50))));
        
        if (deckName != null) {
            // --- If a deck is selected, show its info ---
            
            // Large deck image
            JPanel largeImagePanel = ComponentFactory.createRoundedPanel(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 390), FrameConfig.BLACK);
            largeImagePanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
            // TODO: Load actual deck image here
            
            // Deck name
            JLabel nameLabel = ComponentFactory.createTextLabel(deckName, FrameConfig.SATOSHI_BOLD);
            nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            
            // Edit button
            JButton editButton = ComponentFactory.createCustomButton("Edit Deck", FrameConfig.SATOSHI_BOLD, 200, () -> {
                System.out.println("Editing deck: " + deckName);
                // --- TRANSITION TO DECK BUILDER ---
                exit(new DeckBuilderMenu(deckName));
                currentState.enter();
                currentState.update();
            });
            editButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
            
            // Delete button
            JButton deleteButton = ComponentFactory.createCustomButton("Delete Deck", FrameConfig.SATOSHI_BOLD, 200, () -> {
                int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete '" + deckName + "'?", "Delete Deck", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    
                    // --- MODIFIED LOGIC ---
                    // 1. Delete .txt file
                    File deckFile = new File("decks/" + deckName + ".txt");
                    if (deckFile.delete()) {
                        System.out.println("Deleted deck file: " + deckName);
                        
                        // 2. Delete from database
                        deleteDeckFromDatabase(deckName);
                        
                        // 3. Refresh UI
                        loadCategoryContent("Decks"); 
                    }
                    // --- END MODIFICATION ---
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
     * MODIFIED: Helper method to parse card name from filename.
     */
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
    
    // --- NEW DATABASE METHODS ---

    /**
     * NEW: Gets the UserID from the Users table.
     */
    private int getUserID() {
        String username = User.getUsername();
        if (username == null) return -1;

        try (PreparedStatement ps = Server.connection.prepareStatement("SELECT UserID FROM Users WHERE Username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found or error
    }

    /**
     * NEW: Inserts a new deck into the Decks table.
     * Inserts a '0' for the problematic 'CardID' column.
     */
    private void addDeckToDatabase(String deckName) {
        int userID = getUserID();
        if (userID == -1) {
            System.err.println("Could not create deck: UserID not found.");
            return;
        }
        
        // This query inserts a dummy '0' for the NOT NULL CardID column.
        // This is a workaround for the schema defect.
        String query = "INSERT INTO Decks (UserID, Name, CardID) VALUES (?, ?, 0)";
        
        try (PreparedStatement ps = Server.connection.prepareStatement(query)) {
            ps.setInt(1, userID);
            ps.setString(2, deckName);
            ps.executeUpdate();
            System.out.println("Added deck to database: " + deckName);
        } catch (SQLException e) {
            System.err.println("Failed to add deck to database. Check 'Decks' table schema.");
            e.printStackTrace();
        }
    }

    /**
     * NEW: Deletes a deck from the Decks table.
     */
    private void deleteDeckFromDatabase(String deckName) {
        int userID = getUserID();
        if (userID == -1) {
            System.err.println("Could not delete deck: UserID not found.");
            return;
        }

        String query = "DELETE FROM Decks WHERE UserID = ? AND Name = ?";
        
        try (PreparedStatement ps = Server.connection.prepareStatement(query)) {
            ps.setInt(1, userID);
            ps.setString(2, deckName);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted deck from database (rows: " + rowsAffected + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Card getCardFromPrototype(String uniqueCardID) {
        String cardFileName = uniqueCardID.split("#")[0];
        return CardRegistry.getInstance().getCard(cardFileName);
    }
    
    private Map<String, List<String>> groupCardsByBaseName() {
        Map<String, List<String>> grouped = new LinkedHashMap<>();

        for (String uniqueCardID : ownedCardFileNames) {
            String baseFileName = uniqueCardID.split("#")[0];

            if (!grouped.containsKey(baseFileName)) {
                grouped.put(baseFileName, new ArrayList<>());
            }
            grouped.get(baseFileName).add(uniqueCardID);
        }

        return grouped;
    }
    
    // --- END NEW DATABASE METHODS ---
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from InventoryMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}

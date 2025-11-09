package com.quiboysstudio.quicards.states.postlaunchmenu;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.proxies.CardImageProxy;
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.server.Server;

// Patterns imports
import com.quiboysstudio.quicards.prototype.Card;
import com.quiboysstudio.quicards.prototype.CardRegistry;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * InventoryMenu (DB-driven) — UI & design improvements applied
 * - Visual polish: rounded panels, badges, hover highlighting, consistent spacing
 * - Uses CardImageProxy for images (proxy pattern)
 * - Uses CardRegistry prototype when available to show detailed info (prototype pattern)
 *
 * NOTE: Database behavior remains unchanged.
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

    // Placeholder image path (used when image file missing)
    private static final String DEFAULT_CARD_PLACEHOLDER = "resources/cards/default_placeholder.png";

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
                populateDecks(); // DB-driven
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

        int userID = User.getUserID();
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
     * Adds UI enhancements: rounded image panel, badge for duplicates, hover highlight, proxy images.
     */
    private void populateCards() {
        loadPlayerInventoryFromDB(); // Load/refresh ownedCards

        if (ownedCards.isEmpty()) {
            contentPanel.add(ComponentFactory.createTextLabel("No cards in inventory. Go to the Store!", FrameConfig.SATOSHI_BOLD));
            updateSidePanelForCard(null); // Clear side panel
        } else {
            // compute counts per cardName for badges (UI-only; does not change DB behaviour)
            Map<String, Integer> counts = new HashMap<>();
            for (CardEntry e : ownedCards) {
                counts.put(e.cardName, counts.getOrDefault(e.cardName, 0) + 1);
            }

            // Row population (5 items per row)
            JPanel currentRowPanel = createRowPanel();
            contentPanel.add(currentRowPanel);

            for (int i = 0; i < ownedCards.size(); i++) {
                if (i > 0 && i % 5 == 0) {
                    currentRowPanel = createRowPanel();
                    contentPanel.add(currentRowPanel);
                }
                CardEntry entry = ownedCards.get(i);
                JPanel cardItem = createCardItem(entry, counts.getOrDefault(entry.cardName, 1));
                currentRowPanel.add(cardItem);
            }

            // Initially show first card in side panel (same behaviour as before)
            selectedCard = ownedCards.get(0);
            updateSidePanelForCard(selectedCard);
        }
    }

    /**
     * Helper to load deck names from the Decks table (DB-driven).
     */
    private List<String> loadPlayerDecksFromDB() {
        List<String> deckNames = new ArrayList<>();
        int userID = User.getUserID();
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

        return deckNames;
    }

    /**
     * Populates contentPanel with decks (5 per row).
     */
    private void populateDecks() {
        List<String> deckNames = loadPlayerDecksFromDB();

        if (deckNames.isEmpty()) {
            contentPanel.add(ComponentFactory.createTextLabel("No decks created.", FrameConfig.SATOSHI_BOLD));
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
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FrameUtil.scale(frame, 400))); // card height + gaps
        return rowPanel;
    }

    /**
     * Creates a card item UI from a CardEntry.
     * Adds: rounded image container, proxy image, badge (count), hover highlight, selection border.
     */
    private JPanel createCardItem(CardEntry entry, int totalCount) {
        final JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 320)));
        card.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        String imagePath = entry.imagePath;
        String cardName = entry.cardName;

        // Image container (rounded panel) — using ComponentFactory to keep consistent look
        int imgWidth = FrameUtil.scale(frame, 180);
        int imgHeight = FrameUtil.scale(frame, 260);

        JPanel imageContainer = ComponentFactory.createRoundedPanel(imgWidth, imgHeight, FrameConfig.BLACK);
        imageContainer.setLayout(null);
        imageContainer.setPreferredSize(new Dimension(imgWidth, imgHeight));
        imageContainer.setMaximumSize(new Dimension(imgWidth, imgHeight));
        imageContainer.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        // Use CardImageProxy for lazy loading and scaling
        // Ensure fallback if file is missing (proxy will handle missing files gracefully, but add fallback check)
        File f = new File(imagePath);
        String finalImagePath = f.exists() ? imagePath : DEFAULT_CARD_PLACEHOLDER;
        Icon proxyIcon = new CardImageProxy(finalImagePath, imgWidth, imgHeight);
        JLabel imageLabel = new JLabel(proxyIcon);
        imageLabel.setBounds(0, 0, imgWidth, imgHeight);
        imageContainer.add(imageLabel);

        // Badge for duplicates (only show if totalCount > 1)
        if (totalCount > 1) {
            JPanel badge = createCounterBadge(totalCount);
            int badgeSize = FrameUtil.scale(frame, 40);
            // place badge top-right with a little margin
            badge.setBounds(imgWidth - badgeSize - FrameUtil.scale(frame, 6), FrameUtil.scale(frame, 6), badgeSize, badgeSize);
            imageContainer.add(badge);
        }

        // Card name label
        JLabel nameLabel = ComponentFactory.createTextLabel(cardName, FrameConfig.SATOSHI_BOLD);
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        card.add(imageContainer);
        card.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
        card.add(nameLabel);

        // Hover highlight and selection border
        LineBorder hoverBorder = new LineBorder(new Color(255, 220, 100), FrameUtil.scale(frame, 3), true);
        LineBorder selectedBorder = new LineBorder(new Color(100, 220, 255), FrameUtil.scale(frame, 3), true);

        card.setBorder(null);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedCard = entry;
                updateSidePanelForCard(entry);

                // mark this card visually as selected; remove selection from siblings by repainting parent
                card.setBorder(selectedBorder);
                // unselect siblings
                ContainerParentClearSelection(card);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (card.getBorder() == null) {
                    card.setBorder(hoverBorder);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // if it's not selected, remove hover border
                if (selectedCard == null || !selectedCard.equals(entry)) {
                    card.setBorder(null);
                } else {
                    // keep selected border
                    card.setBorder(selectedBorder);
                }
            }
        });

        return card;
    }

    /**
     * Clears selection border for sibling components when one card is selected.
     */
    private void ContainerParentClearSelection(JPanel selectedCardPanel) {
        if (selectedCardPanel.getParent() == null) return;
        java.awt.Component[] siblings = selectedCardPanel.getParent().getComponents();
        for (java.awt.Component s : siblings) {
            if (s instanceof JPanel && s != selectedCardPanel) {
                ((JPanel) s).setBorder(null);
            }
        }
    }

    /**
     * Creates a circular counter badge (drawn custom).
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
                g2d.setColor(new Color(220, 60, 60)); // red-ish
                g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);

                // Draw white border
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(1, 1, getWidth() - 3, getHeight() - 3);

                // Draw count text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, FrameUtil.scale(frame, 16)));
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
     * Side panel: displays selected card's scaled image and name.
     * Uses prototype (CardRegistry) when available to show additional stats (attack/defense/rarity).
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
            // Try to get the prototype Card by name (non-destructive; prototype may not exist)
            Card prototype = null;
            try {
                prototype = CardRegistry.getInstance().getCard(entry.cardName);
            } catch (Exception ignored) {
                prototype = null;
            }

            String imagePath = entry.imagePath;
            String cardName = entry.cardName;
            String displayName = cardName;

            // If prototype exists, prefer prototype details (keeps DB logic intact — purely presentational)
            if (prototype != null) {
                displayName = prototype.getName() != null ? prototype.getName() : cardName;
            }

            // --- Large Image ---
            File f = new File(imagePath);
            String finalImagePath = f.exists() ? imagePath : DEFAULT_CARD_PLACEHOLDER;

            ImageIcon cardIcon = new ImageIcon(finalImagePath);
            int targetWidth = FrameUtil.scale(frame, 260);
            int originalWidth = Math.max(1, cardIcon.getIconWidth());
            int originalHeight = Math.max(1, cardIcon.getIconHeight());
            int targetHeight = (int) ((double) targetWidth / originalWidth * originalHeight);
            Image scaledImage = cardIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            JLabel largeImageLabel = new JLabel(new ImageIcon(scaledImage));
            largeImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // --- Info Panel ---
            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameLabel = ComponentFactory.createTextLabel(displayName, FrameConfig.SATOSHI_BOLD);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            infoPanel.add(nameLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));

            // If prototype present, show rarity / attack / defense details from prototype
            if (prototype != null) {
                JLabel rarityLabel = ComponentFactory.createTextLabel("Rarity: " + prototype.getRarity(), FrameConfig.SATOSHI_BOLD);
                rarityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel attackLabel = ComponentFactory.createTextLabel("ATK: " + prototype.getAttack(), FrameConfig.SATOSHI_BOLD);
                attackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel defenseLabel = ComponentFactory.createTextLabel("DEF: " + prototype.getDefense(), FrameConfig.SATOSHI_BOLD);
                defenseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                infoPanel.add(rarityLabel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 5))));
                infoPanel.add(attackLabel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 5))));
                infoPanel.add(defenseLabel);
            } else {
                // If no prototype, only show basic name (keeps behavior intact)
                JLabel hint = ComponentFactory.createTextLabel("(No prototype available)", FrameConfig.SATOSHI_BOLD);
                hint.setAlignmentX(Component.CENTER_ALIGNMENT);
                infoPanel.add(hint);
            }

            sidePanel.add(Box.createVerticalGlue());
            sidePanel.add(largeImageLabel);
            sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 20))));
            sidePanel.add(infoPanel);
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

                // REMOVED: All legacy file creation logic (File, File.mkdir(), newFile.createNewFile(), try/catch IOException)

                // 1. Add to database
                if (addDeckToDatabase(newDeckName)) {
                    System.out.println("Deck added to database: " + newDeckName);
                    // 2. Refresh UI
                    loadCategoryContent("Decks");
                } else {
                    // This assumes the failure was a duplicate name, mimicking the old file-based check.
                    // A UNIQUE constraint in your DB on (UserID, Name) will cause addDeckToDatabase to fail and return false.
                    JOptionPane.showMessageDialog(frame, "A deck with this name already exists or a database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        createButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        sidePanel.add(createButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 50))));

        if (deckName != null) {

            int searchID = 0;
            String findDeck = "SELECT DeckID FROM Decks WHERE UserID = ? AND Name = ?";

            try (PreparedStatement ps = Server.connection.prepareStatement(findDeck)) {
                ps.setInt(1, User.getUserID());
                ps.setString(2, deckName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        searchID = rs.getInt("DeckID");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Failed to query deck id: " + e);
                e.printStackTrace();
            }
            int deckID = searchID;

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
                exit(new DeckBuilderMenu(deckName, deckID));
                currentState.enter();
                currentState.update();
            });
            editButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

            // Delete button
            JButton deleteButton = ComponentFactory.createCustomButton("Delete Deck", FrameConfig.SATOSHI_BOLD, 200, () -> {
                int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete '" + deckName + "'?", "Delete Deck", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {

                    // 1. Delete from database (DeckContents then Decks)
                    deleteDeckFromDatabase(deckName);

                    // 2. Refresh UI
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
     * Inserts a new deck into the Decks table (DB).
     * MODIFIED: Returns boolean to indicate success/failure.
     */
    private boolean addDeckToDatabase(String deckName) {
        int userID = User.getUserID();
        if (userID == -1) {
            System.err.println("Could not create deck: UserID not found.");
            return false; // MODIFIED
        }

        String insert = "INSERT INTO Decks (UserID, Name) VALUES (?, ?)";
        try (PreparedStatement ps = Server.connection.prepareStatement(insert)) {
            ps.setInt(1, userID);
            ps.setString(2, deckName);
            ps.executeUpdate();
            System.out.println("Added deck to database: " + deckName);
            return true; // MODIFIED
        } catch (SQLException e) {
            System.err.println("Failed to add deck to database: " + e);
            e.printStackTrace();
            return false; // MODIFIED (e.g., if a UNIQUE constraint is violated)
        }
    }

    /**
     * Deletes a deck and its contents from DeckContents and Decks tables (DB).
     */
    private void deleteDeckFromDatabase(String deckName) {
        int userID = User.getUserID();
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
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateSidePanelForDeck(deckName);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                deck.setBorder(new LineBorder(new Color(255, 220, 100), FrameUtil.scale(frame, 3), true));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                deck.setBorder(null);
            }
        });

        return deck;
    }

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

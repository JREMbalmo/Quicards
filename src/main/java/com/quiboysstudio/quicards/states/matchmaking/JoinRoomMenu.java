package com.quiboysstudio.quicards.states.matchmaking;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class JoinRoomMenu extends State {
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JLayeredPane layeredPane;
    private JScrollPane scrollPane;
    private JPanel firstLayerPanel;
    private JPanel topBarPanel;
    private JPanel navigationPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel sidePanel;
    private JLabel headerLabel;
    private JButton backButton;
    private JButton joinButton;
    
    // Selected room
    private String selectedRoom = null;

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
        
        System.out.println("Showing Join Room menu");
        
        //add background
        layeredPane.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Join Room Menu");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from JoinRoomMenu state");
        
        //initialize layered panel
        layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        //initialize first layer
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        // Top bar panel
        topBarPanel = new JPanel();
        topBarPanel.setOpaque(false);
        topBarPanel.setLayout(new BorderLayout());
        topBarPanel.setPreferredSize(new Dimension(frame.getWidth(), FrameUtil.scale(frame, 150)));
        
        // Navigation panel (back button)
        navigationPanel = new JPanel();
        navigationPanel.setOpaque(false);
        navigationPanel.setLayout(new FlowLayout(FlowLayout.LEFT, FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 20)));
        
        // Back button
        backButton = ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 150, previousState);
        backButton.setVerticalAlignment(JButton.CENTER);
        backButton.setHorizontalAlignment(JButton.CENTER);
        
        navigationPanel.add(backButton);
        
        // Header panel (centered title)
        headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, FrameUtil.scale(frame, 20)));
        
        // Header label
        headerLabel = ComponentFactory.createTextLabel("Select Room", FrameConfig.SATOSHI_BOLD);
        headerPanel.add(headerLabel);
        
        topBarPanel.add(navigationPanel, BorderLayout.WEST);
        topBarPanel.add(headerPanel, BorderLayout.CENTER);
        
        // Main container with side panel and content
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        
        // Side panel (left side) - for Join button
        sidePanel = new JPanel();
        sidePanel.setOpaque(false);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(FrameUtil.scale(frame, 280), frame.getHeight()));
        sidePanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 30)));
        
     // Join button
    joinButton = ComponentFactory.createCustomButton("Join Room", FrameConfig.SATOSHI_BOLD, 200,
        () -> {
            if (selectedRoom != null) {
                // Ask player to select a deck
                List<String> deckNames = loadPlayerDecks();
                if (deckNames.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "You need to create a deck first!", "No Decks", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String selectedDeck = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select your deck:",
                    "Choose Deck",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    deckNames.toArray(),
                    deckNames.get(0)
                );

                if (selectedDeck != null) {
                    System.out.println("Joining room: " + selectedRoom + " with deck: " + selectedDeck);

                    SwingUtilities.invokeLater(() -> {
                        Room room = new Room(selectedDeck); // Pass deck name
                        room.setVisible(true);
                    });
                }
            }
        });
        
        joinButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        joinButton.setEnabled(false); // Disabled until a room is selected
        
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(joinButton);
        sidePanel.add(Box.createVerticalGlue());
        
        // Content panel (right side) - for list of rooms
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50)));
        
        // Populate with sample rooms - replace with actual data
        populateRooms();
        
        //scroll pane for content
        scrollPane = new CustomScrollPane(contentPanel);
        
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        //add components
        firstLayerPanel.add(topBarPanel, BorderLayout.NORTH);
        firstLayerPanel.add(mainPanel, BorderLayout.CENTER);
        
        layeredPane.add(firstLayerPanel, Integer.valueOf(1));
        
        //create join room menu card
        cardPanel.add("Join Room Menu", layeredPane);
        
        initialized = true;
        
        System.out.println("Entering JoinRoomMenu state");
    }
    
    private List<String> loadPlayerDecks() {
        List<String> deckNames = new ArrayList<>();
        File deckDir = new File("decks");
        if (!deckDir.exists()) return deckNames;

        File[] files = deckDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                deckNames.add(file.getName().replace(".txt", ""));
            }
        }
        return deckNames;
    }
    
    private void populateRooms() {
        // Sample rooms - replace with actual data from server
        String[] roomNames = {
            "Anon's Room 100",
            "Anon's Room 101",
            "Anon's Room 102",
            "Anon's Room 103",
            "Anon's Room 104",
            "Anon's Room 105",
            "Anon's Room 106",
            "Anon's Room 107"
        };
        
        for (String roomName : roomNames) {
            JButton roomButton = createRoomButton(roomName);
            contentPanel.add(roomButton);
            contentPanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 15))));
        }
    }
    
    private JButton createRoomButton(String roomName) {
        JButton roomButton = ComponentFactory.createCustomButton(roomName, FrameConfig.SATOSHI_BOLD, 577, () -> {});
        roomButton.setMaximumSize(new Dimension(FrameUtil.scale(frame, 577), FrameUtil.scale(frame, 70)));
        roomButton.setPreferredSize(new Dimension(FrameUtil.scale(frame, 577), FrameUtil.scale(frame, 70)));
        roomButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        
        roomButton.addActionListener(e -> {
            selectedRoom = roomName;
            joinButton.setEnabled(true);
            System.out.println("Selected room: " + roomName);
        });
        
        return roomButton;
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from JoinRoomMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}

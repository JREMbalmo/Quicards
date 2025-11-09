package com.quiboysstudio.quicards.states.matchmaking;

import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
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
    private JButton refreshButton; // Added

    // Selected room
    private int selectedRoomID = -1; // Changed from String to int

    @Override
    public void enter() {
        init();
        // Populate rooms every time the menu is entered - REMOVED to prevent auto-refresh
        // populateRooms();
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
        
        // Reset selection
        selectedRoomID = -1;
        joinButton.setEnabled(false);
        
        // --- ADDED ---
        // Clear the panel on entry and prompt user to refresh.
        // This prevents the list from auto-loading.
        contentPanel.removeAll();
        JLabel promptLabel = ComponentFactory.createTextLabel("Press Refresh to load rooms.", FrameConfig.SATOSHI);
        promptLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        contentPanel.add(promptLabel);
        // --- END ADDED ---
        
        cardLayout.show(cardPanel, "Join Room Menu");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;

        // TODO: The 'waitingRoom' state object must be initialized here
        // e.g., waitingRoom = StateManager.getState("WaitingRoom");
        
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
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 20)));
        
        // Header label
        headerLabel = ComponentFactory.createTextLabel("Select Room", FrameConfig.SATOSHI_BOLD);
        headerPanel.add(headerLabel);

        // Refresh button
        refreshButton = ComponentFactory.createCustomButton("Refresh", FrameConfig.SATOSHI_BOLD, 150, this::populateRooms);
        headerPanel.add(refreshButton);
        
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
            // Updated to call new handler method
            handleJoinRoomRequest();
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
        // This method is no longer used by handleJoinRoomRequest,
        // but left in case other parts of the class use it.
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
    
    /**
     * Fetches available rooms from the database and displays them.
     */
    private void populateRooms() {
        // Run database query on a new thread
        new Thread(() -> {
            // --- MODIFICATION START ---
            // Create a temporary list to hold new buttons.
            // This prevents clearing the UI until we have the new data.
            final List<JButton> newRoomButtons = new ArrayList<>();
            final JLabel noRoomsLabel = ComponentFactory.createTextLabel("No available rooms found.", FrameConfig.SATOSHI);
            noRoomsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            
            boolean hasRooms = false;
            // --- MODIFICATION END ---
            
            // Clear current rooms - MOVED
            /*
            SwingUtilities.invokeLater(() -> {
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();
                selectedRoomID = -1;
                joinButton.setEnabled(false);
            });
            */

            // SQL to find rooms with < 2 players
            String sql = "SELECT r.RoomID, COUNT(p.UserID) AS PlayerCount " +
                         "FROM Rooms r " +
                         "LEFT JOIN PlayersInRoom p ON r.RoomID = p.RoomID " +
                         "WHERE r.Finished = 0 " +
                         "GROUP BY r.RoomID " +
                         "HAVING PlayerCount < 2;";

            try (
                 Statement statement = Server.connection.createStatement();
                 ResultSet rs = statement.executeQuery(sql)) {

                if (rs.isBeforeFirst()) { // Check if there are any rows at all
                    hasRooms = true;
                }

                while (rs.next()) {
                    int roomID = rs.getInt("RoomID");
                    int playerCount = rs.getInt("PlayerCount");

                    // Create button for each room
                    JButton roomButton = createRoomButton(roomID, playerCount);
                    
                    // Add button to the temporary list instead of directly to panel
                    newRoomButtons.add(roomButton);
                }
                
                // --- MODIFICATION START ---
                // Now, update the UI *after* all data is fetched
                final boolean finalHasRooms = hasRooms; // Need final for lambda
                SwingUtilities.invokeLater(() -> {
                    // 1. Clear the panel
                    contentPanel.removeAll();
                    selectedRoomID = -1;
                    joinButton.setEnabled(false);

                    // 2. Add new components
                    if (finalHasRooms) {
                        for (JButton button : newRoomButtons) {
                            contentPanel.add(button);
                            contentPanel.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 15))));
                        }
                    } else {
                        // No rooms found
                        contentPanel.add(noRoomsLabel);
                    }
                    
                    // 3. Refresh the panel
                    contentPanel.revalidate();
                    contentPanel.repaint();
                });
                // --- MODIFICATION END ---

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    // Also clear panel on error and show a message
                    contentPanel.removeAll();
                    contentPanel.add(noRoomsLabel);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                    JOptionPane.showMessageDialog(frame, "Error fetching rooms: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    
    /**
     * Creates a button representing a single room.
     * @param roomID The ID of the room.
     * @param playerCount The current number of players in the room.
     * @return A JButton configured to represent the room.
     */
    private JButton createRoomButton(int roomID, int playerCount) {
        String roomName = "Room " + roomID + " (" + playerCount + "/2)";
        JButton roomButton = ComponentFactory.createCustomButton(roomName, FrameConfig.SATOSHI_BOLD, 577, () -> {});
        roomButton.setMaximumSize(new Dimension(FrameUtil.scale(frame, 577), FrameUtil.scale(frame, 70)));
        roomButton.setPreferredSize(new Dimension(FrameUtil.scale(frame, 577), FrameUtil.scale(frame, 70)));
        roomButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        
        roomButton.addActionListener(e -> {
            selectedRoomID = roomID;
            joinButton.setEnabled(true);
            System.out.println("Selected room ID: " + roomID);
            // Optional: highlight selected button
        });
        
        return roomButton;
    }

    /**
     * Handles the entire process of joining a room, from deck selection
     * to polling for a server response.
     */
    private void handleJoinRoomRequest() {
        if (selectedRoomID == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room first.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- DECK SELECTION LOGIC REMOVED ---

        // 2. Run request on new thread
        new Thread(() -> {
            try (Connection conn = Server.connection;
                 Statement statement = conn.createStatement()) {

                // --- DECKID QUERY REMOVED ---

                // 4. Create Join Request
                int userID = User.getUserID();
                String password = User.getPassword(); // Assuming password is required
                int actionID = 9; // Join Room
                String var1 = String.valueOf(selectedRoomID);
                // String var2 = String.valueOf(deckID); // Removed
                String var2 = "NULL"; // Set Var2 to NULL as deck is no longer handled here

                // WARNING: SQL Injection vulnerability
                String insertSql = "INSERT INTO Request (UserID, Password, ActionID, Var1, Var2, Processed) VALUES (" +
                                   userID + ", '" + password.replace("'", "''") + "', " + actionID + ", '" + var1 + "', " + var2 + ", 0);";
                
                statement.executeUpdate(insertSql);

                // 5. Get RequestID
                int requestID = -1;
                String sqlGetID = "SELECT RequestID FROM Request WHERE UserID = " + userID + " ORDER BY RequestID DESC LIMIT 1;";
                ResultSet rsID = statement.executeQuery(sqlGetID);
                if (rsID.next()) {
                    requestID = rsID.getInt("RequestID");
                }
                rsID.close();

                if (requestID == -1) {
                    throw new SQLException("Failed to retrieve RequestID after insert.");
                }

                // 6. Poll for Result
                boolean requestFulfilled = false;
                while (!requestFulfilled) {
                    String sqlResult = "SELECT Valid FROM Result WHERE RequestID = " + requestID;
                    ResultSet rsResult = statement.executeQuery(sqlResult);

                    if (rsResult.next()) {
                        requestFulfilled = true;
                        int valid = rsResult.getInt("Valid");

                        if (valid == 1) {
                            // Success
                            SwingUtilities.invokeLater(() -> {
                                // As requested:
                                if (waitingRoom != null) {
                                    exit(waitingRoom);
                                } else {
                                    System.err.println("Join successful, but 'waitingRoom' state is null!");
                                    JOptionPane.showMessageDialog(frame, "Joined room, but state transition failed.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                        } else {
                            // Failed (e.g., room full)
                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Failed to join room. It might be full or no longer exists.", "Join Failed", JOptionPane.ERROR_MESSAGE));
                        }
                    }
                    rsResult.close();
                    Thread.sleep(1000); // Poll every 1 second
                }

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Error joining room: " + e.getMessage(), "Request Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
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
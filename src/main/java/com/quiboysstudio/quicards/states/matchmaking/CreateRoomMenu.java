package com.quiboysstudio.quicards.states.matchmaking;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.server.Server;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CreateRoomMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JLayeredPane layeredPanel;
    private JPanel firstLayerPanel;
    private JPanel creationMenuPanel;
    private JPanel buttonPanel;
    private JTextField nameField;
    private JLabel nameLabel;
    private JButton backButton;
    private JButton createButton;
    
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
        
        //add header to first layer
        firstLayerPanel.add(FrameConfig.header, BorderLayout.NORTH);
        
        //add background
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Create Room Menu");
        frame.revalidate();
        frame.repaint();
    }
    
    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from Create Room state");
        
        // initialize layered panel
        layeredPanel = new JLayeredPane();
        layeredPanel.setOpaque(false);
        layeredPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        //initialize first layer
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        //main panel;
        creationMenuPanel = new JPanel();
        creationMenuPanel.setOpaque(false);
        creationMenuPanel.setPreferredSize(FrameUtil.scale(frame, 557, 520));
        creationMenuPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //button panel
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 556, 150));
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 50),0,0,0));
        
        //text fields
        nameField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        
        //labels
        nameLabel = ComponentFactory.createRoundedLabel("Room Name",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        
        //add components
        creationMenuPanel.add(nameLabel);
        creationMenuPanel.add(nameField);
        
        //buttons
        backButton = ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 250, mainMenu);
        createButton = ComponentFactory.createCustomButton("Create Room", FrameConfig.SATOSHI_BOLD, 250, () -> {
                    // Call the new method to handle room creation
                    createRoom();
                }
            );
        buttonPanel.add(backButton);
        buttonPanel.add(createButton);
        
        creationMenuPanel.add(buttonPanel);
        
        //subpanels
        firstLayerPanel.add(creationMenuPanel, BorderLayout.CENTER);
        
        //panel layers
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create host server menu card
        cardPanel.add("Create Room Menu", layeredPanel);
        
        initialized = true;
        
        System.out.println("Entering Create Room state");
    }
    
    /**
     * Handles the logic for creating a room by inserting a request into the database
     * and polling for a result.
     */
    private void createRoom() {
        String roomName = nameField.getText();
        
        //disable buttons
        backButton.setEnabled(false);
        createButton.setEnabled(false);

        // Validate input
        if (roomName == null || roomName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Room name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Run database operations on a separate thread to avoid freezing the UI
        new Thread(() -> {
            try {
                
                // Get user data (assuming static User class/methods as requested)
                int userID = User.getUserID();
                String password = User.getPassword();
                int actionID = 8; // ActionID for creating a room

                // 1. Insert the request into the Request table
                String insertSql = "INSERT INTO Request (UserID, Password, ActionID, Var1) VALUES ("
                                 + userID + ", '" + password + "', " + actionID + ", '" + roomName + "')";

                Server.statement.executeUpdate(insertSql);

                // 2. Retrieve the RequestID as requested
                String selectIdSql = "SELECT RequestID FROM Request ORDER BY RequestID DESC LIMIT 1";
                Server.result = Server.statement.executeQuery(selectIdSql);

                int requestID = -1;
                if (Server.result.next()) {
                    requestID = Server.result.getInt("RequestID");
                }

                if (requestID == -1) {
                    throw new SQLException("Could not retrieve RequestID after insert.");
                }

                // 3. Poll the Result table
                int attempts = 0;
                boolean resultFound = false;
                int maxAttempts = 10;

                while (!resultFound && attempts < maxAttempts) {
                    // System.out.println("Polling for RequestID: " + requestID + ", Attempt: " + (attempts + 1));
                    
                    String pollSql = "SELECT Valid, NumResult FROM Result WHERE RequestID = " + requestID;
                    Server.result = Server.statement.executeQuery(pollSql);

                    if (Server.result.next()) {
                        // Result row has been created, stop polling
                        resultFound = true;
                        int valid = Server.result.getInt("Valid");
                        int roomID = Server.result.getInt("NumResult");

                        if (valid == 1) {
                            // SUCCESS
                            JOptionPane.showMessageDialog(frame, "Room created successfully!");
                            WaitingRoom.setRoomID(roomID);
                            exit(waitingRoom);
                        } else {
                            // FAILED (Valid == 0)
                            JOptionPane.showMessageDialog(frame, "Room creation failed");
                        }
                    } else {
                        // No result yet, wait and try again
                        attempts++;
                        Thread.sleep(1000); // Wait 1 second before polling again
                    }
                }

                if (!resultFound) {
                    // Polling timed out
                    JOptionPane.showMessageDialog(frame, "Room creation request timed out. Please try again.", "Timeout", JOptionPane.ERROR_MESSAGE);
                    
                    //re enable buttons
                    backButton.setEnabled(true);
                    createButton.setEnabled(true);
                }

            } catch (SQLException e) {
                // Handle database errors
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                //re enable buttons
                backButton.setEnabled(true);
                createButton.setEnabled(true);
            } catch (InterruptedException e) {
                // Handle thread interruption
                Thread.currentThread().interrupt();
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Room creation was interrupted.", "Error", JOptionPane.ERROR_MESSAGE);
                //re enable buttons
                backButton.setEnabled(true);
                createButton.setEnabled(true);
            } catch (Exception e) {
                // Handle other unexpected errors (e.g., if User class or connection is null)
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                //re enable buttons
                backButton.setEnabled(true);
                createButton.setEnabled(true);
            }
        }).start();
    }
    
    private void clearFields() {
        //reset fields
        nameField.setText(null);
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from Create Room");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
        backButton.setEnabled(true);
        createButton.setEnabled(true);
        
        //cleanup
        clearFields();
    }
}
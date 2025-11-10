package com.quiboysstudio.quicards.server.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class MatchmakingHandler {

    // SQL
    private ResultSet result;            // main ResultSet for Request table
    private final Statement mainStatement;  // used ONLY for looping requests + updates
    private final Statement queryStatement; // used for all internal queries to avoid corruption

    /**
     * Constructor for the MatchmakingHandler.
     * Initializes the main statement and a separate query statement.
     * @param result A ResultSet (can be null).
     * @param mainStatement The main statement for database updates.
     * @param conn The database connection.
     */
    public MatchmakingHandler(ResultSet result, Statement mainStatement, Connection conn) {
        this.result = result;

        // Create a second Statement to keep the main ResultSet stable
        Statement temp = null;
        Statement temp1 = null;
        try {
            temp = conn.createStatement();
            temp1 = conn.createStatement();
        } catch (Exception e) {
            System.err.println("Failed to create queryStatement for MatchmakingHandler: " + e);
        }
        this.queryStatement = temp;
        this.mainStatement = temp1;
    }

    /**
     * Public entry point to check for and process matchmaking actions.
     * After processing, it cleans up any empty rooms.
     */
    public void checkActions() {
        // Process new requests
        processRequests(checkNewRows());

        // ** NEW **
        // Clean up empty rooms after processing requests
        cleanupEmptyRooms();
    }

    /**
     * Fetches all unprocessed requests related to matchmaking (ActionID 8 or 9).
     * @return ResultSet containing unprocessed matchmaking requests.
     */
    private ResultSet checkNewRows() {
        try {
            // Poll for requests with ActionID 8 (Create Room) or 9 (Join Room)
            result = mainStatement.executeQuery(
                "SELECT * FROM Request WHERE Processed = 0 AND ActionID IN (8, 9);"
            );
        } catch (Exception e) {
            System.err.println("Failed to check Request table for matchmaking: " + e);
        }
        return result;
    }

    /**
     * Loops through the ResultSet of unprocessed requests and dispatches them.
     * @param result The ResultSet from checkNewRows().
     */
    private void processRequests(ResultSet result) {
        if (result == null) return;

        try {
            while (result.next()) {
                int requestID = result.getInt("RequestID");
                int actionID  = result.getInt("ActionID");
                int userID    = result.getInt("UserID");
                String var1   = result.getString("Var1");

                System.out.println("Processing matchmaking RequestID: " + requestID + " with ActionID: " + actionID);

                try {
                    if (actionID == 8) {
                        // Handle Create Room
                        handleCreateRoom(requestID, userID, var1);
                    } else if (actionID == 9) {
                        // Handle Join Room
                        handleJoinRoom(requestID, userID, var1);
                    }
                } catch (Exception e) {
                    // Catch errors during individual request processing
                    System.err.println("Error processing RequestID " + requestID + ": " + e.getMessage());
                    e.printStackTrace();
                    // Mark as processed even if failed to avoid re-processing a bad request
                    insertInvalidResult(requestID, "Error: " + e.getMessage());
                    markAsProcessed(requestID);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to process matchmaking requests: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Handles Action 8: Create Room.
     * Creates a new room, gets the new RoomID, adds the user to it,
     * and returns the new RoomID in the Result table.
     */
    private void handleCreateRoom(int requestID, int userID, String roomName) throws SQLException {
        // 1. Create a new row in the Rooms table
        // We ignore the roomName (Var1) as the 'Rooms' schema provided has no column for it.
        mainStatement.executeUpdate("INSERT INTO Rooms (Name) VALUES ('" + roomName + "');");

        // 2. Get the new RoomID. We must query for it.
        int newRoomID = -1;
        ResultSet rs = queryStatement.executeQuery("SELECT RoomID FROM Rooms ORDER BY RoomID DESC LIMIT 1;");
        if (rs.next()) {
            newRoomID = rs.getInt("RoomID");
        }

        if (newRoomID == -1) {
            throw new SQLException("Could not retrieve new RoomID after creation.");
        }

        // 3. Add the creator to the PlayersInRoom table
        mainStatement.executeUpdate(
            "INSERT INTO PlayersInRoom (RoomID, UserID, Status) VALUES ("
            + newRoomID + ", " + userID + ", 0);"
        );

        // 4. Insert a valid result with the new RoomID
        insertValidResult(requestID, newRoomID, "Room created.");
        
        // 5. Mark the request as processed
        markAsProcessed(requestID);
        System.out.println("Room " + newRoomID + " created for UserID " + userID);
    }

    /**
     * Handles Action 9: Join Room.
     * Checks if the room (from Var1) has space. If yes, adds the user.
     * If no, returns an invalid result.
     */
    private void handleJoinRoom(int requestID, int userID, String var1) throws SQLException, NumberFormatException {
        int roomID = Integer.parseInt(var1); // Throws exception if Var1 is not an int

        // 1. Check how many players are in the room
        int playerCount = 0;
        ResultSet rs = queryStatement.executeQuery(
            "SELECT COUNT(*) AS playerCount FROM PlayersInRoom WHERE RoomID = " + roomID + ";"
        );
        if (rs.next()) {
            playerCount = rs.getInt("playerCount");
        }

        // 2. Check if room is full (>= 2 players)
        if (playerCount >= 2) {
            // Room is full, insert invalid result
            insertInvalidResult(requestID, "Room is full.");
            markAsProcessed(requestID);
            System.out.println("Join failed for UserID " + userID + ": Room " + roomID + " is full.");
        } else {
            // Room has space, add the player
            mainStatement.executeUpdate(
                "INSERT INTO PlayersInRoom (RoomID, UserID, Status) VALUES ("
                + roomID + ", " + userID + ", 0);"
            );

            // Insert a valid result
            insertValidResult(requestID, roomID, "Joined room.");
            markAsProcessed(requestID);
            System.out.println("UserID " + userID + " successfully joined Room " + roomID);
        }
    }

    /**
     * ** NEW METHOD **
     * Finds and deletes any rooms from the 'Rooms' table that have no corresponding
     * players in the 'PlayersInRoom' table.
     */
    public void cleanupEmptyRooms() {
        try {
            // This query deletes all rows from 'Rooms' that do not have a matching
            // 'RoomID' in the 'PlayersInRoom' table.
            String cleanupSql = "DELETE Rooms FROM Rooms " +
                                "LEFT JOIN PlayersInRoom ON Rooms.RoomID = PlayersInRoom.RoomID " +
                                "WHERE PlayersInRoom.RoomID IS NULL;";
            
            int deletedRows = mainStatement.executeUpdate(cleanupSql);

            if (deletedRows > 0) {
                System.out.println("Cleanup successful: Deleted " + deletedRows + " empty rooms.");
            }
        } catch (Exception e) {
            System.err.println("Error during empty room cleanup: " + e);
        }
    }

    // --- HELPER METHODS (from GachaHandler) ---

    /**
     * Inserts a valid (Valid=1) row into the Result table.
     */
    private void insertValidResult(int requestID, int numResult, String textResult) {
        try {
            // Basic sanitation for the text result
            String safeText = (textResult != null) ? textResult.replace("'", "''") : "";
            mainStatement.executeUpdate(
                "INSERT INTO Result (RequestID, NumResult, TextResult, Valid) VALUES (" +
                requestID + ", " + numResult + ", '" + safeText + "', 1);"
            );
        } catch (Exception e) {
            System.err.println("Failed inserting valid matchmaking result: " + e);
        }
    }

    /**
     * Inserts an invalid (Valid=0) row into the Result table.
     */
    private void insertInvalidResult(int requestID, String textResult) {
        try {
            // Basic sanitation for the text result
            String safeText = (textResult != null) ? textResult.replace("'", "''") : "";
            mainStatement.executeUpdate(
                "INSERT INTO Result (RequestID, TextResult, Valid) VALUES (" +
                requestID + ", '" + safeText + "', 0);"
            );
        } catch (Exception e) {
            System.err.println("Failed to insert invalid matchmaking result: " + e);
        }
    }

    /**
     * Marks a request as processed (Processed=1) in the Request table.
     */
    private void markAsProcessed(int requestID) {
        try {
            mainStatement.executeUpdate(
                "UPDATE Request SET Processed = 1 WHERE RequestID = " + requestID + ";"
            );
        } catch (Exception e) {
            System.err.println("Failed to mark request as processed: " + e);
        }
    }
}
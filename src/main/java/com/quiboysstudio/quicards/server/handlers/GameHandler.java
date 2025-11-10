package com.quiboysstudio.quicards.server.handlers;

import com.quiboysstudio.quicards.Composites.GameCard;
import com.quiboysstudio.quicards.strategies.BasicDMGStrategy;
import com.quiboysstudio.quicards.strategies.ICardEffectStrategy;
import com.quiboysstudio.quicards.strategies.SplitDEFStrategy;
import com.quiboysstudio.quicards.strategies.SplitDMGStrategy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Server-side handler for all in-game logic.
 * Polls for ActionID 2 (EndTurn) and 3 (PlaceCard).
 */
public class GameHandler {

    private ResultSet result;
    private final Statement mainStatement;
    private final Connection connection; // Store connection

    // Strategy factory
    private static final Map<Integer, ICardEffectStrategy> strategyMap = new HashMap<>();

    /**
     * NEW: Public static getter for the strategy map.
     * This allows RoomState to fetch the correct strategy object.
     * @param strategyID The ID from the database (1=BasicATK, 2=BasicDEF, etc.)
     * @return The corresponding ICardEffectStrategy object.
     */
    public static ICardEffectStrategy getStrategy(int strategyID) {
        // Default to BasicAttackStrategy if an unknown ID is given
        return strategyMap.getOrDefault(strategyID, new BasicDMGStrategy());
    }

    public GameHandler(ResultSet result, Statement mainStatement, Connection conn) {
        this.result = result;
        this.mainStatement = mainStatement;
        this.connection = conn; // Store the connection

        // Initialize the Strategy Map
        strategyMap.put(1, new BasicDMGStrategy());
        strategyMap.put(2, new BasicDMGStrategy()); // BasicDEF uses BasicAttack
        strategyMap.put(3, new SplitDMGStrategy());
        strategyMap.put(4, new SplitDEFStrategy());
    }

    public void checkActions() {
        processRequests(checkNewRows());
    }

    private ResultSet checkNewRows() {
        try {
            result = mainStatement.executeQuery(
                "SELECT * FROM Request WHERE Processed = 0 AND ActionID IN (2, 3);"
            );
        } catch (Exception e) {
            System.err.println("GameHandler: Failed to check Request table: " + e);
        }
        return result;
    }

    private void processRequests(ResultSet result) {
        if (result == null) return;

        try {
            while (result.next()) {
                int requestID = result.getInt("RequestID");
                int actionID = result.getInt("ActionID");
                int userID = result.getInt("UserID");
                String var1 = result.getString("Var1");
                String var2 = result.getString("Var2");

                System.out.println("GameHandler: Processing RequestID: " + requestID + " with ActionID: " + actionID);

                try {
                    // Get RoomID from UserID
                    int roomID = getRoomIDFromUserID(userID);
                    if (roomID == -1) {
                        throw new SQLException("User " + userID + " is not in a room.");
                    }

                    if (actionID == 2) {
                        handleEndTurn(requestID, roomID, userID);
                    } else if (actionID == 3) {
                        handlePlaceCard(requestID, roomID, userID, var1, var2);
                    }
                } catch (Exception e) {
                    System.err.println("GameHandler: Error processing RequestID " + requestID + ": " + e.getMessage());
                    e.printStackTrace();
                    insertInvalidResult(requestID, "Error: " + e.getMessage());
                    markAsProcessed(requestID);
                }
            }
        } catch (SQLException e) {
            System.err.println("GameHandler: Failed to process requests: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Handles Action 3: Place Card.
     * Var1: OwnershipID, Var2: SlotName (e.g., "LeftCard")
     */
    private void handlePlaceCard(int requestID, int roomID, int userID, String var1, String var2) throws SQLException {
        int ownershipID = Integer.parseInt(var1);
        String slotName = var2; // "LeftCard", "MidCard", or "RightCard"

        // 1. Create a new entry in CardStates
        int newStateID = createCardState(roomID, ownershipID);

        // 2. Update the BoardState to place this card
        String sql = "UPDATE BoardState SET " + slotName + " = ? WHERE RoomID = ? AND UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newStateID);
            ps.setInt(2, roomID);
            ps.setInt(3, userID);
            ps.executeUpdate();
        }

        // 3. TODO: Remove card from player's hand (requires Hand table)
        
        insertValidResult(requestID, newStateID, "Card placed.");
        markAsProcessed(requestID);
    }
    
    /**
     * Handles Action 2: End Turn.
     * This is the main combat logic.
     */
    private void handleEndTurn(int requestID, int roomID, int actingUserID) throws SQLException {
        // 1. Get both players' UserIDs
        int opponentUserID = getOpponentUserID(roomID, actingUserID);
        if (opponentUserID == -1) {
            throw new SQLException("Could not find opponent in room " + roomID);
        }
        
        // 2. Load the full board state for both players
        Map<String, Integer> playerBoard = getBoardState(roomID, actingUserID);
        Map<String, Integer> opponentBoard = getBoardState(roomID, opponentUserID);
        
        // 3. Execute actions for all cards on the acting player's board
        List<GameCard> actingCards = loadCardsFromBoard(playerBoard);
        
        // --- TAUNT CHECK (SplitDEF) ---
        // Find all taunting cards on the opponent's board
        List<GameCard> tauntCards = new ArrayList<>();
        for (GameCard card : loadCardsFromBoard(opponentBoard)) {
            if (card.getStrategy() instanceof SplitDEFStrategy) {
                tauntCards.add(card);
            }
        }
        
        // --- EXECUTE ACTIONS ---
        for (GameCard card : actingCards) {
            // TODO: Implement Taunt logic.
            // If tauntCards is not empty, redirect 'BasicAttackStrategy'
            // to target one of the tauntCards instead of the card in front.
            
            card.getStrategy().execute(connection, roomID, actingUserID, card.getStateID(), playerBoard, opponentBoard);
        }
        
        // 4. TODO: Resolve combat
        // - Check health of all cards in CardStates for this room.
        // - If health <= 0, remove from BoardState (set slot to NULL)
        // - ...and move to a DiscardPile (new table?)
        
        // 5. TODO: Check for game end (player health <= 0)
        
        // 6. Update the turn
        String sql = "UPDATE BoardTurn SET Turn = Turn + 1 WHERE RoomID = ?";
         try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomID);
            ps.executeUpdate();
        }
        
        insertValidResult(requestID, roomID, "Turn ended.");
        markAsProcessed(requestID);
    }
    
    // --- Database Helper Methods ---
    
    private int getRoomIDFromUserID(int userID) throws SQLException {
        String sql = "SELECT RoomID FROM PlayersInRoom WHERE UserID = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RoomID");
                }
            }
        }
        return -1;
    }
    
    private int getOpponentUserID(int roomID, int playerUserID) throws SQLException {
        String sql = "SELECT UserID FROM PlayersInRoom WHERE RoomID = ? AND UserID != ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomID);
            ps.setInt(2, playerUserID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        }
        return -1;
    }

    /**
     * Creates a new row in CardStates and returns the new StateID.
     */
    private int createCardState(int roomID, int ownershipID) throws SQLException {
        // 1. Get base stats
        String statSql = "SELECT T1.Attack, T1.Health " +
                         "FROM CardStats T1 " +
                         "JOIN OwnedCards T2 ON T1.CardID = T2.CardID " +
                         "WHERE T2.OwnershipID = ?";
        int attack = 0, health = 0;
        try (PreparedStatement ps = connection.prepareStatement(statSql)) {
            ps.setInt(1, ownershipID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    attack = rs.getInt("Attack");
                    health = rs.getInt("Health");
                } else {
                    throw new SQLException("No stats found for OwnershipID " + ownershipID);
                }
            }
        }

        // 2. Insert into CardStates
        String insertSql = "INSERT INTO CardStates (RoomID, OwnershipID, CurrentAttack, CurrentHealth) VALUES (?, ?, ?, ?)";
        int stateID = -1;
        try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, roomID);
            ps.setInt(2, ownershipID);
            ps.setInt(3, attack);
            ps.setInt(4, health);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    stateID = rs.getInt(1); // Get the auto-generated StateID
                } else {
                    // Fallback if RETURN_GENERATED_KEYS fails
                    String sqlId = "SELECT StateID FROM CardStates WHERE OwnershipID = " + ownershipID + " AND RoomID = " + roomID + " LIMIT 1";
                    try(ResultSet rsId = mainStatement.executeQuery(sqlId)) {
                        if(rsId.next()) stateID = rsId.getInt("StateID");
                    }
                }
            }
        }
        
        if (stateID == -1) {
            throw new SQLException("Failed to create CardState and get StateID.");
        }
        return stateID;
    }
    
    /**
     * Gets the current board state for one player.
     * @return Map of ("LeftCard" -> StateID, "MidCard" -> StateID, "RightCard" -> StateID)
     */
    private Map<String, Integer> getBoardState(int roomID, int userID) throws SQLException {
        Map<String, Integer> board = new HashMap<>();
        String sql = "SELECT LeftCard, MidCard, RightCard FROM BoardState WHERE RoomID = ? AND UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomID);
            ps.setInt(2, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    board.put("LeftCard", (Integer) rs.getObject("LeftCard"));
                    board.put("MidCard", (Integer) rs.getObject("MidCard"));
                    board.put("RightCard", (Integer) rs.getObject("RightCard"));
                }
            }
        }
        return board;
    }
    
    /**
     * Loads the GameCard objects for all cards on a player's board.
     */
    private List<GameCard> loadCardsFromBoard(Map<String, Integer> board) throws SQLException {
        List<GameCard> cards = new ArrayList<>();
        for (Integer stateID : board.values()) {
            if (stateID != null) {
                cards.add(loadGameCard(stateID));
            }
        }
        return cards;
    }
    
    /**
     * Loads a single GameCard from the database using its StateID.
     */
    private GameCard loadGameCard(int stateID) throws SQLException {
        String sql = "SELECT " +
                     "T1.CurrentAttack, T1.CurrentHealth, T1.OwnershipID, " +
                     "T3.CardID, T3.Name, T3.Attack AS BaseAttack, T3.Health AS BaseHealth, T3.StrategyID " +
                     "FROM CardStates T1 " +
                     "JOIN OwnedCards T2 ON T1.OwnershipID = T2.OwnershipID " +
                     "JOIN Cards T3 ON T2.CardID = T3.CardID " + // <-- This was wrong in my head, CardStats has the StrategyID
                     "JOIN CardStats T4 ON T3.CardID = T4.CardID " + // <-- Corrected JOIN
                     "WHERE T1.StateID = ?";
                     
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, stateID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // --- MODIFIED ---
                    // Use the new public static getter
                    ICardEffectStrategy strategy = GameHandler.getStrategy(rs.getInt("StrategyID"));
                    
                    GameCard card = new GameCard(
                        rs.getInt("CardID"),
                        rs.getInt("OwnershipID"),
                        rs.getString("Name"),
                        rs.getInt("BaseAttack"),
                        rs.getInt("BaseHealth"),
                        strategy
                    );
                    card.setStateID(stateID);
                    card.setCurrentAttack(rs.getInt("CurrentAttack"));
                    card.setCurrentHealth(rs.getInt("CurrentHealth"));
                    return card;
                }
            }
        }
        throw new SQLException("Could not find card with StateID " + stateID);
    }
    
    // --- Static Helper Methods for Strategies ---
    
    public static int getCardStat(Connection conn, int cardStateID, String statName) throws SQLException {
        String sql = "SELECT " + statName + " FROM CardStates WHERE StateID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cardStateID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(statName);
                }
            }
        }
        return 0;
    }
    
    public static void dealDamageToCard(Connection conn, int targetCardStateID, int damage) throws SQLException {
        String sql = "UPDATE CardStates SET CurrentHealth = CurrentHealth - ? WHERE StateID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, damage);
            ps.setInt(2, targetCardStateID);
            ps.executeUpdate();
        }
    }
    
    public static void dealDamageToPlayer(Connection conn, int roomID, int actingUserID, int damage) throws SQLException {
        // We hit the *opponent*, so we must find the BoardState row that is NOT the actingUserID
        String sql = "UPDATE BoardState SET Health = Health - ? WHERE RoomID = ? AND UserID != ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, damage);
            ps.setInt(2, roomID);
            ps.setInt(3, actingUserID);
            ps.executeUpdate();
        }
    }

    // --- Result/Marking Methods ---
    
    private void insertValidResult(int requestID, int numResult, String textResult) {
        try {
            String safeText = (textResult != null) ? textResult.replace("'", "''") : "";
            mainStatement.executeUpdate(
                "INSERT INTO Result (RequestID, NumResult, TextResult, Valid) VALUES (" +
                requestID + ", " + numResult + ", '" + safeText + "', 1);"
            );
        } catch (Exception e) {
            System.err.println("GameHandler: Failed inserting valid result: " + e);
        }
    }

    private void insertInvalidResult(int requestID, String textResult) {
        try {
            String safeText = (textResult != null) ? textResult.replace("'", "''") : "";
            mainStatement.executeUpdate(
                "INSERT INTO Result (RequestID, TextResult, Valid) VALUES (" +
                requestID + ", '" + safeText + "', 0);"
            );
        } catch (Exception e) {
            System.err.println("GameHandler: Failed to insert invalid result: " + e);
        }
    }

    private void markAsProcessed(int requestID) {
        try {
            mainStatement.executeUpdate(
                "UPDATE Request SET Processed = 1 WHERE RequestID = " + requestID + ";"
            );
        } catch (Exception e) {
            System.err.println("GameHandler: Failed to mark request as processed: " + e);
        }
    }
}
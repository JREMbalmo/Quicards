package com.quiboysstudio.quicards.server.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GachaHandler {

    // RNG
    private final Random rng = new Random();

    // SQL
    private ResultSet result;            // main ResultSet for Request table
    private final Statement mainStatement;  // used ONLY for looping requests + updates
    private final Statement queryStatement; // used for all internal queries to avoid corruption

    public GachaHandler(ResultSet result, Statement mainStatement, Connection conn) {
        this.result = result;
        this.mainStatement = mainStatement;

        // ✅ second Statement to keep ResultSet stable
        Statement temp = null;
        try { temp = conn.createStatement(); } 
        catch (Exception e) { System.out.println("Failed to create queryStatement: " + e); }

        this.queryStatement = temp;
    }

    public void checkActions() {
        processRequests(checkNewRows());
    }

    // ✅ Fetch unprocessed requests using mainStatement ONLY
    private ResultSet checkNewRows() {
        try {
            result = mainStatement.executeQuery(
                "SELECT * FROM Request WHERE Processed = 0;"
            );
        } catch (Exception e) {
            System.out.println("Failed to check Request table: " + e);
        }
        return result;
    }

    // ✅ Main request handler
    private void processRequests(ResultSet result) {

        try {
            while (result.next()) {

                int requestID = result.getInt("RequestID");
                int actionID  = result.getInt("ActionID");
                int userID    = result.getInt("UserID"); // <-- We use this for the inventory
                String var1   = result.getString("Var1");

                if (actionID != 1) continue; // not a gacha roll

                int packID = Integer.parseInt(var1);

                // ✅ Step 1: Validate user funds
                if (!userCanAfford(userID, packID)) {
                    System.out.println("inefficient funds detected");
                    insertInvalidResult(requestID);
                    markAsProcessed(requestID);
                    System.out.println("Gacha request invalid for RequestID=" + requestID);
                    continue;
                }

                // ✅ Step 2: Deduct money
                deductMoney(userID, packID);

                // ✅ Step 3: Perform Gacha
                ArrayList<Integer> pulledCards = performGachaRolls(packID);

                // --- MODIFIED SECTION ---
                // ✅ Step 4: Insert results AND save to inventory
                System.out.println("Saving " + pulledCards.size() + " cards to inventory for UserID = " + userID);
                for (Integer cardID : pulledCards) {
                    // 1. Insert into Result table
                    insertResult(requestID, cardID); 
                    
                    // 2. Insert into OwnedCards table (new call)
                    insertCardToInventory(userID, cardID);
                }
                // --- END MODIFIED SECTION ---

                // ✅ Step 5: Mark as processed
                markAsProcessed(requestID);

                System.out.println("Gacha request processed successfully for RequestID=" + requestID);
            }

        } catch (Exception e) {
            System.out.println("Failed to process gacha request: " + e);
        }
    }

    // ✅ Check money using queryStatement
    private boolean userCanAfford(int userID, int packID) {
        try {
            ResultSet rsUser = queryStatement.executeQuery(
                "SELECT Money FROM Users WHERE UserID = " + userID + ";"
            );
            rsUser.next();
            int money = rsUser.getInt("Money");

            ResultSet rsPack = queryStatement.executeQuery(
                "SELECT Price FROM Packs WHERE PackID = " + packID + ";"
            );
            rsPack.next();
            int price = rsPack.getInt("Price");

            return money >= price;

        } catch (Exception e) {
            System.out.println("Failed checking money: " + e);
        }
        return false;
    }

    // ✅ Deducting money using queryStatement
    private void deductMoney(int userID, int packID) {
        try {
            ResultSet rsPack = queryStatement.executeQuery(
                "SELECT Price FROM Packs WHERE PackID = " + packID + ";"
            );
            rsPack.next();
            int price = rsPack.getInt("Price");

            mainStatement.executeUpdate(
                "UPDATE Users SET Money = Money - " + price +
                " WHERE UserID = " + userID + ";"
            );

        } catch (Exception e) {
            System.out.println("Failed to deduct money: " + e);
        }
    }

    // ✅ Build rarity pools with queryStatement
    private Map<String, ArrayList<Integer>> buildRarityPools(int packID) {

        Map<String, ArrayList<Integer>> pools = new HashMap<>();
        pools.put("Common", new ArrayList<>());
        pools.put("Rare", new ArrayList<>());
        pools.put("Epic", new ArrayList<>());
        pools.put("Legendary", new ArrayList<>());

        // ✅ Run ONE query that gets everything
        String sql = "SELECT P.CardID, R.Name AS RarityName " +
                     "FROM PackContents P " +
                     "LEFT JOIN CardRarity CR ON P.CardID = CR.CardID " +
                     "LEFT JOIN Rarity R ON CR.RarityID = R.RarityID " +
                     "WHERE P.PackID = " + packID + ";";

        try {
            ResultSet rs = queryStatement.executeQuery(sql);

            while (rs.next()) {
                int cardID = rs.getInt("CardID");
                String rarity = rs.getString("RarityName");

                if (rarity != null && pools.containsKey(rarity)) {
                    pools.get(rarity).add(cardID);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed building rarity pools: " + e);
        }

        return pools;
    }

    // ✅ Gacha rolls (10 cards)
    private ArrayList<Integer> performGachaRolls(int packID) {

        Map<String, ArrayList<Integer>> pools = buildRarityPools(packID);
        ArrayList<Integer> results = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String rarity = rollRarity();
            ArrayList<Integer> pool = pools.get(rarity);

            // Fallback to common if a rarity pool is empty
            if (pool == null || pool.isEmpty()) {
                pool = pools.get("Common");
            }
            
            // Final safety check if even common pool is empty
            if (pool == null || pool.isEmpty()) {
                 System.out.println("Warning: Common pool is empty! Cannot pull card.");
                 continue; // Skip this roll
            }

            int cardID = pool.get(rng.nextInt(pool.size()));
            results.add(cardID);
        }

        return results;
    }

    // ✅ RNG rarity roll
    private String rollRarity() {
        int roll = rng.nextInt(100) + 1;

        if (roll <= 5)  return "Legendary";
        if (roll <= 20) return "Epic";
        if (roll <= 50) return "Rare";
        return "Common";
    }

    // ✅ Insert valid result using mainStatement
    private void insertResult(int requestID, int cardID) {
        try {
            mainStatement.executeUpdate(
                "INSERT INTO Result (RequestID, NumResult, Valid) VALUES (" +
                requestID + ", " + cardID + ", 1);"
            );
        } catch (Exception e) {
            System.out.println("Failed inserting result: " + e);
        }
    }

    // --- NEW METHOD ---
    // ✅ Insert card into user's inventory
    private void insertCardToInventory(int userID, int cardID) {
        try {
            String sql = "INSERT INTO OwnedCards (UserID, CardID) VALUES (" 
                         + userID + ", " + cardID + ")";
            
            mainStatement.executeUpdate(sql);

        } catch (Exception e) {
            System.err.println("Error saving cardID " + cardID + " to inventory for userID " + userID + ": " + e.getMessage());
        }
    }
    // --- END NEW METHOD ---

    // ✅ Insert invalid result using mainStatement
    private void insertInvalidResult(int requestID) {
        try {
            mainStatement.executeUpdate(
                "INSERT INTO Result (RequestID, Valid) VALUES (" +
                requestID + ", 0);"
            );
        } catch (Exception e) {
            System.out.println("Failed to insert invalid result: " + e);
        }
    }

    // ✅ Mark request processed using mainStatement
    private void markAsProcessed(int requestID) {
        try {
            mainStatement.executeUpdate(
                "UPDATE Request SET Processed = 1 WHERE RequestID = " + requestID + ";"
            );
        } catch (Exception e) {
            System.out.println("Failed to mark request as processed: " + e);
        }
    }
}
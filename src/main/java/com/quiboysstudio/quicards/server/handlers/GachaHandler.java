package com.quiboysstudio.quicards.server.handlers;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GachaHandler {
    private final Random rng = new Random();
    private Statement statement;
    private ResultSet result;

    public GachaHandler(ResultSet result, Statement statement) {
        this.result = result;
        this.statement = statement;
    }

    public void checkActions() {
        processRequests(checkNewRows());
    }

    // ✅ Fetch all unprocessed requests
    private ResultSet checkNewRows() {
        try {
            result = statement.executeQuery(
                    "SELECT * FROM Request WHERE Processed = 0;"
            );
        } catch (Exception e) {
            System.out.println("Failed to check Request table: " + e);
        }
        return result;
    }

    // ✅ Handle each unprocessed request
    private void processRequests(ResultSet result) {

        try {
            while (result.next()) {

                int requestID = result.getInt("RequestID");
                int actionID  = result.getInt("ActionID");
                int userID    = result.getInt("UserID");
                String var1   = result.getString("Var1");  // PackID for gacha requests

                // Skip if not a gacha request
                if (actionID != 1) continue;

                int packID = Integer.parseInt(var1);

                // ✅ Step 1: Validate user money and pack price
                if (!userCanAfford(userID, packID)) {
                    insertInvalidResult(requestID);
                    markAsProcessed(requestID);
                    System.out.println("Gacha request invalid for RequestID=" + requestID);
                    continue;
                }

                // ✅ Step 2: Deduct money from user
                deductMoney(userID, packID);

                // ✅ Step 3: Perform Gacha Logic
                ArrayList<Integer> pulledCards = performGachaRolls(packID);

                // ✅ Step 4: Insert results into Results table
                for (Integer cardID : pulledCards) {
                    insertResult(requestID, cardID);
                }

                // ✅ Step 5: Mark request as processed
                markAsProcessed(requestID);

                System.out.println("Gacha request processed successfully for RequestID=" + requestID);
            }

        } catch (Exception e) {
            System.out.println("Failed to process gacha request: " + e);
        }
    }

    // ✅ Check user money vs pack price
    private boolean userCanAfford(int userID, int packID) {
        try {
            ResultSet rsUser = statement.executeQuery(
                    "SELECT Money FROM Users WHERE UserID = " + userID + ";"
            );
            rsUser.next();
            int money = rsUser.getInt("Money");

            ResultSet rsPack = statement.executeQuery(
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

    // ✅ Deduct price from user
    private void deductMoney(int userID, int packID) {
        try {
            ResultSet rsPack = statement.executeQuery(
                    "SELECT Price FROM Packs WHERE PackID = " + packID + ";"
            );
            rsPack.next();
            int price = rsPack.getInt("Price");

            statement.executeUpdate(
                    "UPDATE Users SET Money = Money - " + price +
                    " WHERE UserID = " + userID + ";"
            );

        } catch (Exception e) {
            System.out.println("Failed to deduct money: " + e);
        }
    }

    // ✅ Build rarity lists (Common, Rare, Epic, Legendary)
    private Map<String, ArrayList<Integer>> buildRarityPools(int packID) {
        Map<String, ArrayList<Integer>> pools = new HashMap<>();

        pools.put("Common", new ArrayList<>());
        pools.put("Rare", new ArrayList<>());
        pools.put("Epic", new ArrayList<>());
        pools.put("Legendary", new ArrayList<>());

        try {
            // Get all CardIDs in the pack
            ResultSet rs = statement.executeQuery(
                "SELECT CardID FROM PackContents WHERE PackID = " + packID + ";"
            );

            while (rs.next()) {
                int cardID = rs.getInt("CardID");

                // Get rarity
                ResultSet rsRarity = statement.executeQuery(
                        "SELECT R.Name FROM CardRarity CR " +
                        "JOIN Rarity R ON CR.RarityID = R.RarityID " +
                        "WHERE CR.CardID = " + cardID + ";"
                );

                if (rsRarity.next()) {
                    String rarity = rsRarity.getString("Name");
                    pools.get(rarity).add(cardID);
                }
            }

        } catch (Exception e) {
            System.out.println("Failed building rarity pools: " + e);
        }

        return pools;
    }

    // ✅ Perform gacha rolls (10 cards per pack)
    private ArrayList<Integer> performGachaRolls(int packID) {

        Map<String, ArrayList<Integer>> pools = buildRarityPools(packID);

        ArrayList<Integer> results = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String rarity = rollRarity();
            ArrayList<Integer> pool = pools.get(rarity);

            if (pool.isEmpty()) {
                // fallback in case rarity pool empty
                pool = pools.get("Common");
            }

            int cardID = pool.get(rng.nextInt(pool.size()));
            results.add(cardID);
        }

        return results;
    }

    // ✅ RNG Rarity Roll
    private String rollRarity() {
        int roll = rng.nextInt(100) + 1;

        if (roll <= 5)  return "Legendary";
        if (roll <= 20) return "Epic";
        if (roll <= 50) return "Rare";
        return "Common";
    }

    // ✅ Insert valid result
    private void insertResult(int requestID, int cardID) {
        try {
            statement.executeUpdate(
                    "INSERT INTO Result (RequestID, NumResult, Valid) " +
                    "VALUES (" + requestID + ", " + cardID + ", 1);"
            );
        } catch (Exception e) {
            System.out.println("Failed inserting result: " + e);
        }
    }

    // ✅ Insert invalid result (user can't afford pack)
    private void insertInvalidResult(int requestID) {
        try {
            statement.executeUpdate(
                    "INSERT INTO Result (RequestID, Valid) VALUES (" +
                    requestID + ", 0);"
            );
        } catch (Exception e) {
            System.out.println("Failed to insert invalid result: " + e);
        }
    }

    // ✅ Mark request as processed
    private void markAsProcessed(int requestID) {
        try {
            statement.executeUpdate(
                    "UPDATE Request SET Processed = 1 WHERE RequestID = " + requestID + ";"
            );
        } catch (Exception e) {
            System.out.println("Failed to mark request as processed: " + e);
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quiboysstudio.quicards.strategies;

import com.quiboysstudio.quicards.server.handlers.GameHandler;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Concrete Strategy: BasicDEF.
 * This card's behavior is identical to BasicDMG.
 * It attacks the opponent's card directly in front of it.
 * If no card is in front, it attacks the opponent's health.
 */
public class BasicDMGStrategy implements ICardEffectStrategy {
    
    @Override
    public void execute(
        Connection conn,
        int roomID,
        int actingUserID,
        int actingCardStateID,
        Map<String, Integer> playerBoard,
        Map<String, Integer> opponentBoard
    ) throws SQLException {
        
        // Find which slot this card is in
        String actingSlot = null;
        for (Map.Entry<String, Integer> entry : playerBoard.entrySet()) {
            if (entry.getValue() != null && entry.getValue() == actingCardStateID) {
                actingSlot = entry.getKey();
                break;
            }
        }

        if (actingSlot == null) {
            System.err.println("BasicDEFStrategy: Could not find acting card " + actingCardStateID + " on board.");
            return;
        }

        // Determine the opposing slot
        String targetSlot = "";
        switch (actingSlot) {
            case "LeftCard":
                targetSlot = "LeftCard";
                break;
            case "MidCard":
                targetSlot = "MidCard";
                break;
            case "RightCard":
                targetSlot = "RightCard";
                break;
        }

        Integer targetCardStateID = opponentBoard.get(targetSlot);
        int actingCardAttack = GameHandler.getCardStat(conn, actingCardStateID, "CurrentAttack");

        if (targetCardStateID != null) {
            // Card vs. Card
            System.out.println("GameHandler: Card " + actingCardStateID + " attacks card " + targetCardStateID);
            GameHandler.dealDamageToCard(conn, targetCardStateID, actingCardAttack);
        } else {
            // Card vs. Player
            System.out.println("GameHandler: Card " + actingCardStateID + " attacks opponent's health");
            GameHandler.dealDamageToPlayer(conn, roomID, actingUserID, actingCardAttack);
        }
    }
}
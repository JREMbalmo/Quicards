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
 * Concrete Strategy: SplitATK.
 * The card attacks the two diagonally adjacent opponent cards.
 */
public class SplitDMGStrategy implements ICardEffectStrategy {
    
    @Override
    public void execute(
        Connection conn,
        int roomID,
        int actingUserID,
        int actingCardStateID,
        Map<String, Integer> playerBoard,
        Map<String, Integer> opponentBoard
    ) throws SQLException {
        
        String actingSlot = null;
        for (Map.Entry<String, Integer> entry : playerBoard.entrySet()) {
            if (entry.getValue() != null && entry.getValue() == actingCardStateID) {
                actingSlot = entry.getKey();
                break;
            }
        }

        if (actingSlot == null) return; // Card not on board?

        int actingCardAttack = GameHandler.getCardStat(conn, actingCardStateID, "CurrentAttack");

        // Determine target slots
        String targetSlot1 = null;
        String targetSlot2 = null;

        switch (actingSlot) {
            case "LeftCard":
                targetSlot1 = "MidCard"; // Only one diagonal
                break;
            case "MidCard":
                targetSlot1 = "LeftCard";
                targetSlot2 = "RightCard";
                break;
            case "RightCard":
                targetSlot1 = "MidCard"; // Only one diagonal
                break;
        }

        // Attack first target
        if (targetSlot1 != null && opponentBoard.get(targetSlot1) != null) {
            int targetID = opponentBoard.get(targetSlot1);
            System.out.println("GameHandler: Card " + actingCardStateID + " (Split) attacks card " + targetID);
            GameHandler.dealDamageToCard(conn, targetID, actingCardAttack);
        }

        // Attack second target
        if (targetSlot2 != null && opponentBoard.get(targetSlot2) != null) {
            int targetID = opponentBoard.get(targetSlot2);
            System.out.println("GameHandler: Card " + actingCardStateID + " (Split) attacks card " + targetID);
            GameHandler.dealDamageToCard(conn, targetID, actingCardAttack);
        }
    }
}
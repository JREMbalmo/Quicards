/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quiboysstudio.quicards.strategies;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Concrete Strategy: SplitDEF (Taunt).
 * This strategy is passive. Its execute() method does nothing.
 * The GameHandler itself will check for the presence of this strategy
 * on the opponent's board and redirect attacks to this card.
 */
public class SplitDEFStrategy implements ICardEffectStrategy {
    
    @Override
    public void execute(
        Connection conn,
        int roomID,
        int actingUserID,
        int actingCardStateID,
        Map<String, Integer> playerBoard,
        Map<String, Integer> opponentBoard
    ) throws SQLException {
        
        // This is a passive "Taunt" ability.
        // The GameHandler will check for cards with this strategy *before*
        // executing an attack.
        System.out.println("GameHandler: Card " + actingCardStateID + " (SplitDEF) is taunting.");
        // No action is performed by the card itself.
    }
}
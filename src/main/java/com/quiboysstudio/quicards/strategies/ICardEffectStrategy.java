package com.quiboysstudio.quicards.strategies;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Strategy Pattern Interface for all card actions (e.g., attacks, abilities).
 * Each strategy defines how a card behaves when its action is executed.
 */
public interface ICardEffectStrategy {

    /**
     * Executes the card's action.
     *
     * @param conn The database connection.
     * @param roomID The ID of the room where the action is taking place.
     * @param actingUserID The UserID of the player whose card is acting.
     * @param actingCardStateID The StateID of the card performing the action.
     * @param playerBoard A map representing the player's board state (SlotName -> CardStateID).
     * @param opponentBoard A map representing the opponent's board state.
     * @throws SQLException If a database error occurs.
     */
    void execute(
        Connection conn,
        int roomID,
        int actingUserID,
        int actingCardStateID,
        Map<String, Integer> playerBoard,
        Map<String, Integer> opponentBoard
    ) throws SQLException;
}
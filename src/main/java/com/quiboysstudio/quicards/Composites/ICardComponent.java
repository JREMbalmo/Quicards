package com.quiboysstudio.quicards.Composites;

import java.util.List;

/**
 * Composite Pattern: Component Interface.
 * Defines the common interface for both individual cards (Leaves)
 * and groups of cards (Composites like Decks or Hands).
 */
public interface ICardComponent {

    /**
     * Adds a card component. (Only used by Composites).
     */
    void add(ICardComponent cardComponent);

    /**
     * Removes a card component. (Only used by Composites).
     */
    void remove(ICardComponent cardComponent);

    /**
     * Gets a list of all child cards. (Returns self in a list for Leaf).
     */
    List<GameCard> getCards();

    /**
     * Gets the total number of cards.
     */
    int getCardCount();

    /**
     * Gets a specific card by its unique OwnershipID.
     * Returns null if not found.
     */
    GameCard getCardByOwnershipID(int ownershipID);
}
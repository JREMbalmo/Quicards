/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quiboysstudio.quicards.Composites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite Pattern: Composite Class.
 * Represents a group of cards, such as a Deck, Hand, or Discard Pile.
 */
public class CardComposite implements ICardComponent {

    private List<ICardComponent> children = new ArrayList<>();
    private String name; // e.g., "Deck", "Hand"

    public CardComposite(String name) {
        this.name = name;
    }

    @Override
    public void add(ICardComponent cardComponent) {
        children.add(cardComponent);
    }

    @Override
    public void remove(ICardComponent cardComponent) {
        children.remove(cardComponent);
    }

    @Override
    public List<GameCard> getCards() {
        List<GameCard> allCards = new ArrayList<>();
        for (ICardComponent child : children) {
            allCards.addAll(child.getCards()); // Recursively get all cards
        }
        return allCards;
    }

    @Override
    public int getCardCount() {
        int count = 0;
        for (ICardComponent child : children) {
            count += child.getCardCount();
        }
        return count;
    }

    @Override
    public GameCard getCardByOwnershipID(int ownershipID) {
        for (ICardComponent child : children) {
            GameCard found = child.getCardByOwnershipID(ownershipID);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
    
    /**
     * Draws the top card from this composite (e.g., from a Deck).
     * @return The card, or null if empty.
     */
    public GameCard drawCard() {
        if (children.isEmpty()) {
            return null;
        }
        // Assumes children are GameCards, which is true for a Deck
        return (GameCard) children.remove(0); 
    }
    
    /**
     * Shuffles the cards in this composite.
     */
    public void shuffle() {
        Collections.shuffle(children);
    }
}
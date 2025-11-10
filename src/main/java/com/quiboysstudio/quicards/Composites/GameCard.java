package com.quiboysstudio.quicards.Composites;

import com.quiboysstudio.quicards.strategies.ICardEffectStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite Pattern: Leaf Class.
 * Represents a single, physical card in the game.
 * It holds its database IDs and its current strategy.
 */
public class GameCard implements ICardComponent {

    // Base stats (from Cards table)
    private final int cardID;
    private final String name;
    private final int baseAttack;
    private final int baseHealth;

    // Instance stats (from OwnedCards and CardStates)
    private final int ownershipID;
    private int stateID; // The ID in CardStates
    private int currentAttack;
    private int currentHealth;

    // Strategy Pattern
    private ICardEffectStrategy strategy;

    public GameCard(int cardID, int ownershipID, String name, int baseAttack, int baseHealth, ICardEffectStrategy strategy) {
        this.cardID = cardID;
        this.ownershipID = ownershipID;
        this.name = name;
        this.baseAttack = baseAttack;
        this.baseHealth = baseHealth;
        this.strategy = strategy;
        
        // Initial state
        this.currentAttack = baseAttack;
        this.currentHealth = baseHealth;
    }

    // --- Getters ---
    public int getCardID() { return cardID; }
    public int getOwnershipID() { return ownershipID; }
    public String getName() { return name; }
    public int getBaseAttack() { return baseAttack; }
    public int getBaseHealth() { return baseHealth; }
    public int getCurrentAttack() { return currentAttack; }
    public int getCurrentHealth() { return currentHealth; }
    public int getStateID() { return stateID; }
    public ICardEffectStrategy getStrategy() { return strategy; }

    // --- Setters ---
    public void setCurrentAttack(int currentAttack) { this.currentAttack = currentAttack; }
    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }
    public void setStateID(int stateID) { this.stateID = stateID; }
    public void setStrategy(ICardEffectStrategy strategy) { this.strategy = strategy; }

    // --- Composite Pattern Methods (Leaf) ---

    @Override
    public void add(ICardComponent cardComponent) {
        // Cannot add to a single card
    }

    @Override
    public void remove(ICardComponent cardComponent) {
        // Cannot remove from a single card
    }

    @Override
    public List<GameCard> getCards() {
        // A leaf just returns itself in a list
        List<GameCard> list = new ArrayList<>();
        list.add(this);
        return list;
    }

    @Override
    public int getCardCount() {
        return 1;
    }
    
    @Override
    public GameCard getCardByOwnershipID(int ownershipID) {
        if (this.ownershipID == ownershipID) {
            return this;
        }
        return null;
    }
}
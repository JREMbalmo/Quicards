package com.quiboysstudio.quicards.prototype;

/**
 *
 * @author Andrew
 */
public class Card implements Cloneable {
    private String name;
    private String imagePath;
    private int attack;
    private int defense;
    private String rarity;
    
    public Card(String name, String imagePath, int attack, int defense, String rarity) {
        this.name = name;
        this.imagePath = imagePath;
        this.attack = attack;
        this.defense = defense;
        this.rarity = rarity;
    }
    
    /**
     * Creates a deep copy of this card.
     * This is the core of the Prototype pattern.
     */
    @Override
    public Card clone() {
        try {
            Card cloned = (Card) super.clone();
            // For deep copy of any mutable objects, clone them too
            cloned.name = new String(this.name);
            cloned.imagePath = new String(this.imagePath);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public String getRarity() { return rarity; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    
    @Override
    public String toString() {
        return name + " [" + rarity + "] ATK:" + attack + " DEF:" + defense;
    }
}

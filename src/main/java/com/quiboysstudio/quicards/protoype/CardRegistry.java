package com.quiboysstudio.quicards.prototype;

import java.io.File;
import java.util.*;

public class CardRegistry {
    private static CardRegistry instance;
    private Map<String, Card> cardPrototypes = new HashMap<>();
    
    private CardRegistry() {
        // Load all card prototypes from the card database/files
        loadCardPrototypes();
    }
    
    public static CardRegistry getInstance() {
        if (instance == null) {
            instance = new CardRegistry();
        }
        return instance;
    }
    
    /**
     * Loads all card prototypes. Each card file becomes a prototype.
     */
    private void loadCardPrototypes() {
        File cardsDir = new File("resources/cards/Fantasy Card Pack/");
        File[] cardFiles = cardsDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));
        
        if (cardFiles != null) {
            for (File file : cardFiles) {
                String fileName = file.getName();
                String cardName = parseCardName(fileName);
                String rarity = parseRarity(fileName);
                
                // Create prototype with default stats (you can load from DB if available)
                Card prototype = new Card(
                    cardName,
                    "resources/cards/Fantasy Card Pack/" + fileName,
                    getDefaultAttack(rarity),
                    getDefaultDefense(rarity),
                    rarity
                );
                
                cardPrototypes.put(fileName, prototype);
            }
        }
    }
    
    /**
     * Gets a clone of a card prototype by filename.
     */
    public Card getCard(String fileName) {
        // Remove unique ID suffix if present
        String baseFileName = fileName.split("#")[0];
        
        Card prototype = cardPrototypes.get(baseFileName);
        if (prototype != null) {
            return prototype.clone(); // Return a clone, not the original
        }
        
        System.err.println("Card prototype not found: " + baseFileName);
        return null;
    }
    
    /**
     * Checks if a card prototype exists.
     */
    public boolean hasCard(String fileName) {
        String baseFileName = fileName.split("#")[0];
        return cardPrototypes.containsKey(baseFileName);
    }
    
    private String parseCardName(String fileName) {
        try {
            String displayName = fileName.substring(0, fileName.lastIndexOf('.'));
            String[] parts = displayName.split(" - ");
            return parts.length > 1 ? parts[1] : displayName;
        } catch (Exception e) {
            return fileName;
        }
    }
    
    private String parseRarity(String fileName) {
        if (fileName.startsWith("Common")) return "Common";
        if (fileName.startsWith("Rare")) return "Rare";
        if (fileName.startsWith("Epic")) return "Epic";
        if (fileName.startsWith("Legendary")) return "Legendary";
        return "Common";
    }
    
    private int getDefaultAttack(String rarity) {
        switch (rarity) {
            case "Common": return 100;
            case "Rare": return 200;
            case "Epic": return 300;
            case "Legendary": return 500;
            default: return 100;
        }
    }
    
    private int getDefaultDefense(String rarity) {
        switch (rarity) {
            case "Common": return 80;
            case "Rare": return 150;
            case "Epic": return 250;
            case "Legendary": return 400;
            default: return 80;
        }
    }
}

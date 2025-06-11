/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company.my.safarigame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the market system in the safari game.
 * <p>
 * The Market class manages tradeable items that can be bought and sold by players.
 * It maintains a list of available items and facilitates transactions between
 * players and the market. Any object that implements the TradeableItem interface
 * can be traded in the market.
 * </p>
 */
public class Market {
    /** The list of items currently available for purchase in the market. */
    private List<TradeableItem> availableItems;
    
    /**
     * Constructs a new Market with an empty inventory of items.
     * <p>
     * The market starts with no available items, but items can be added
     * programmatically or through game events.
     * </p>
     */
    public Market() {
        availableItems = new ArrayList<>();
        // You can preload some sample items here if needed
    }
    
    /**
     * Gets a list of all items currently available in the market.
     *
     * @return The list of tradeable items available for purchase
     */
    public List<TradeableItem> listAvailableItems() {
        return availableItems;
    }
    
    /**
     * Facilitates a player buying an item from the market.
     * <p>
     * This method delegates the transaction to the player's buyItem method,
     * which should handle the financial aspects and inventory updates.
     * </p>
     *
     * @param item The item being purchased
     * @param player The player making the purchase
     */
    public void buy(TradeableItem item, Player player) {
        player.buyItem(item);
    }
    
    /**
     * Facilitates a player selling an item to the market.
     * <p>
     * This method delegates the transaction to the player's sellItem method,
     * which should handle the financial aspects and inventory updates.
     * </p>
     *
     * @param item The item being sold
     * @param player The player making the sale
     */
    public void sell(TradeableItem item, Player player) {
        player.sellItem(item);
    }
    
    /**
     * Adds a new item to the market's available inventory.
     * <p>
     * This method can be used to stock the market with new items for players to purchase.
     * </p>
     *
     * @param item The tradeable item to add to the market
     */
    public void addItem(TradeableItem item) {
        availableItems.add(item);
    }
    
    /**
     * Removes an item from the market's available inventory.
     * <p>
     * This method can be used when an item is purchased or when it should no longer
     * be available for other reasons.
     * </p>
     *
     * @param item The tradeable item to remove from the market
     */
    public void removeItem(TradeableItem item) {
        availableItems.remove(item);
    }
}
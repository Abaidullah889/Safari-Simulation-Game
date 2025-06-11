/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company.my.safarigame.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a player in the safari game.
 * <p>
 * The Player class manages player information such as name, financial capital,
 * and inventory of owned items. It provides methods for buying and selling items,
 * managing the inventory, and tracking the player's financial status.
 * </p>
 * <p>
 * Players can engage in the game economy by purchasing tradeable items from the market
 * and selling items they own. Their financial capital limits what they can buy,
 * and their inventory represents what they currently own.
 * </p>
 */
public class Player {
    /** The player's name. */
    private String name;
    
    /** The player's financial capital (money). */
    private double capital;
    
    /** The list of tradeable items owned by the player. */
    private List<TradeableItem> inventory;
    
    /**
     * Constructs a new Player with the specified name.
     * <p>
     * The player starts with a default capital of 1000 and an empty inventory.
     * </p>
     *
     * @param name The name of the player
     */
    public Player(String name) {
        this.name = name;
        this.capital = 1000;
        this.inventory = new ArrayList<>();
    }
    
    /**
     * Allows the player to buy an item.
     * <p>
     * If the player has sufficient capital, the item's price is deducted from
     * the player's capital and the item is added to their inventory.
     * </p>
     *
     * @param item The tradeable item to buy
     */
    public void buyItem(TradeableItem item) {
        if (capital >= item.getPrice()) {
            capital -= item.getPrice();
            inventory.add(item);
        }
    }
    
    /**
     * Allows the player to sell an item.
     * <p>
     * If the player owns the item, it is removed from their inventory and
     * its price is added to the player's capital.
     * </p>
     *
     * @param item The tradeable item to sell
     */
    public void sellItem(TradeableItem item) {
        if (inventory.contains(item)) {
            capital += item.getPrice();
            inventory.remove(item);
        }
    }
    
    /**
     * Updates the player's capital by the specified amount.
     * <p>
     * This can be used to add or subtract money from the player's capital.
     * Positive values increase capital, while negative values decrease it.
     * </p>
     *
     * @param amount The amount to add to (or subtract from) the player's capital
     */
    public void updateCapital(double amount) {
        capital += amount;
    }
    
    /**
     * Gets the player's current financial capital.
     *
     * @return The player's capital (money)
     */
    public double getCapital() {
        return capital;
    }
    
    /**
     * Gets the player's name.
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the player's name.
     *
     * @param name The new name for the player
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the player's inventory of owned items.
     *
     * @return The list of tradeable items in the player's inventory
     */
    public List<TradeableItem> getInventory() {
        return inventory;
    }
    
    /**
     * Removes a specific item from the player's inventory.
     * <p>
     * This method searches for an item in the inventory that matches the class
     * and description of the specified item, and removes the first matching item found.
     * </p>
     *
     * @param itemToRemove The item to remove from the inventory
     * @return true if an item was removed, false if no matching item was found
     */
    public boolean removeInventoryItem(TradeableItem itemToRemove) {
        // Find and remove the first matching item
        Iterator<TradeableItem> iterator = inventory.iterator();
        while (iterator.hasNext()) {
            TradeableItem item = iterator.next();
            // Check if the items are of the same class and have the same description
            if (item.getClass().equals(itemToRemove.getClass()) && 
                item.getDescription().equals(itemToRemove.getDescription())) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds an item to the player's inventory.
     * <p>
     * This method can be used to directly add items to the inventory
     * without going through the buying process (e.g., for quest rewards,
     * starting items, or gifts).
     * </p>
     *
     * @param item The tradeable item to add to the inventory
     */
    public void addItemToInventory(TradeableItem item) {
        inventory.add(item);
    }
}
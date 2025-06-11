/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package my.company.my.safarigame.model;

/**
 * Interface for items that can be traded in the safari game.
 * <p>
 * This interface defines the contract for objects that can be bought and sold
 * through the game's market system. Any class that implements this interface
 * can be traded by players, allowing for a diverse economy within the game.
 * Tradeable items include animals, plants, roads, jeeps, rangers, and various
 * other objects that can be placed or used in the safari.
 * </p>
 * <p>
 * Classes implementing this interface must provide methods to determine the
 * item's price and a descriptive string that identifies the item.
 * </p>
 */
public interface TradeableItem {
    
    /**
     * Gets the price of the item when traded.
     * <p>
     * This method returns the monetary value of the item, which is used
     * when buying or selling the item through the market system. Different
     * types of items may have different pricing strategies or fixed prices.
     * </p>
     *
     * @return The price of the item as a double value
     */
    double getPrice();
    
    /**
     * Gets a descriptive string for the item.
     * <p>
     * This method returns a human-readable description of the item, which
     * can be displayed in the user interface or market listings. The description
     * typically includes the type or name of the item.
     * </p>
     *
     * @return A string describing the item
     */
    String getDescription();
}
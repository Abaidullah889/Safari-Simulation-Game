/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company.my.safarigame.model;

/**
 * Represents a plant in the safari game.
 * <p>
 * Plants are landscape objects that can be placed in the game world, serving as
 * decoration, barriers, or food sources for herbivores. They have properties like
 * growth stage and health that change over time. Plants can be traded in the game
 * economy through the market system.
 * </p>
 * <p>
 * Different types of plants (species) can have different appearances and prices.
 * Common plant species in the game include "bush", "plant", and "shrub".
 * </p>
 */
public class Plant extends LandScapeObject {
    /** The price of the plant when traded. */
    private double price;
    
    /** The current growth stage of the plant, which increases over time. */
    private int growthStage;
    
    /** The health of the plant, which decreases over time or when eaten. */
    private int health;
    
    /** The species of the plant (e.g., "bush", "plant", "shrub"). */
    private String species;
    
    /**
     * Constructs a new Plant at the specified position with the given species and price.
     * <p>
     * The plant starts at growth stage 0 with full health (100).
     * </p>
     *
     * @param position The coordinate position of the plant in the game world
     * @param species The species of the plant
     * @param price The price of the plant when traded
     */
    public Plant(Coordinate position, String species, double price) {
        super(position, "Plant");
        this.species = species;
        this.price = price;
        this.growthStage = 0;
        this.health = 100;
    }
    
    /**
     * Increments the plant's growth stage and slightly decreases its health.
     * <p>
     * This method simulates the natural aging process of the plant, where it
     * grows larger but also gradually loses health over time.
     * </p>
     */
    public void grow() {
        growthStage++;
        health -= 1;
    }
    
    /**
     * Updates the plant's state during each game cycle.
     * <p>
     * This implementation calls the grow method to advance the plant's
     * growth stage and decrease its health.
     * </p>
     */
    @Override
    public void update() {
        grow();
    }
    
    /**
     * Gets the price of the plant when traded.
     * <p>
     * Implemented from the TradeableItem interface via LandScapeObject.
     * </p>
     *
     * @return The price of the plant
     */
    @Override
    public double getPrice() {
        return price;
    }
    
    /**
     * Gets a description of the plant, which is its species name.
     * <p>
     * Implemented from the TradeableItem interface via LandScapeObject.
     * </p>
     *
     * @return The species name of the plant
     */
    @Override
    public String getDescription() {
        return species;
    }
}
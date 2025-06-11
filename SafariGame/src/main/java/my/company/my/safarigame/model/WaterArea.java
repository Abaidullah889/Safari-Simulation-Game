/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company.my.safarigame.model;

/**
 * Represents a water area in the safari game.
 * <p>
 * Water areas are landscape objects that provide drinking sources for animals.
 * They have a capacity that can increase through filling (e.g., from rainfall)
 * and decrease through evaporation. Animals can visit water areas to reduce their
 * thirst. As a landscape object, water areas can be placed in the game world and
 * potentially traded, though their tradability might be limited in actual gameplay.
 * </p>
 */
public class WaterArea extends LandScapeObject {
    /** The water capacity of this water area. */
    private double capacity;
    
    /**
     * Constructs a new WaterArea at the specified position with the given capacity.
     *
     * @param position The coordinate position of the water area
     * @param capacity The initial water capacity
     */
    public WaterArea(Coordinate position, double capacity) {
        super(position, "WaterArea");
        this.capacity = capacity;
    }
    
    /**
     * Increases the water capacity, simulating rainfall or other water sources.
     * <p>
     * This method adds 10 units to the water area's capacity.
     * </p>
     */
    public void fill() {
        capacity += 10;
    }
    
    /**
     * Decreases the water capacity, simulating natural evaporation.
     * <p>
     * This method reduces the water area's capacity by 5 units.
     * </p>
     */
    public void evaporate() {
        capacity -= 5;
    }
    
    /**
     * Updates the water area's state during each game cycle.
     * <p>
     * This implementation calls the evaporate method to gradually
     * reduce the water capacity over time, simulating natural water loss.
     * </p>
     */
    @Override
    public void update() {
        evaporate();
    }
    
    /**
     * Gets the price of the water area when traded.
     * <p>
     * Implemented from the TradeableItem interface via LandScapeObject.
     * Note that water areas might not be intended to be tradable in actual
     * gameplay, as indicated by the comment.
     * </p>
     *
     * @return The price of the water area (80)
     */
    @Override
    public double getPrice() {
        return 80; // Not tradable maybe
    }
    
    /**
     * Gets a description of the water area, including its capacity.
     * <p>
     * Implemented from the TradeableItem interface via LandScapeObject.
     * </p>
     *
     * @return A string describing the water area and its capacity
     */
    @Override
    public String getDescription() {
        return "WaterArea with capacity: " + capacity;
    }
}
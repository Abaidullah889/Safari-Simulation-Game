package my.company.my.safarigame.model;

/**
 * Represents a road in the safari game.
 * <p>
 * Roads are landscape objects that can be placed in the game world to create
 * pathways for vehicles like jeeps. They have properties such as length, price,
 * and type (orientation or style). Roads can connect different points in the safari
 * and can be traded through the game's market system.
 * </p>
 * <p>
 * Common road types include "horizontal", "vertical", and various curved sections
 * like "rightDown", "rightUp", "leftUp", and "leftDown".
 * </p>
 */
public class Road extends LandScapeObject {
    /** The price of the road when traded. */
    private double price;
    
    /** The length of the road. */
    private double length;
    
    /** 
     * The type of road, which can indicate its orientation or style.
     * Common types include "horizontal", "vertical", and curved sections.
     */
    private String type;
    
    /**
     * Constructs a new Road at the specified position with the given properties.
     *
     * @param position The coordinate position of the road in the game world
     * @param length The length of the road
     * @param price The price of the road when traded
     * @param type The type of road (e.g., "horizontal", "vertical", "rightDown", etc.)
     */
    public Road(Coordinate position, double length, double price, String type) {
        super(position, "Road");
        this.length = length;
        this.price = price;
        
        // Set road type
        this.type = type;
    }
    
    /**
     * Connects two points with a road and calculates its length.
     * <p>
     * This method calculates the Euclidean distance between the start and end
     * coordinates and sets that as the road's length.
     * </p>
     *
     * @param start The starting coordinate of the road
     * @param end The ending coordinate of the road
     */
    public void connectPoints(Coordinate start, Coordinate end) {
        // Logic for road connection
        // Can calculate length based on start and end coordinates
        double roadLength = Math.sqrt(
            Math.pow(end.x - start.x, 2) + 
            Math.pow(end.y - start.y, 2)
        );
        this.length = roadLength;
    }
    
    /**
     * Updates the road's state during each game cycle.
     * <p>
     * This implementation is empty as roads typically don't need frequent updates,
     * but the method is required by the LandScapeObject abstract class.
     * </p>
     */
    @Override
    public void update() {
        // Roads may not need frequent updates
    }
    
    /**
     * Gets the price of the road when traded.
     * <p>
     * Implemented from the TradeableItem interface via LandScapeObject.
     * </p>
     *
     * @return The price of the road
     */
    @Override
    public double getPrice() {
        return price;
    }
    
    /**
     * Gets a description of the road, which is its type.
     * <p>
     * Implemented from the TradeableItem interface via LandScapeObject.
     * </p>
     *
     * @return The type of the road (e.g., "horizontal", "vertical", etc.)
     */
    @Override
    public String getDescription() {
        return type;
    }
    
    /**
     * Gets the type of the road.
     *
     * @return The road type string (e.g., "horizontal", "vertical", etc.)
     */
    public String getRoadType() {
        return type;
    }
}
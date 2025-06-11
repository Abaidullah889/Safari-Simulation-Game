package my.company.my.safarigame.model;

import java.util.*;

/**
 * Represents a tourist jeep in the safari game.
 * <p>
 * Jeeps are vehicles that can transport tourists around the safari. They have a 
 * fixed capacity for passengers and can pick up and drop off tourists at different 
 * locations. Jeeps implement the TradeableItem interface, allowing them to be 
 * purchased and sold within the game economy.
 * </p>
 */
public class Jeep implements TradeableItem {
    /** The price of the jeep when traded. */
    private final double price = 300;
    
    /** The maximum number of tourists the jeep can carry. */
    private final int capacity = 4;
    
    /** The list of tourists currently riding in the jeep. */
    private List<Tourist> currentPassengers;
    
    /** The current position of the jeep in the game world. */
    private Coordinate position;
    
    /** The previous position of the jeep, used for movement tracking. */
    private Coordinate lastPosition;

    /**
     * Constructs a new Jeep at the specified position.
     * <p>
     * Initializes the jeep with an empty list of passengers and sets both the
     * current and last position to the provided coordinate.
     * </p>
     *
     * @param position The initial position of the jeep
     */
    public Jeep(Coordinate position) {
        this.position = position;
        this.currentPassengers = new ArrayList<>();
        this.lastPosition = new Coordinate(position.getX(), position.getY());  // Initialize last position
    }

    /**
     * Moves the jeep to a new destination.
     * <p>
     * Updates the jeep's position and stores the previous position for
     * movement tracking and animation purposes.
     * </p>
     *
     * @param destination The coordinate to move the jeep to
     */
    public void moveTo(Coordinate destination) {
        this.lastPosition = new Coordinate(position.getX(), position.getY()); // Store current position as last
        this.position = destination;
    }

    /**
     * Picks up a tourist and adds them to the jeep's passenger list.
     * <p>
     * The tourist is only added if the jeep has not reached its maximum capacity.
     * </p>
     *
     * @param t The tourist to pick up
     */
    public void pickUp(Tourist t) {
        if (currentPassengers.size() < capacity) {
            currentPassengers.add(t);
        }
    }

    /**
     * Drops off a tourist, removing them from the jeep's passenger list.
     *
     * @param t The tourist to drop off
     */
    public void dropOff(Tourist t) {
        currentPassengers.remove(t);
    }

    /**
     * Gets the price of the jeep when traded.
     * <p>
     * Implemented from the TradeableItem interface.
     * </p>
     *
     * @return The price of the jeep
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * Gets a description of the jeep.
     * <p>
     * Implemented from the TradeableItem interface.
     * </p>
     *
     * @return A description string of the jeep
     */
    @Override
    public String getDescription() {
        return "Tourist Jeep";
    }

    /**
     * Gets the current position of the jeep.
     *
     * @return The coordinate representing the jeep's current position
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Gets the last position of the jeep.
     * <p>
     * This is useful for movement tracking and animation purposes.
     * </p>
     *
     * @return The coordinate representing the jeep's previous position
     */
    public Coordinate getLastPosition() {
        return lastPosition;
    }

    /**
     * Gets the maximum passenger capacity of the jeep.
     *
     * @return The number of tourists the jeep can carry
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the list of tourists currently riding in the jeep.
     *
     * @return The list of current passengers
     */
    public List<Tourist> getCurrentPassengers() {
        return currentPassengers;
    }

    /**
     * Sets the position of the jeep to the specified coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public void setPosition(int x, int y) {
        this.position = new Coordinate(x, y);
    }

    /**
     * Sets the last position of the jeep manually.
     * <p>
     * This can be useful when initializing the jeep or when special movement
     * patterns are needed.
     * </p>
     *
     * @param lastPosition The coordinate to set as the jeep's last position
     */
    public void setLastPosition(Coordinate lastPosition) {
        this.lastPosition = lastPosition;
    }
}
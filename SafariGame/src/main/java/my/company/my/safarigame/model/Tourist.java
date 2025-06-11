/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company.my.safarigame.model;

/**
 * Represents a tourist visiting the safari.
 * <p>
 * Tourists are visitors to the safari who have a satisfaction level that changes
 * based on their experiences. They can ride in jeeps to travel around the safari
 * and have destinations they wish to visit. Tourist satisfaction is an important
 * metric for the player's success in managing the safari.
 * </p>
 *
 * @author Muhammad Eman Aftab
 */
public class Tourist {
    /** The satisfaction level of the tourist (0-100). */
    public int satisfaction;
    
    /** The destination coordinate the tourist wants to visit. */
    private Coordinate destination;
    
    /** Flag indicating if the tourist is currently riding in a jeep. */
    private boolean isRiding;
    
    /** The current position of the tourist in the safari. */
    private Coordinate position;
    
    /**
     * Constructs a new Tourist with the specified destination.
     * <p>
     * The tourist is initialized with maximum satisfaction (100), 
     * positioned at their destination, and not currently riding in a jeep.
     * </p>
     *
     * @param destination The coordinate destination the tourist wants to visit
     */
    public Tourist(Coordinate destination) {
        this.destination = destination;
        this.position = destination; // Initially at destination
        this.satisfaction = 100;
        this.isRiding = false;
    }
    
    /**
     * Simulates the tourist choosing a jeep to ride in.
     * <p>
     * This method contains placeholder logic for the tourist's jeep selection
     * process. In a complete implementation, it would include criteria for
     * selecting jeeps based on various factors like proximity or capacity.
     * </p>
     */
    public void chooseJeep() {
        // Selection logic placeholder
    }
    
    /**
     * Updates the tourist's satisfaction level.
     * <p>
     * Currently, this method simply decreases satisfaction by 1. In a more
     * complex implementation, it could consider factors like wildlife sightings,
     * comfort, and safety to adjust satisfaction levels.
     * </p>
     */
    public void updateSatisfaction() {
        satisfaction -= 1;
    }
    
    /**
     * Checks if the tourist is currently riding in a jeep.
     *
     * @return true if the tourist is in a jeep, false otherwise
     */
    public boolean isRiding() {
        return isRiding;
    }
    
    /**
     * Gets the current position of the tourist.
     *
     * @return The coordinate representing the tourist's position
     */
    public Coordinate getPosition() {
        return position;
    }
    
    /**
     * Sets the position of the tourist to a new coordinate.
     * <p>
     * This method would typically be called when a tourist moves,
     * either independently or as part of a jeep's movement.
     * </p>
     *
     * @param position The new coordinate position for the tourist
     */
    public void setPosition(Coordinate position) {
        this.position = position;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company.my.safarigame.model;

/**
 * Represents a 2D coordinate point in the safari game grid.
 * <p>
 * This immutable class stores x and y integer coordinates and provides
 * methods for coordinate comparison and string representation.
 * Coordinates are used throughout the game to track positions of animals,
 * cells, and other objects in the safari world.
 * </p>
 *
 * @author Muhammad Eman Aftab
 */
public class Coordinate {
    /** The x-coordinate (horizontal position). */
    public final int x;
    
    /** The y-coordinate (vertical position). */
    public final int y;
    
    /**
     * Constructs a new Coordinate with the specified x and y values.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Returns a string representation of this coordinate.
     * <p>
     * The format of the string is "(x, y)", where x and y are the coordinate values.
     * </p>
     *
     * @return A string representation of this coordinate
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    /**
     * Checks if this coordinate is equal to another object.
     * <p>
     * Two coordinates are considered equal if they have the same x and y values.
     * </p>
     *
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate coord) {
            return this.x == coord.x && this.y == coord.y;
        }
        return false;
    }
    
    /**
     * Computes a hash code for this coordinate.
     * <p>
     * The hash code is computed based on the x and y values to ensure
     * coordinates with the same values have the same hash code.
     * </p>
     *
     * @return The hash code value for this coordinate
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
    
    /**
     * Gets the x-coordinate.
     *
     * @return The x value of this coordinate
     */
    public int getX() {
        return this.x;
    }
    
    /**
     * Gets the y-coordinate.
     *
     * @return The y value of this coordinate
     */
    public int getY() {
        return this.y;
    }
}
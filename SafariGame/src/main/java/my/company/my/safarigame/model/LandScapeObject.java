/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company.my.safarigame.model;

/**
 * Abstract base class for all objects that can be placed in the safari landscape.
 * <p>
 * LandScapeObject serves as the foundation for various game entities such as animals,
 * plants, roads, and water areas. It provides common properties like position and type,
 * and defines the basic interface that all landscape objects must implement. All
 * landscape objects are also tradeable items in the game economy.
 * </p>
 * <p>
 * This class implements the TradeableItem interface, requiring concrete subclasses
 * to implement methods for pricing and description. It also declares an abstract update
 * method that subclasses must implement to handle their specific update logic.
 * </p>
 */
public abstract class LandScapeObject implements TradeableItem {
    /** The position of this object in the game world. */
    public Coordinate position;
    
    /** The type identifier for this landscape object. */
    protected String type;
    
    /**
     * Constructs a new LandScapeObject at the specified position with the given type.
     *
     * @param position The coordinate position of this object in the game world
     * @param type The type identifier for this landscape object
     */
    public LandScapeObject(Coordinate position, String type) {
        this.position = position;
        this.type = type;
    }
    
    /**
     * Gets the position of this landscape object.
     *
     * @return The coordinate representing the object's position
     */
    public Coordinate getPosition() {
        return position;
    }
    
    /**
     * Gets the type identifier of this landscape object.
     *
     * @return The type string
     */
    public String getType() {
        return type;
    }
    
    /**
     * Updates the state of this landscape object.
     * <p>
     * This abstract method must be implemented by all concrete subclasses to handle
     * their specific update logic, such as animal movement, health changes, or
     * other lifecycle events.
     * </p>
     */
    public abstract void update();
}
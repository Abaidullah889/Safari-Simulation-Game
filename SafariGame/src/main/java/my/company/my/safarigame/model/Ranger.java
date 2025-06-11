package my.company.my.safarigame.model;

/**
 * Represents a ranger in the safari game.
 * <p>
 * Rangers are specialized characters that can move around the safari and provide
 * security by eliminating threats like poachers or dangerous predators. They
 * implement the TradeableItem interface, allowing them to be hired (purchased)
 * through the game's market system.
 * </p>
 */
public class Ranger implements TradeableItem {
    /** The current position of the ranger in the game world. */
    private Coordinate position;
    
    /** The movement speed of the ranger. */
    private int speed;
    
    /** The price of hiring (purchasing) the ranger. */
    private double price;
    
    /** The name of the ranger. */
    private String name;
    
    /**
     * Constructs a new Ranger at the specified position.
     * <p>
     * The ranger is initialized with the default name "Steve", a speed of 1,
     * and a price of 200.
     * </p>
     *
     * @param position The initial position of the ranger in the game world
     */
    public Ranger(Coordinate position) {
        this.position = position;
        this.name = "Steve";
        this.speed = 1;
        this.price = 200;
    }
    
    /**
     * Moves the ranger to a new destination.
     *
     * @param destination The coordinate to move the ranger to
     */
    public void moveTo(Coordinate destination) {
        this.position = destination;
    }
    
    /**
     * Eliminates a threat in the ranger's vicinity.
     * <p>
     * This method represents the ranger's ability to remove poachers or dangerous
     * predators from the safari. The specific logic for threat elimination would
     * be implemented in this method.
     * </p>
     */
    public void eliminateThreat() {
        // Logic to remove poacher or predator
    }
    
    /**
     * Gets the current position of the ranger.
     *
     * @return The coordinate representing the ranger's position
     */
    public Coordinate getPosition() {
        return position;
    }
    
    /**
     * Sets the position of the ranger to the specified coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public void setPosition(int x, int y) {
        this.position = new Coordinate(x, y);
    }
    
    /**
     * Gets the price of hiring (purchasing) the ranger.
     * <p>
     * Implemented from the TradeableItem interface.
     * </p>
     *
     * @return The price of the ranger
     */
    @Override
    public double getPrice() {
        return price;
    }
    
    /**
     * Gets a description of the ranger, including their name.
     * <p>
     * Implemented from the TradeableItem interface.
     * </p>
     *
     * @return A string describing the ranger, prefixed with "Ranger: " and followed by their name
     */
    @Override
    public String getDescription() {
        return "Ranger: " + name;
    }
    
    /**
     * Gets the name of the ranger.
     *
     * @return The ranger's name
     */
    public String getName() {
        return name;
    }
}
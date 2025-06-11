package my.company.my.safarigame.model;

/**
 * Represents a terrain obstacle in the safari game.
 * <p>
 * Terrain obstacles are elements in the game world that can impede movement or
 * create natural barriers. Examples include rocks, fallen trees, mud pits, or
 * other environmental features. As they implement the TradeableItem interface,
 * obstacles can be purchased and placed by players to shape the safari landscape.
 * </p>
 */
public class TerrainObstacle implements TradeableItem {
    /** The position of this obstacle in the game world. */
    private Coordinate position;
    
    /** The type of obstacle (e.g., "rock", "fallen tree", "mud pit"). */
    private String obstacleType;
    
    /** The price of the obstacle when traded. */
    private double price;
    
    /**
     * Constructs a new TerrainObstacle at the specified position with the given type.
     * <p>
     * The obstacle is initialized with a default price of 30.
     * </p>
     *
     * @param position The coordinate position of the obstacle in the game world
     * @param obstacleType The type of terrain obstacle
     */
    public TerrainObstacle(Coordinate position, String obstacleType) {
        this.position = position;
        this.obstacleType = obstacleType;
        this.price = 30;
    }
    
    /**
     * Gets the position of this obstacle.
     *
     * @return The coordinate representing the obstacle's position
     */
    public Coordinate getPosition() {
        return position;
    }
    
    /**
     * Gets the type of this obstacle.
     *
     * @return The obstacle type string (e.g., "rock", "fallen tree")
     */
    public String getObstacleType() {
        return obstacleType;
    }
    
    /**
     * Gets the price of the obstacle when traded.
     * <p>
     * Implemented from the TradeableItem interface.
     * </p>
     *
     * @return The price of the obstacle
     */
    @Override
    public double getPrice() {
        return price;
    }
    
    /**
     * Gets a description of the obstacle, which is its type.
     * <p>
     * Implemented from the TradeableItem interface.
     * </p>
     *
     * @return The obstacle type as a description
     */
    @Override
    public String getDescription() {
        return obstacleType;
    }
}
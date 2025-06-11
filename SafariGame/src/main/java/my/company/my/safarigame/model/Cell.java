package my.company.my.safarigame.model;

/**
 * Represents a single cell in the safari game grid.
 * <p>
 * Each cell has a position in the grid, can be occupied or unoccupied,
 * has a specific cell type that determines its terrain, and can contain
 * a tradeable item (like an animal) on top of it.
 * </p>
 */
public class Cell {
    /** The position of this cell in the grid. */
    private Coordinate position;
    
    /** Flag indicating whether this cell is currently occupied. */
    private boolean occupied;
    
    /** 
     * Character representing the cell type/terrain. 
     * <p>Examples:</p>
     * <ul>
     *   <li>'-' - Grass (default)</li>
     *   <li>'=' - Road</li>
     *   <li>'W' - Water</li>
     *   <li>'R' - Rock</li>
     *   <li>etc.</li>
     * </ul>
     */
    public char cellType;
    
    /** The tradeable item (such as an animal) that is placed on this cell. */
    private TradeableItem objectOnTop;
    
    /**
     * Constructs a new Cell at the specified position.
     * <p>
     * By default, the cell is unoccupied, has a grass terrain type,
     * and contains no object on top.
     * </p>
     *
     * @param position The coordinate position of this cell in the grid
     */
    public Cell(Coordinate position) {
        this.position = position;
        this.occupied = false;
        this.cellType = '-'; // Default to grass
        this.objectOnTop = null;
    }
    
    /**
     * Gets the position of this cell.
     *
     * @return The coordinate position of the cell
     */
    public Coordinate getPosition() {
        return position;
    }
    
    /**
     * Checks if this cell is currently occupied.
     *
     * @return true if the cell is occupied, false otherwise
     */
    public boolean isOccupied() {
        return occupied;
    }
    
    /**
     * Sets the occupied status of this cell.
     *
     * @param occupied true to mark the cell as occupied, false to mark it as unoccupied
     */
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
    
    /**
     * Gets the terrain type of this cell.
     *
     * @return The character representing the cell's terrain type
     */
    public char getCellType() {
        return cellType;
    }
    
    /**
     * Sets the terrain type of this cell.
     *
     * @param cellType The character representing the new terrain type
     */
    public void setCellType(char cellType) {
        this.cellType = cellType;
    }
    
    /**
     * Gets the tradeable item placed on this cell.
     *
     * @return The tradeable item on this cell, or null if there is none
     */
    public TradeableItem getObjectOnTop() {
        return objectOnTop;
    }
    
    /**
     * Places a tradeable item on this cell.
     *
     * @param objectOnTop The tradeable item to place on this cell
     */
    public void setObjectOnTop(TradeableItem objectOnTop) {
        this.objectOnTop = objectOnTop;
    }
    
    /**
     * Removes any tradeable item from this cell.
     */
    public void clearObjectOnTop() {
        this.objectOnTop = null;
    }
    
    /**
     * Resets the cell to its default state.
     * <p>
     * This marks the cell as unoccupied, sets its terrain type to grass,
     * and removes any tradeable item placed on it.
     * </p>
     */
    public void reset() {
        this.occupied = false;
        this.cellType = '-'; // Default to grass
        this.objectOnTop = null;
    }
}
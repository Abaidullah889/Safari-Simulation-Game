package my.company.my.safarigame.model;

import java.io.IOException;
import java.util.*;

/**
 * Represents the 2D grid structure of the safari game world.
 * <p>
 * The Grid consists of a rectangular array of Cells, each with its own position,
 * occupation status, and terrain type. It provides methods for accessing cells,
 * getting neighbors, and managing the grid layout.
 * </p>
 */
public class Grid {
    /** The number of rows in the grid. */
    private int rows;
    
    /** The number of columns in the grid. */
    private int columns;
    
    /** The size of each cell in pixels. */
    private int cellSize;
    
    /** The 2D array of cells that make up the grid. */
    private Cell[][] cells;
    
    /**
     * Constructs a new Grid with the specified dimensions and cell size.
     * <p>
     * Initializes all cells with empty Cells at their respective coordinates.
     * </p>
     *
     * @param rows The number of rows in the grid
     * @param columns The number of columns in the grid
     * @param cellSize The size of each cell in pixels
     */
    public Grid(int rows, int columns, int cellSize) {
        this.rows = rows;
        this.columns = columns;
        this.cellSize = cellSize;
        cells = new Cell[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                cells[r][c] = new Cell(new Coordinate(r, c));
            }
        }
    }
    
    /**
     * Factory method to create a grid from a file.
     * <p>
     * Loads a grid layout from a specified file, creating cells and adding
     * objects to the provided SafariMap.
     * </p>
     *
     * @param filePath Path to the grid file
     * @param cellSize Size of each cell in pixels
     * @param map The Safari map to add objects to
     * @return A new Grid object initialized from the file
     * @throws IOException If an error occurs reading the file
     */
    public static Grid fromFile(String filePath, int cellSize, SafariMap map) throws IOException {
        return GridLoader.loadGridFromFile(filePath, map);
    }
    
    /**
     * Gets the cell at the specified row and column.
     *
     * @param row Row index of the cell
     * @param col Column index of the cell
     * @return The cell at the specified position, or null if the position is out of bounds
     */
    public Cell getCell(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < columns)
            return cells[row][col];
        return null;
    }
    
    /**
     * Checks if a cell at the specified position is occupied.
     *
     * @param row Row index of the cell
     * @param col Column index of the cell
     * @return true if the cell exists and is occupied, false otherwise
     */
    public boolean isOccupied(int row, int col) {
        Cell cell = getCell(row, col);
        return cell != null && cell.isOccupied();
    }
    
    /**
     * Gets the type of a cell at the specified location.
     *
     * @param row Row index of the cell
     * @param col Column index of the cell
     * @return Character representing the cell type, or null character if out of bounds
     */
    public char getCellType(int row, int col) {
        Cell cell = getCell(row, col);
        return cell != null ? cell.getCellType() : '\0';
    }
    
    /**
     * Gets a list of neighboring cells for the specified cell.
     * <p>
     * Returns the cells that are adjacent in the four cardinal directions
     * (north, south, east, west).
     * </p>
     *
     * @param cell The cell to find neighbors for
     * @return A list of neighboring cells that exist within the grid bounds
     */
    public List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int x = cell.getPosition().x;
        int y = cell.getPosition().y;
        int[][] directions = { {-1,0}, {1,0}, {0,-1}, {0,1} };
        for (int[] dir : directions) {
            Cell neighbor = getCell(x + dir[0], y + dir[1]);
            if (neighbor != null)
                neighbors.add(neighbor);
        }
        return neighbors;
    }
    
    /**
     * Gets the number of rows in the grid.
     *
     * @return The number of rows
     */
    public int getRows() { return rows; }
    
    /**
     * Gets the number of columns in the grid.
     *
     * @return The number of columns
     */
    public int getColumns() { return columns; }
    
    /**
     * Gets the size of each cell in pixels.
     *
     * @return The cell size
     */
    public int getCellSize() { return cellSize; }
    
    /**
     * Sets the terrain type of a cell at the specified position.
     *
     * @param row Row index of the cell
     * @param col Column index of the cell
     * @param cellType Character representing the new cell type
     */
    public void setCellType(int row, int col, char cellType) {
        Cell cell = getCell(row, col);
        if (cell != null) {
            cell.setCellType(cellType);
        }
    }
    
    /**
     * Clears a cell's occupation and resets its type to the default grass type.
     *
     * @param row Row index of the cell
     * @param col Column index of the cell
     */
    public void clearCell(int row, int col) {
        Cell cell = getCell(row, col);
        if (cell != null) {
            cell.setOccupied(false);
            cell.setCellType('-'); // Reset to default grass type
        }
    }
}
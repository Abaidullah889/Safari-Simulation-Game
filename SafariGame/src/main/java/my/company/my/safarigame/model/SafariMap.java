package my.company.my.safarigame.model;

import java.io.IOException;
import java.util.*;

/**
 * Represents the safari map in the game, containing all game objects and the grid.
 * <p>
 * SafariMap is the central container for all objects in the game world, including
 * the grid layout, landscape objects, animals, vehicles, and characters. It manages
 * the loading and initialization of the game world, and provides methods for
 * adding, removing, and updating objects within the safari.
 * </p>
 */
public class SafariMap {

    /** The grid representing the layout of the safari. */
    private Grid grid;
    
    /** List of all landscape objects in the safari (animals, plants, etc.). */
    private List<LandScapeObject> landscapeObjects;
    
    /** List of animal groups in the safari. */
    private List<AnimalGroup> animalGroups;
    
    /** List of jeeps (vehicles) in the safari. */
    private List<Jeep> jeeps;
    
    /** List of tourists visiting the safari. */
    private List<Tourist> tourists;
    
    /** List of rangers patrolling the safari. */
    private List<Ranger> rangers;
    
    /** List of terrain obstacles in the safari. */
    private List<TerrainObstacle> obstacles;

    /** The entrance coordinate of the safari. */
    private Coordinate entrance;
    
    /** The exit coordinate of the safari. */
    private Coordinate exit;

    /** Default grid file path for loading the safari layout. */
    private static final String DEFAULT_GRID_FILE = "/grids/grid1.txt";

    /** Default grid cell size in pixels. */
    private static final int DEFAULT_CELL_SIZE = 48;

    /**
     * Constructor that takes rows, columns, and cell size.
     * <p>
     * Will try to load the grid from the default file path.
     * </p>
     *
     * @param rows Number of grid rows
     * @param cols Number of grid columns
     * @param cellSize Cell size in pixels
     */
    public SafariMap(int rows, int cols, int cellSize) {
        initializeCollections();
        // Load the grid with default file path
        loadGrid(DEFAULT_GRID_FILE, cellSize, rows, cols);
    }

    /**
     * Constructor that specifies a grid file to load.
     *
     * @param gridFilePath Path to the grid file
     * @param cellSize Cell size in pixels
     */
    public SafariMap(String gridFilePath, int cellSize) {
        initializeCollections();
        // Load the grid with specified file path
        loadGrid(gridFilePath, cellSize, 50, 50); // Default to 50x50 if loading fails
    }

    /**
     * Initializes all the collection objects.
     * <p>
     * Creates empty lists for landscape objects, animal groups, jeeps,
     * tourists, rangers, and obstacles.
     * </p>
     */
    private void initializeCollections() {
        landscapeObjects = new ArrayList<>();
        animalGroups = new ArrayList<>();
        jeeps = new ArrayList<>();
        tourists = new ArrayList<>();
        rangers = new ArrayList<>();
        obstacles = new ArrayList<>();
    }

    /**
     * Loads the grid from a file or creates a default one if loading fails.
     * <p>
     * Attempts to load the grid layout from the specified file path. If loading
     * fails, a default grid is created with the specified dimensions, and default
     * boundaries are set up.
     * </p>
     *
     * @param gridFilePath Path to the grid file
     * @param cellSize Cell size in pixels
     * @param defaultRows Default number of rows if loading fails
     * @param defaultCols Default number of columns if loading fails
     */
    private void loadGrid(String gridFilePath, int cellSize, int defaultRows, int defaultCols) {
        try {
            // Try to load the grid from file
            grid = Grid.fromFile(gridFilePath, cellSize, this);
            System.out.println("Successfully loaded grid from file: " + gridFilePath);
        } catch (IOException e) {
            System.err.println("Error loading grid from file: " + e.getMessage());
            e.printStackTrace();

            // Fall back to creating a default grid
            grid = new Grid(defaultRows, defaultCols, cellSize);
            System.out.println("Using default grid.");

            // Set up default walls on the boundaries
            setupDefaultBoundaries();
        }
    }

    /**
     * Reloads the grid from a file.
     * <p>
     * This method can be used to load a different map layout during gameplay
     * or when starting a new game.
     * </p>
     *
     * @param gridFilePath Path to the grid file
     * @return True if successful, false otherwise
     */
    public boolean reloadGrid(String gridFilePath) {
        try {
            grid = Grid.fromFile(gridFilePath, DEFAULT_CELL_SIZE, this);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to reload grid: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sets up default boundary walls if the grid file couldn't be loaded.
     * <p>
     * Creates walls around the perimeter of the grid to prevent objects from
     * moving outside the boundaries.
     * </p>
     */
    private void setupDefaultBoundaries() {
        int rows = grid.getRows();
        int cols = grid.getColumns();

        // Set up walls on the boundaries
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid.getCell(r, c);

                // If on boundary, mark as wall
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    cell.setOccupied(true);
                    cell.setCellType('w');
                } else {
                    cell.setOccupied(false);
                    cell.setCellType('-');
                }
            }
        }
    }

    /**
     * Adds a landscape object to the safari.
     *
     * @param obj The landscape object to add
     */
    public void addLandscapeObject(LandScapeObject obj) {
        landscapeObjects.add(obj);
    }

    /**
     * Removes a landscape object from the safari.
     *
     * @param obj The landscape object to remove
     */
    public void removeLandscapeObject(LandScapeObject obj) {
        landscapeObjects.remove(obj);
    }

    /**
     * Adds an animal group to the safari.
     *
     * @param group The animal group to add
     */
    public void addAnimalGroup(AnimalGroup group) {
        animalGroups.add(group);
    }

    /**
     * Adds a jeep to the safari.
     *
     * @param jeep The jeep to add
     */
    public void addJeep(Jeep jeep) {
        jeeps.add(jeep);
    }

    /**
     * Adds a tourist to the safari.
     *
     * @param tourist The tourist to add
     */
    public void addTourist(Tourist tourist) {
        tourists.add(tourist);
    }

    /**
     * Updates the state of all objects in the safari.
     * <p>
     * This method is called during each game cycle to update all landscape objects,
     * remove dead animals, and update the state of jeeps and tourists. Dead animals
     * are removed from both the landscape objects list and any animal groups they
     * belong to.
     * </p>
     */
    public void updateMap() {
        // Track animals to remove
        List<LandScapeObject> objectsToRemove = new ArrayList<>();

        // Update all landscape objects
        for (LandScapeObject obj : landscapeObjects) {
            // Update the object
            obj.update();

            // Check if it's an animal that died
            if (obj instanceof Animal) {
                Animal animal = (Animal) obj;
                if (animal.isDead()) {
                    System.out.println(animal.getDescription() + " died at " + animal.getPosition());
                    objectsToRemove.add(obj);

                    // Also remove from animal groups if needed
                    for (AnimalGroup group : animalGroups) {
                        group.getAnimals().remove(animal);
                    }
                }
            }
        }

        // Remove dead objects
        landscapeObjects.removeAll(objectsToRemove);

        // Update all jeeps
        for (Jeep jeep : jeeps) {
            // Jeep update logic here
        }

        // Update tourists
        for (Tourist tourist : tourists) {
            // Tourist update logic here
        }
    }

    /**
     * Gets the grid representing the safari layout.
     *
     * @return The grid object
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Sets the entrance coordinate of the safari.
     *
     * @param coord The coordinate to set as the entrance
     */
    public void setEntrance(Coordinate coord) {
        this.entrance = coord;
    }

    /**
     * Sets the exit coordinate of the safari.
     *
     * @param coord The coordinate to set as the exit
     */
    public void setExit(Coordinate coord) {
        this.exit = coord;
    }

    /**
     * Gets the entrance coordinate of the safari.
     *
     * @return The entrance coordinate
     */
    public Coordinate getEntrance() {
        return entrance;
    }

    /**
     * Gets the exit coordinate of the safari.
     *
     * @return The exit coordinate
     */
    public Coordinate getExit() {
        return exit;
    }

    /**
     * Gets all landscape objects on the map.
     *
     * @return List of landscape objects
     */
    public List<LandScapeObject> getLandscapeObjects() {
        return landscapeObjects;
    }

    /**
     * Gets all animal groups on the map.
     *
     * @return List of animal groups
     */
    public List<AnimalGroup> getAnimalGroups() {
        return animalGroups;
    }

    /**
     * Gets all jeeps on the map.
     *
     * @return List of jeeps
     */
    public List<Jeep> getJeeps() {
        return jeeps;
    }

    /**
     * Gets all tourists on the map.
     *
     * @return List of tourists
     */
    public List<Tourist> getTourists() {
        return tourists;
    }

    /**
     * Gets all rangers on the map.
     *
     * @return List of rangers
     */
    public List<Ranger> getRangers() {
        return rangers;
    }

    /**
     * Gets all terrain obstacles on the map.
     *
     * @return List of terrain obstacles
     */
    public List<TerrainObstacle> getObstacles() {
        return obstacles;
    }

    /**
     * Adds a ranger to the map.
     *
     * @param ranger The ranger to add
     */
    public void addRanger(Ranger ranger) {
        rangers.add(ranger);
    }

    /**
     * Adds a terrain obstacle to the map.
     *
     * @param obstacle The obstacle to add
     */
    public void addObstacle(TerrainObstacle obstacle) {
        obstacles.add(obstacle);
    }

    /**
     * Sets the grid representing the safari layout.
     *
     * @param grid The new grid object
     */
    public void setGrid(Grid grid) {
        this.grid = grid;
    }
}
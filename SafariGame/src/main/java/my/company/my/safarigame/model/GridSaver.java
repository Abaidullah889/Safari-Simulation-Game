package my.company.my.safarigame.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for saving safari game grid data to text files.
 * <p>
 * The GridSaver provides functionality to save game grid layouts to various locations,
 * create backups, and visualize grids for debugging. It handles the serialization of
 * grid data into character-based text files that can be later loaded using the GridLoader.
 * </p>
 * <p>
 * This class offers several saving options:
 * </p>
 * <ul>
 *   <li>Saving to a specific file path</li>
 *   <li>Saving to the resources directory</li>
 *   <li>Creating timestamped backups</li>
 *   <li>Saving the complete game state</li>
 * </ul>
 * <p>
 * It also provides debugging capabilities through console visualization of grid data.
 * </p>
 */
public class GridSaver {

    /**
     * Saves a grid to a file at the specified path.
     * <p>
     * This method writes the grid data character by character to a text file.
     * Each row in the grid becomes a line in the file, with each cell represented
     * by its cell type character. The method ensures that parent directories
     * exist before writing the file.
     * </p>
     *
     * @param grid The grid to save
     * @param filePath The path where to save the grid file
     * @throws IOException If an error occurs writing the file
     * @throws IllegalArgumentException If the grid is null
     */
    public static void saveGridToFile(Grid grid, String filePath) throws IOException {
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null");
        }

        // Create directories if they don't exist
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        
        // Write the grid data to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int rows = grid.getRows();
            int cols = grid.getColumns();
            
            // Write each row
            for (int r = 0; r < rows; r++) {
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < cols; c++) {
                    char cellType = grid.getCellType(r, c);
                    line.append(cellType);
                }
                writer.write(line.toString());
                writer.newLine();
            }
            
            System.out.println("Grid successfully saved to: " + filePath);
        }
    }
    
    /**
     * Saves a grid to a file in the resources directory.
     * <p>
     * This method attempts to locate the resources directory in the project structure
     * and saves the grid file in a "grids" subdirectory within it. If the directory
     * doesn't exist, it will be created.
     * </p>
     *
     * @param grid The grid to save
     * @param fileName The name of the file (e.g., "mygrid.txt")
     * @throws IOException If an error occurs writing the file or if the resources directory cannot be located
     */
    public static void saveGridToResources(Grid grid, String fileName) throws IOException {
        // Get the resources directory path
        String resourcesPath = getResourcesPath();
        if (resourcesPath == null) {
            throw new IOException("Could not locate resources directory");
        }
        
        // Create the grids directory if it doesn't exist
        File gridsDir = new File(resourcesPath + "/grids");
        if (!gridsDir.exists()) {
            gridsDir.mkdirs();
        }
        
        // Save to the grids directory
        String filePath = resourcesPath + "/grids/" + fileName;
        saveGridToFile(grid, filePath);
    }
    
    /**
     * Attempts to find the resources directory in the project structure.
     * <p>
     * Checks several common locations for the resources directory and returns the first
     * path that exists and is a directory.
     * </p>
     *
     * @return Path to the resources directory or null if not found
     */
    private static String getResourcesPath() {
        // Try to locate the resources directory
        String[] possiblePaths = {
            "src/main/resources",           // Maven standard
            "resources",                    // Simple project
            "src/resources",                // Alternative location
            ".",                            // Current directory fallback
        };
        
        for (String path : possiblePaths) {
            if (new File(path).exists() && new File(path).isDirectory()) {
                return path;
            }
        }
        
        return null;
    }
    
    /**
     * Creates a backup of the current grid.
     * <p>
     * Generates a timestamped backup file in the resources directory, allowing
     * for multiple backups to be created without overwriting previous ones.
     * </p>
     *
     * @param grid The grid to backup
     * @throws IOException If an error occurs writing the file
     */
    public static void createGridBackup(Grid grid) throws IOException {
        // Create a backup with timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        saveGridToResources(grid, "grid_backup_" + timestamp + ".txt");
    }
    
    /**
     * Utility method to save the entire game state, including the grid and all objects.
     * <p>
     * Currently, this method only saves the grid data, but it could be extended to save
     * object states like animal positions, player information, etc. in a more comprehensive
     * implementation.
     * </p>
     *
     * @param map The SafariMap containing the game state
     * @param filePath The path where to save the game state
     * @throws IOException If an error occurs writing the file
     * @throws IllegalArgumentException If the SafariMap or its Grid is null
     */
    public static void saveGameState(SafariMap map, String filePath) throws IOException {
        if (map == null || map.getGrid() == null) {
            throw new IllegalArgumentException("SafariMap and Grid cannot be null");
        }
        
        // First save the grid
        Grid grid = map.getGrid();
        saveGridToFile(grid, filePath);
        
        // In a more advanced implementation, you could also save object data
        // like animal positions, player information, etc. in a separate file
        // or in a different format like JSON or XML
        System.out.println("Note: Only grid data was saved. Object states were not saved.");
    }
    
    /**
     * Prints a visual representation of the grid to the console.
     * <p>
     * This method is useful for debugging and quickly visualizing the current state
     * of a grid without having to save it to a file first.
     * </p>
     *
     * @param grid The grid to visualize
     */
    public static void printGridToConsole(Grid grid) {
        if (grid == null) {
            System.err.println("Grid is null");
            return;
        }
        
        int rows = grid.getRows();
        int cols = grid.getColumns();
        
        System.out.println("Grid visualization (" + rows + "x" + cols + "):");
        System.out.println("---------------------------------------");
        
        for (int r = 0; r < rows; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                char cellType = grid.getCellType(r, c);
                line.append(cellType).append(' ');
            }
            System.out.println(line.toString());
        }
        
        System.out.println("---------------------------------------");
    }
}
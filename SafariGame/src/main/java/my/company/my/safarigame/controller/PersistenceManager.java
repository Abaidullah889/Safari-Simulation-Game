package my.company.my.safarigame.controller;

import my.company.my.safarigame.model.Grid;
import my.company.my.safarigame.model.GridLoader;
import my.company.my.safarigame.model.GridSaver;
import my.company.my.safarigame.model.SafariGameModel;
import my.company.my.safarigame.model.SafariMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manages persistence operations for the Safari Game, including saving and loading game states.
 * <p>
 * This class serves as a central point for all persistence-related operations in the game,
 * handling the creation, loading, and management of save files. It maintains game state
 * persistence through text file operations, managing a dedicated save directory structure.
 * </p>
 * <p>
 * The persistence manager supports various operations including:
 * <ul>
 *   <li>Saving current game states with custom or auto-generated filenames</li>
 *   <li>Loading games from saved files</li>
 *   <li>Managing auto-save functionality</li>
 *   <li>Listing available save files</li>
 *   <li>Deleting save files</li>
 * </ul>
 * </p>
 * 
 * @author [Your Name]
 * @version 1.0
 * @see my.company.my.safarigame.model.SafariGameModel
 * @see my.company.my.safarigame.model.GridSaver
 * @see my.company.my.safarigame.model.GridLoader
 */
public class PersistenceManager {
    
    private SafariGameModel gameModel;
    private final String DEFAULT_SAVE_DIR = "saves";
    private final String DEFAULT_GRID_FILE = "grid1.txt";
    
    /**
     * Creates a new PersistenceManager instance.
     * <p>
     * Initializes the persistence manager with the provided game model and
     * creates the default save directory if it doesn't already exist.
     * </p>
     *
     * @param gameModel The game model to manage persistence for, must not be null
     */
    public PersistenceManager(SafariGameModel gameModel) {
        this.gameModel = gameModel;
        
        // Create saves directory if it doesn't exist
        File savesDir = new File(DEFAULT_SAVE_DIR);
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }
    }
    
    /**
     * Saves the current game state to a file.
     * <p>
     * If no filename is provided, a name will be auto-generated using the player's name
     * and current timestamp in the format: "playerName_yyyyMMdd_HHmmss.txt".
     * All save files are stored in the default save directory.
     * </p>
     *
     * @param fileName Optional file name (without extension), can be null or empty
     * @return The full path to the saved file
     * @throws IOException If an error occurs during the save operation
     * @throws IllegalStateException If the game model, map, or grid is null
     */
    public String saveGameToFile(String fileName) throws IOException {
        if (gameModel == null || gameModel.getMap() == null || gameModel.getMap().getGrid() == null) {
            throw new IllegalStateException("Game model, map or grid is null");
        }
        
        // If no filename provided, generate one with player name and timestamp
        if (fileName == null || fileName.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String playerName = (gameModel.getPlayer() != null) ? 
                gameModel.getPlayer().getName() : "unknown";
            
            fileName = playerName + "_" + timestamp;
        }
        
        // Ensure filename has .txt extension
        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }
        
        // Get full save path
        String savePath = DEFAULT_SAVE_DIR + File.separator + fileName;
        
        // Save the grid
        Grid grid = gameModel.getMap().getGrid();
        GridSaver.saveGridToFile(grid, savePath);
        
        // Log the save
        System.out.println("Game saved to: " + savePath);
        
        return savePath;
    }
    
    
    /**
     * Loads a game from a saved grid file.
     * <p>
     * Creates a new map based on the saved grid file and updates the game model.
     * </p>
     *
     * @param filePath Path to the saved grid file
     * @return {@code true} if loading was successful, {@code false} otherwise
     */
    public boolean loadGameFromFile(String filePath) {
        try {
            // Create a new map or reset the existing one
            SafariMap map = new SafariMap(filePath, 48); // 48 is the cell size
            
            // Set the map in the game model
            gameModel.setMap(map);
            
            // Log the load
            System.out.println("Game loaded from: " + filePath);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Loads the default grid.
     * <p>
     * Creates a new map with the default grid file and updates the game model.
     * This is typically used when starting a new game.
     * </p>
     *
     * @return {@code true} if loading was successful, {@code false} otherwise
     */
    public boolean loadDefaultGrid() {
        try {
            // Create a new map with the default grid
            SafariMap map = new SafariMap(DEFAULT_GRID_FILE, 48);
            
            // Set the map in the game model
            gameModel.setMap(map);
            
            System.out.println("Default grid loaded");
            
            return true;
        } catch (Exception e) {
            System.err.println("Error loading default grid: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets a list of all available save files.
     * <p>
     * Returns the absolute paths of all .txt files in the default save directory.
     * </p>
     *
     * @return List of save file paths, may be empty but never null
     */
    public List<String> getAvailableSaveFiles() {
        List<String> saveFiles = new ArrayList<>();
        
        File savesDir = new File(DEFAULT_SAVE_DIR);
        if (savesDir.exists() && savesDir.isDirectory()) {
            File[] files = savesDir.listFiles((dir, name) -> name.endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    saveFiles.add(file.getAbsolutePath());
                }
            }
        }
        
        return saveFiles;
    }
    
    /**
     * Gets a list of save file names without path or extension.
     * <p>
     * Returns just the file names (without the .txt extension) of all save files.
     * </p>
     *
     * @return List of save file names, may be empty but never null
     */
    public List<String> getSaveFileNames() {
        List<String> fileNames = new ArrayList<>();
        
        for (String filePath : getAvailableSaveFiles()) {
            String fileName = new File(filePath).getName();
            
            // Remove .txt extension
            if (fileName.endsWith(".txt")) {
                fileName = fileName.substring(0, fileName.length() - 4);
            }
            
            fileNames.add(fileName);
        }
        
        return fileNames;
    }
    
    /**
     * Checks if an auto-save file exists.
     *
     * @return {@code true} if an auto-save file exists, {@code false} otherwise
     */
    public boolean hasAutoSave() {
        File autoSaveFile = new File(DEFAULT_SAVE_DIR + File.separator + "autosave.txt");
        return autoSaveFile.exists() && autoSaveFile.isFile();
    }
    
    /**
     * Loads the auto-save file if it exists.
     *
     * @return {@code true} if the auto-save was loaded successfully, {@code false} otherwise
     */
    public boolean loadAutoSave() {
        if (hasAutoSave()) {
            return loadGameFromFile(DEFAULT_SAVE_DIR + File.separator + "autosave.txt");
        }
        return false;
    }
    
    /**
     * Deletes a save file.
     * <p>
     * The .txt extension will be added automatically if not included in the filename.
     * </p>
     *
     * @param fileName The name of the save file to delete (with or without .txt extension)
     * @return {@code true} if deletion was successful, {@code false} otherwise
     */
    public boolean deleteSaveFile(String fileName) {
        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }
        
        File fileToDelete = new File(DEFAULT_SAVE_DIR + File.separator + fileName);
        return fileToDelete.delete();
    }
}
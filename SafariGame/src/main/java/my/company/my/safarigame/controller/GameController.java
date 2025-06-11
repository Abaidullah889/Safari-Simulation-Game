package my.company.my.safarigame.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import my.company.my.safarigame.model.SafariGameModel;
import my.company.my.safarigame.model.Grid;
import my.company.my.safarigame.model.GridLoader;
import my.company.my.safarigame.model.GridSaver;
import my.company.my.safarigame.model.SafariMap;
import my.company.my.safarigame.view.GameView;
import my.company.my.safarigame.view.MapView;
import my.company.my.safarigame.view.DashboardView;
import my.company.my.safarigame.view.MiniMapView;
import my.company.my.safarigame.view.MarketView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import my.company.my.safarigame.model.Animal;
import my.company.my.safarigame.model.Carnivore;
import my.company.my.safarigame.model.Cell;
import my.company.my.safarigame.model.Coordinate;
import my.company.my.safarigame.model.Herbivore;
import my.company.my.safarigame.model.Jeep;
import my.company.my.safarigame.model.LandScapeObject;
import my.company.my.safarigame.model.Plant;
import my.company.my.safarigame.model.Ranger;
import my.company.my.safarigame.model.Road;
import my.company.my.safarigame.model.Time;
import my.company.my.safarigame.model.TradeableItem;
import my.company.my.safarigame.model.WaterArea;
import my.company.my.safarigame.view.ItemPlacementHandler;

/**
 * Controller class for the Safari Game that connects the model and view components.
 * <p>
 * This class follows the MVC (Model-View-Controller) pattern by mediating interactions
 * between the game model and various view components. It handles user inputs, game
 * logic processing, and updates the views based on changes in the model. The controller
 * is responsible for game initialization, saving/loading game state, item placement,
 * time management, and win condition checking.
 * </p>
 */
public class GameController {

    /** The main game model containing game state and logic. */
    private SafariGameModel model;
    
    /** The main game view that handles the overall UI layout. */
    private GameView gameView;
    
    /** The map view that displays the safari grid and game objects. */
    private MapView mapView;
    
    /** The dashboard view that displays game statistics and controls. */
    private DashboardView dashboardView;
    
    /** The main application frame that contains all UI components. */
    private JFrame mainFrame;
    
    /** Handler for placing items on the map. */
    private ItemPlacementHandler itemPlacementHandler;

    /** Manager for game persistence operations (saving/loading). */
    private PersistenceManager persistenceManager;
    
    /** Directory path for saved games. */
    private static final String SAVES_DIRECTORY = "saves";
    
    /** Timer for updating the game state at regular intervals. */
    private Timer gameTimer;

    /**
     * Constructs a new GameController with the specified model.
     * <p>
     * Initializes the controller, creates the game views, sets up the persistence
     * manager, and starts the game timer for regular updates. If running in a
     * graphical environment, the UI components will be initialized.
     * </p>
     *
     * @param model The SafariGameModel that contains the game state and logic
     */
    public GameController(SafariGameModel model) {
        this.model = model;

        // Initialize the persistence manager
        this.persistenceManager = new PersistenceManager(model);

        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            this.gameView = new GameView(model, this);
            if (gameView != null) {
                this.mainFrame = gameView.getMainFrame();
            }
        } else {
            System.out.println("Headless environment detected. Skipping GameView initialization.");
        }

        // Ensure saves directory exists
        createSavesDirectory();

        // Start the game timer
        startGameTimer();
    }

    /**
     * Creates the directory for saved games if it doesn't exist.
     * <p>
     * This method ensures that the application has a valid location to
     * store saved game files.
     * </p>
     */
    private void createSavesDirectory() {
        File savesDir = new File(SAVES_DIRECTORY);
        if (!savesDir.exists()) {
            boolean created = savesDir.mkdirs();
            if (created) {
                System.out.println("Created saves directory: " + savesDir.getAbsolutePath());
            } else {
                System.err.println("Failed to create saves directory: " + savesDir.getAbsolutePath());
            }
        }
    }

    /**
     * Initializes the map and dashboard views.
     * <p>
     * This method is called when the game starts to set up the main game interface.
     * It creates the map view, dashboard, connects the mini-map, and sets up the
     * item placement handler.
     * </p>
     */
    public void initializeGameViews() {
        try {
            // Get the grid from the model
            Grid grid = model.getMap().getGrid();

            // Initialize the map view with the grid from the model
            mapView = new MapView(grid, this);

            // Initialize the dashboard with player information and controller reference
            dashboardView = new DashboardView(model.getPlayer(), model.getGameSpeed(), this);

            // Set parent frame for dialogs
            if (mainFrame != null) {
                dashboardView.setParentFrame(mainFrame);
            }

            // Connect the map view with the mini map
            MiniMapView miniMapView = dashboardView.getMiniMapView();
            mapView.setMiniMapView(miniMapView);

            // Connect the map view to the dashboard
            dashboardView.setMapView(mapView);

            // Set up item placement handler
            itemPlacementHandler = new ItemPlacementHandler() {
                @Override
                public void handleItemPlacement(TradeableItem item) {
                    // Make sure this correctly enables placement mode
                    mapView.enablePlacementMode(item);
                    // Consider adding debug output here
                    System.out.println("Enabling placement mode for: " + item.getDescription());
                }
            };

            // Connect the inventory view with the placement handler
            dashboardView.setItemPlacementHandler(itemPlacementHandler);

            // Show the game screen
            gameView.showGamePanel(mapView, dashboardView);

        } catch (Exception e) {
            System.err.println("Error initializing game views: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts a new game with the specified player information.
     * <p>
     * Sets up the player, game speed, and difficulty in the model,
     * applies difficulty-specific effects, and initializes the game views.
     * </p>
     *
     * @param playerName The name of the player
     * @param gameSpeed The selected game speed (1=Hour, 2=Day, 3=Week)
     * @param difficulty The selected difficulty level ("Easy", "Medium", "Hard")
     */
    public void startGame(String playerName, int gameSpeed, String difficulty) {
        try {
            // Set the player information in the model
            model.setPlayer(playerName);
            model.setGameSpeed(gameSpeed);
            model.setGameDifficulty(difficulty);

            // Apply effects based on difficulty
            applyDifficultyEffects();

            // Change game state to running
            model.startGame();

            // Initialize game views
            initializeGameViews();

        } catch (Exception e) {
            System.err.println("Error starting game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a saved game by name.
     * <p>
     * This method delegates to loadFromGrid to load the game data.
     * </p>
     *
     * @param savedGameName Name of the saved game to load
     */
    public void loadGame(String savedGameName) {
        // Use the loadFromGrid method to load the game
        loadFromGrid(savedGameName);
    }

    /**
     * Updates game settings.
     * <p>
     * This method updates various game settings such as sound volume
     * and game speed based on user preferences.
     * </p>
     *
     * @param volume Sound volume (0-100)
     * @param gameSpeed Game speed setting (1=Hour, 2=Day, 3=Week)
     */
    public void updateSettings(int volume, int gameSpeed) {
        // Update model settings
        model.setGameSpeed(gameSpeed);

        // Any other settings would be updated here
    }

    /**
     * Opens the market view dialog.
     * <p>
     * Creates and displays the market interface where players can buy
     * and sell items for their safari.
     * </p>
     */
    public void openMarketView() {
        // Make sure model and player are initialized
        if (model == null || model.getPlayer() == null || model.getMarket() == null) {
            System.err.println("Cannot open market: Model or player not initialized");
            return;
        }

        // Create and show the market dialog
        if (mainFrame != null) {
            MarketView marketView = new MarketView(
                    mainFrame,
                    model.getMarket(),
                    model.getPlayer(),
                    this
            );
            marketView.setVisible(true);
        } else {
            System.err.println("Cannot open market: Main frame not found");
        }
    }

    /**
     * Gets the main game model.
     *
     * @return The SafariGameModel
     */
    public SafariGameModel getModel() {
        return model;
    }

    /**
     * Gets the main application frame.
     *
     * @return The JFrame containing the application UI
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Sets the main application frame.
     *
     * @param frame The JFrame to set as the main application frame
     */
    public void setMainFrame(JFrame frame) {
        this.mainFrame = frame;
    }

    /**
     * Saves the current game grid state to a file.
     * <p>
     * If no filename is provided, an auto-generated name will be created
     * based on the player name and current timestamp.
     * </p>
     *
     * @param fileName The name of the save file (optional, will be auto-generated if null)
     * @return True if saving was successful, false otherwise
     */
    public boolean saveToGrid(String fileName) {
        // Check if model and grid exist
        if (model == null || model.getMap() == null || model.getMap().getGrid() == null) {
            showErrorMessage("Cannot save: Game not initialized");
            return false;
        }

        // Generate file name if not provided
        if (fileName == null || fileName.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String playerName = model.getPlayer() != null ? model.getPlayer().getName() : "unknown";
            fileName = playerName + "_" + timestamp + ".txt";
        } else if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }

        // Ensure saves directory exists
        createSavesDirectory();

        // Get full save path
        String savePath = SAVES_DIRECTORY + File.separator + fileName;

        try {
            // Save grid to file using GridSaver
            Grid grid = model.getMap().getGrid();
            GridSaver.saveGridToFile(grid, savePath);

            System.out.println("Game grid saved to: " + savePath);

            // Show success message if there's a UI
            showSuccessMessage("Game saved successfully to: " + fileName);

            return true;
        } catch (IOException e) {
            System.err.println("Error saving game grid: " + e.getMessage());
            e.printStackTrace();

            // Show error message if there's a UI
            showErrorMessage("Error saving game: " + e.getMessage());

            return false;
        }
    }

    /**
     * Loads a game grid from a file.
     * <p>
     * This method loads a saved game from the specified file name,
     * creates a new SafariMap with the loaded grid, and updates the model
     * and views accordingly.
     * </p>
     *
     * @param fileName The name of the save file to load
     * @return True if loading was successful, false otherwise
     */
    public boolean loadFromGrid(String fileName) {
        // Check if model exists
        if (model == null) {
            showErrorMessage("Cannot load: Game model not initialized");
            return false;
        }

        // Ensure file name has .txt extension
        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }

        // Get full path
        String filePath = SAVES_DIRECTORY + File.separator + fileName;
        File saveFile = new File(filePath);

        // Check if file exists
        if (!saveFile.exists() || !saveFile.isFile()) {
            showErrorMessage("Save file not found: " + fileName);
            return false;
        }

        try {
            // Create a new SafariMap using the absolute path
            String absolutePath = saveFile.getAbsolutePath();
            System.out.println("Loading game grid from absolute path: " + absolutePath);

            // Create a new SafariMap instance with the grid file
            SafariMap map = new SafariMap(absolutePath, 48);
            Grid grid = GridLoader.loadGridFromFile(absolutePath, map);
            map.setGrid(grid);

            // Set the map in the model
            model.setMap(map);

            // Make sure player is initialized
            if (model.getPlayer() == null) {
                // If loading a game before player is set, create a default player
                model.setPlayer("Player");
                model.setGameSpeed(2); // Default to day speed
                model.setGameDifficulty("Medium"); // Default difficulty
            }

            System.out.println("Map loaded successfully, landscape objects: "
                    + (map.getLandscapeObjects() != null ? map.getLandscapeObjects().size() : "null"));

            // Reinitialize game views with the loaded map
            initializeGameViews();

            // IMPORTANT: Initialize movement for all animals
            if (mapView != null) {
                System.out.println("Initializing animal movement after load...");
                mapView.initializeAllAnimalMovement();
            } else {
                System.err.println("Warning: mapView is null, cannot initialize animal movement");
            }

            // IMPORTANT: Start the game timer which handles time advances and effects
            stopGameTimer(); // Stop any existing timer first
            startGameTimer();

            // Show success message
            showSuccessMessage("Game loaded successfully from: " + fileName);

            return true;
        } catch (Exception e) {
            System.err.println("Error loading game grid: " + e.getMessage());
            e.printStackTrace();

            showErrorMessage("Error loading game: " + e.getMessage());

            return false;
        }
    }

    /**
     * Saves the current game with an auto-generated name.
     * <p>
     * This is a convenience method that calls saveToGrid with null
     * to generate an automatic filename.
     * </p>
     *
     * @return True if saving was successful, false otherwise
     */
    public boolean saveGame() {
        return saveToGrid(null);
    }

    /**
     * Gets a list of available save files.
     * <p>
     * Searches the saves directory for .txt files and returns their names
     * without the extension.
     * </p>
     *
     * @return List of save file names (without .txt extension)
     */
    public List<String> getAvailableSaves() {
        List<String> saveFiles = new ArrayList<>();

        File savesDir = new File(SAVES_DIRECTORY);
        if (savesDir.exists() && savesDir.isDirectory()) {
            File[] files = savesDir.listFiles((dir, name) -> name.endsWith(".txt"));

            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    // Remove .txt extension
                    if (fileName.endsWith(".txt")) {
                        fileName = fileName.substring(0, fileName.length() - 4);
                    }
                    saveFiles.add(fileName);
                }
            }
        }

        return saveFiles;
    }

    /**
     * Creates an auto-save of the current game.
     * <p>
     * Saves the game with the fixed name "autosave".
     * </p>
     *
     * @return True if saving was successful, false otherwise
     */
    public boolean createAutoSave() {
        return saveToGrid("autosave");
    }

    /**
     * Shows an error message dialog if UI is available.
     *
     * @param message The error message to display
     */
    private void showErrorMessage(String message) {
        if (mainFrame != null) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Shows a success message dialog if UI is available.
     *
     * @param message The success message to display
     */
    private void showSuccessMessage(String message) {
        if (mainFrame != null) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    message,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Handles the placement of an item on the map.
     * <p>
     * Updates the cell state, sets the appropriate cell type based on the item,
     * and adds the item to the appropriate collections in the SafariMap.
     * </p>
     *
     * @param item The tradeable item being placed
     * @param row The row coordinate where the item is placed
     * @param col The column coordinate where the item is placed
     */
    public void onItemPlaced(TradeableItem item, int row, int col) {
        // Get the map and grid from the model
        SafariMap map = model.getMap();
        Grid grid = map.getGrid();

        if (map == null || grid == null) {
            System.err.println("Error: SafariMap or Grid is null");
            return;
        }

        // Debug output
        System.out.println("Controller processing item placement at (" + row + "," + col + ")");
        System.out.println("Grid state in controller - Type: " + grid.getCellType(row, col)
                + ", Occupied: " + grid.isOccupied(row, col));

        // Store the item's current position if it's a LandScapeObject
        Coordinate position = null;
        if (item instanceof LandScapeObject) {
            position = ((LandScapeObject) item).getPosition();
            System.out.println("Item position before adding to model: " + position);
        }

        // Ensure cell is marked as occupied and has the right type
        Cell cell = grid.getCell(row, col);
        if (cell != null) {
            cell.setOccupied(true);

            // Set cell type based on item type if needed
            char currentType = grid.getCellType(row, col);
            if (currentType == '-') { // Only if not already set
                if (item instanceof Plant) {
                    Plant plant = (Plant) item;
                    char cellType = 'p'; // Default type

                    if (plant.getDescription().toLowerCase().contains("bush")) {
                        cellType = 'b';
                    } else if (plant.getDescription().toLowerCase().contains("shrub")) {
                        cellType = 'h';
                    }

                    grid.setCellType(row, col, cellType);
                } else if (item instanceof Road) {
                    Road road = (Road) item;
                    char cellType = 'r'; // Default horizontal

                    String roadType = road.getRoadType().toLowerCase();
                    if (roadType.contains("vertical")) {
                        cellType = '|';
                    } else if (roadType.contains("rightdown")) {
                        cellType = '1';
                    } else if (roadType.contains("rightup")) {
                        cellType = '2';
                    } else if (roadType.contains("leftup")) {
                        cellType = '3';
                    } else if (roadType.contains("leftdown")) {
                        cellType = '4';
                    }

                    grid.setCellType(row, col, cellType);
                } else if (item instanceof WaterArea) {
                    grid.setCellType(row, col, 'P');
                } else if (item instanceof Herbivore) {
                    String species = ((Herbivore) item).getDescription().toLowerCase();
                    if (species.contains("cow")) {
                        grid.setCellType(row, col, 'c');
                    } else {
                        grid.setCellType(row, col, 'd'); // Deer
                    }
                } else if (item instanceof Carnivore) {
                    String species = ((Carnivore) item).getDescription().toLowerCase();
                    if (species.contains("lion")) {
                        grid.setCellType(row, col, 'y');
                    } else {
                        grid.setCellType(row, col, 'z'); // Wolf
                    }
                } else if (item instanceof Ranger) {
                    grid.setCellType(row, col, 'R');
                } else if (item instanceof Jeep) {
                    grid.setCellType(row, col, 'j');
                }
            }
        }

        // Directly add the item to the model's landscape objects list
        if (item instanceof LandScapeObject) {
            // Check if item's position is correct, fix if needed
            LandScapeObject lsObject = (LandScapeObject) item;
            Coordinate currentPos = lsObject.getPosition();

            // Special case for Jeep with fixed position
            if (item instanceof Jeep) {
                int jeepRow = 48;
                int jeepCol = 7;
                // Only update if position is wrong
                if (currentPos == null || currentPos.x != jeepRow || currentPos.y != jeepCol) {
                    // Jeeps have setPosition method
                    ((Jeep) item).setPosition(jeepRow, jeepCol);
                    System.out.println("Updated Jeep position to fixed location: (48,7)");
                }

                // Add to the jeep collection
                map.addJeep((Jeep) item);
            } // For other landscape objects
            else {
                // Only update if position is incorrect
                if (currentPos == null || currentPos.x != row || currentPos.y != col) {
                    // Set position based on object type
                    if (item instanceof Animal) {
                        ((Animal) item).setPosition(row, col);
                        System.out.println("Updated Animal position to: (" + row + "," + col + ")");
                    } else {
                        // For other objects, update position field directly if accessible
                        try {
                            lsObject.position = new Coordinate(row, col);
                            System.out.println("Updated LandScapeObject position to: (" + row + "," + col + ")");
                        } catch (Exception e) {
                            System.err.println("Failed to update position: " + e.getMessage());
                        }
                    }
                }

                // Add to specific collections based on type
                if (item instanceof Ranger) {
                    map.addRanger((Ranger) item);
                }
            }

            // Add to general landscape objects list
            map.addLandscapeObject(lsObject);
            System.out.println("Added item to landscape objects: " + lsObject.getClass().getSimpleName());
        }

        // Print final grid state
        System.out.println("Final grid state at (" + row + "," + col + "): Type="
                + grid.getCellType(row, col) + ", Occupied=" + grid.isOccupied(row, col));
    }

    /**
     * Handles the removal of an item from the map.
     * <p>
     * Removes the landscape object at the specified coordinates from
     * the SafariMap.
     * </p>
     *
     * @param row The row coordinate of the item to remove
     * @param col The column coordinate of the item to remove
     */
    public void onItemRemoved(int row, int col) {
        if (model != null && model.getMap() != null) {
            // Remove the landscape object from the map at the specified coordinates
            SafariMap map = model.getMap();

            // Remove landscape objects at the specified position
            map.getLandscapeObjects().removeIf(obj
                    -> obj.getPosition().x == row && obj.getPosition().y == col
            );

            // Optional: You might want to update any other game state or UI
            System.out.println("Item removed from map at (" + row + ", " + col + ")");
        }
    }

    /**
     * Starts the game timer to periodically update the game state.
     * <p>
     * The timer interval is determined by the current game speed setting.
     * </p>
     */
    private void startGameTimer() {
        int delay = getTimerDelayForSpeed(model.getGameSpeed());
        gameTimer = new Timer(delay, e -> updateGameTime());
        gameTimer.start();
    }

    /**
     * Determines the appropriate timer delay based on the game speed setting.
     *
     * @param speed The game speed (1=Hour, 2=Day, 3=Week)
     * @return The timer delay in milliseconds
     */
    private int getTimerDelayForSpeed(int speed) {
        switch (speed) {
            case 1:
                return 5000; // 5 seconds per hour
            case 2:
                return 15000; // 15 seconds per day
            case 3:
                return 60000; // 1 minute per week
            default:
                return 5000;
        }
    }

    /**
     * Updates the game time and related state during each timer tick.
     * <p>
     * Advances the game time, checks for dead animals, updates the day/night
     * status, and checks for win conditions.
     * </p>
     */
    private void updateGameTime() {
        // Advance game time
        model.updateTime();

        // Important: Check for and remove dead animals
        if (mapView != null) {
            mapView.checkForDeadAnimals();
        }

        // Update day/night status in the map view
        if (mapView != null) {
            boolean isDaytime = model.getCurrentTime().isDaytime();
            mapView.updateDayNightStatus(isDaytime);

            // Update the UI to show current time
            updateTimeDisplay();
        }

        // Add this line to check for win condition
        checkWinCondition();
    }

    /**
     * Refreshes the map display.
     * <p>
     * Checks for dead animals and repaints the map view.
     * </p>
     */
    public void refreshMap() {
        if (mapView != null) {
            // Check for and remove any dead animals
            mapView.checkForDeadAnimals();

            // Repaint the map
            mapView.getScrollPane().repaint();
        }
    }

    /**
     * Updates the time display in the dashboard.
     * <p>
     * Formats the current game time and updates the time display
     * in the dashboard view.
     * </p>
     */
    private void updateTimeDisplay() {
        // This would update a time display component in the UI
        // For example, if you had a time label in the dashboard
        if (dashboardView != null) {
            Time time = model.getCurrentTime();
            String timeStr = String.format("Day %d, Month %d, %d:00 %s",
                    time.getDay(), time.getMonth(),
                    time.getHour() > 12 ? time.getHour() - 12 : time.getHour(),
                    time.getHour() >= 12 ? "PM" : "AM");
            dashboardView.updateTimeDisplay(timeStr);
        }
    }

    /**
     * Toggles the night mode in the map view.
     * <p>
     * Updates the visual appearance of the map to reflect day or night time.
     * </p>
     *
     * @param isNight True if night mode should be enabled, false for day mode
     */
    public void toggleNightMode(boolean isNight) {
        // Update the map view
        if (mapView != null) {
            // This directly sets the night mode state
            mapView.updateDayNightStatus(!isNight);

            // Update indicator in dashboard
            if (dashboardView != null) {
                dashboardView.updateDayNightIndicator(!isNight);
            }
        }
    }

    /**
     * Handles capital updates when a jeep is near animals.
     * <p>
     * Calculates satisfaction score based on nearby animals and
     * updates the player's capital accordingly.
     * </p>
     *
     * @param jeepPosition The position of the jeep
     */
    public void handleJeepCapitalUpdate(Coordinate jeepPosition) {
        List<Animal> animalsInRange = mapView.getNearbyAnimals(jeepPosition.getX(), jeepPosition.getY(), 20);
        int earned = mapView.calculateSatisfactionScore(animalsInRange);
        model.getPlayer().updateCapital(earned);
        dashboardView.showCapitalIncreasePopup(earned);
    }

    /**
     * Advances the game time quickly.
     * <p>
     * Advances time by 6 hours and updates relevant displays.
     * </p>
     */
    public void advanceTimeQuickly() {
        // Advance time by 6 hours
        model.getCurrentTime().advanceTimeByHours(6);

        // Format the time string
        Time time = model.getCurrentTime();
        String timeStr = String.format("Time: Day %d, %d:00 %s",
                time.getDay(),
                time.getHour() > 12 ? time.getHour() - 12 : time.getHour() == 0 ? 12 : time.getHour(),
                time.getHour() >= 12 ? "PM" : "AM");

        // Update the map view with new day/night status
        boolean isDaytime = time.isDaytime();
        if (mapView != null) {
            mapView.updateDayNightStatus(isDaytime);
        }

        // Update the dashboard
        if (dashboardView != null) {
            dashboardView.updateDayNightIndicator(isDaytime);
            dashboardView.updateTimeDisplay(timeStr);
        }
    }

/**
     * Updates the tourist count display in the dashboard.
     *
     * @param count The number of tourists to display
     */
    public void updateTouristDisplay(int count) {
        if (dashboardView != null) {
            dashboardView.updateTouristDisplay(count);
        }
    }

    /**
     * Gets the appropriate movement delay based on the current game speed.
     * <p>
     * This method determines how quickly animals and other entities should
     * move in the game, with faster game speeds resulting in quicker movements.
     * </p>
     *
     * @return The movement delay in milliseconds
     */
    public int getMovementDelayBasedOnGameSpeed() {
        int gameSpeed = model.getGameSpeed();  // 1 = Hour, 2 = Day, 3 = Week

        switch (gameSpeed) {
            case 1:  // Hour-level detail = slowest movement
                return 1000;
            case 2:  // Day-level = medium movement
                return 600;
            case 3:  // Week-level = fast movement
                return 300;
            default:
                return 800; // fallback
        }
    }

    /**
     * Stops the game timer if it's running.
     * <p>
     * This method is called when the game ends or when switching to a different state
     * that doesn't require regular updates.
     * </p>
     */
    private void stopGameTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
            System.out.println("Game timer stopped");
        }
    }

    /**
     * Gets the capital threshold required to win based on current difficulty.
     * <p>
     * Different difficulty levels have different capital requirements to win the game.
     * </p>
     *
     * @return The capital amount needed to win
     */
    public double getCapitalThresholdToWin() {
        String difficulty = model.getGameDifficulty();

        // Default threshold in case difficulty is not recognized
        double threshold = 1000.0;

        if (difficulty != null) {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    threshold = 1500.0;
                    break;
                case "medium":
                    threshold = 5000.0;
                    break;
                case "hard":
                    threshold = 8000.0;
                    break;
                default:
                    // Fall back to medium difficulty
                    threshold = 5000.0;
            }
        }

        return threshold;
    }

    /**
     * Checks if the player has met the win condition.
     * <p>
     * The win condition is based on the player's capital reaching a threshold
     * that depends on the game difficulty. If the condition is met, the game
     * ends with a victory.
     * </p>
     */
    private void checkWinCondition() {
        // Only check if game is currently running
        if (model != null && model.getGameState().equals("Running") && model.getPlayer() != null) {
            double currentCapital = model.getPlayer().getCapital();
            double thresholdToWin = getCapitalThresholdToWin();

            // Add detailed debug output
            System.out.println("--- WIN CONDITION CHECK ---");
            System.out.println("Current capital: " + currentCapital);
            System.out.println("Threshold: " + thresholdToWin);
            System.out.println("Difficulty: " + model.getGameDifficulty());
            System.out.println("Game state: " + model.getGameState());
            System.out.println("Comparison result: " + (currentCapital >= thresholdToWin));

            // Instead of using exact equality:
            if (currentCapital >= thresholdToWin) {
                // Use a small epsilon to account for floating point comparison issues:
                if (currentCapital >= thresholdToWin - 0.001) {
                    // Player has won!
                    endGameWithVictory();
                }
            }
        }
    }

    /**
     * Ends the game with a victory.
     * <p>
     * Updates the game state, stops all timers, and displays the victory popup.
     * </p>
     */
    private void endGameWithVictory() {
        // Update game state
        JOptionPane.showMessageDialog(mainFrame, "DEBUG: Victory method called!");
        model.setGameState("Completed");
        model.endGame();

        // Stop timers to prevent further updates
        stopGameTimer();

        // If we're using animal timers in MapView, stop those too
        if (mapView != null) {
            mapView.stopAllAnimalTimers();
        }

        // Show victory popup
        showVictoryPopup();
    }

    /**
     * A panel for displaying fireworks animation in the victory popup.
     * <p>
     * This inner class creates and manages animated fireworks for visual celebration
     * when the player wins the game.
     * </p>
     */
    private class FireworksPanel extends JPanel {
        /** List of active fireworks in the animation. */
        private List<Firework> fireworks = new ArrayList<>();
        
        /** Timer for updating the fireworks animation. */
        private Timer animationTimer;

        /**
         * Constructs a new FireworksPanel.
         * <p>
         * Initializes the panel with several fireworks and starts the animation timer.
         * </p>
         */
        public FireworksPanel() {
            setOpaque(false);

            // Create initial fireworks
            for (int i = 0; i < 5; i++) {
                addFirework();
            }

            // Start animation timer
            animationTimer = new Timer(50, e -> {
                updateFireworks();
                repaint();
            });
            animationTimer.start();
        }

        /**
         * Adds a new firework to the animation.
         * <p>
         * Creates a firework at a random horizontal position at the bottom of the panel,
         * with a random target height and color.
         * </p>
         */
        private void addFirework() {
            int x = (int) (Math.random() * getWidth());
            int y = getHeight();
            int targetY = (int) (Math.random() * getHeight() * 0.7);
            Color color = new Color(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255)
            );

            fireworks.add(new Firework(x, y, targetY, color));
        }

        /**
         * Updates all fireworks in the animation.
         * <p>
         * Updates each firework's state, removes expired fireworks,
         * and occasionally adds new ones.
         * </p>
         */
        private void updateFireworks() {
            // Update existing fireworks
            Iterator<Firework> it = fireworks.iterator();
            while (it.hasNext()) {
                Firework firework = it.next();
                if (firework.update()) {
                    // Firework has expired, remove it
                    it.remove();
                }
            }

            // Add new fireworks occasionally
            if (Math.random() < 0.1) {
                addFirework();
            }
        }

        /**
         * Paints the fireworks animation.
         *
         * @param g The Graphics context to paint on
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw all fireworks
            for (Firework firework : fireworks) {
                firework.draw(g2d);
            }
        }

        /**
         * Cleans up resources when panel is removed.
         */
        @Override
        public void removeNotify() {
            super.removeNotify();
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }
        }

        /**
         * Inner class representing a single firework in the animation.
         */
        private class Firework {
            /** X-coordinate of the firework. */
            private int x, y;
            
            /** Target height for the firework to explode at. */
            private int targetY;
            
            /** Color of the firework. */
            private Color color;
            
            /** Flag indicating if the firework has exploded. */
            private boolean exploded = false;
            
            /** Particles created when the firework explodes. */
            private List<Particle> particles = new ArrayList<>();
            
            /** Lifespan of the exploded firework effect. */
            private int lifespan = 20;

            /**
             * Constructs a new Firework.
             *
             * @param x The x-coordinate of the firework
             * @param y The y-coordinate of the firework
             * @param targetY The target height for explosion
             * @param color The color of the firework
             */
            public Firework(int x, int y, int targetY, Color color) {
                this.x = x;
                this.y = y;
                this.targetY = targetY;
                this.color = color;
            }

            /**
             * Updates the firework's state.
             *
             * @return true if the firework should be removed, false otherwise
             */
            public boolean update() {
                if (!exploded) {
                    // Move up toward target
                    y -= 5;
                    if (y <= targetY) {
                        explode();
                    }
                    return false;
                } else {
                    // Update particles
                    for (Particle particle : particles) {
                        particle.update();
                    }

                    // Decrement lifespan
                    lifespan--;
                    return lifespan <= 0;
                }
            }

            /**
             * Causes the firework to explode, creating particles.
             */
            private void explode() {
                exploded = true;

                // Create particles in all directions
                for (int i = 0; i < 30; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double speed = 1 + Math.random() * 3;
                    double vx = Math.cos(angle) * speed;
                    double vy = Math.sin(angle) * speed;

                    particles.add(new Particle(x, y, vx, vy, color));
                }
            }

            /**
             * Draws the firework.
             *
             * @param g2d The Graphics2D context to draw on
             */
            public void draw(Graphics2D g2d) {
                if (!exploded) {
                    // Draw rocket
                    g2d.setColor(color);
                    g2d.fillRect(x, y, 2, 10);
                } else {
                    // Draw particles
                    for (Particle particle : particles) {
                        particle.draw(g2d);
                    }
                }
            }
        }

        /**
         * Inner class representing a particle from an exploded firework.
         */
        private class Particle {
            /** Position of the particle. */
            private double x, y;
            
            /** Velocity of the particle. */
            private double vx, vy;
            
            /** Color of the particle. */
            private Color color;
            
            /** Size of the particle. */
            private int size = 3;

            /**
             * Constructs a new Particle.
             *
             * @param x The x-coordinate
             * @param y The y-coordinate
             * @param vx The x-velocity
             * @param vy The y-velocity
             * @param color The color
             */
            public Particle(double x, double y, double vx, double vy, Color color) {
                this.x = x;
                this.y = y;
                this.vx = vx;
                this.vy = vy;
                this.color = color;
            }

            /**
             * Updates the particle's position and size.
             */
            public void update() {
                x += vx;
                y += vy;
                vy += 0.1; // Gravity
                size = (int) Math.max(1, size - 0.1);
            }

            /**
             * Draws the particle.
             *
             * @param g2d The Graphics2D context to draw on
             */
            public void draw(Graphics2D g2d) {
                g2d.setColor(color);
                g2d.fillOval((int) x, (int) y, size, size);
            }
        }
    }

    /**
     * Shows a congratulatory popup for winning the game.
     * <p>
     * Creates and displays a custom dialog with fireworks animation,
     * game statistics, and options to start a new game or exit.
     * </p>
     */
    private void showVictoryPopup() {
        if (mainFrame == null) {
            return;
        }

        // Get the difficulty level for display
        String difficulty = model.getGameDifficulty();
        if (difficulty == null) {
            difficulty = "Medium"; // Default fallback
        }

        // Create a custom dialog for victory
        JDialog victoryDialog = new JDialog(mainFrame, "Congratulations!", true);

        // Create the content panel with a gradient background
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Create a gold gradient background
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(218, 165, 32), // Gold
                        0, h, new Color(184, 134, 11) // Darker gold
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Add fireworks animation panel at the top
        FireworksPanel fireworksPanel = new FireworksPanel();
        fireworksPanel.setPreferredSize(new Dimension(500, 100)); // Match dialog width

        // Create heading
        JLabel titleLabel = new JLabel("Congratulations!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        // Create message with difficulty level
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>"
                + "You have successfully built a thriving Safari business!<br><br>"
                + "Your final capital: $" + model.getPlayer().getCapital() + "<br>"
                + "Difficulty level: " + difficulty + "<br><br>"
                + "Thanks for playing!</div></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        messageLabel.setForeground(Color.WHITE);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // New Game button
        JButton newGameButton = new JButton("New Game");
        styleButton(newGameButton);
        newGameButton.addActionListener(e -> {
            victoryDialog.dispose();
            // Reset to main menu
            if (gameView != null) {
                gameView.switchToMainGamePanel();
            }
        });

        // Exit button
        JButton exitButton = new JButton("Exit Game");
        styleButton(exitButton);
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);

        // Center panel for title and message
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(titleLabel, BorderLayout.NORTH);
        centerPanel.add(messageLabel, BorderLayout.CENTER);

        // Add components to content panel
        contentPanel.add(fireworksPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog content and display
        victoryDialog.setContentPane(contentPanel);
        victoryDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Set size AFTER setting content pane
        victoryDialog.setSize(500, 400);
        victoryDialog.setLocationRelativeTo(mainFrame);

        // Make dialog visible
        victoryDialog.setVisible(true);

        System.out.println("Victory popup displayed!");
    }

    /**
     * Applies game effects based on the selected difficulty level.
     * <p>
     * Adjusts various game parameters based on the chosen difficulty
     * when starting a new game.
     * </p>
     */
    private void applyDifficultyEffects() {
        String difficulty = model.getGameDifficulty();
        if (difficulty == null) {
            return;
        }

        switch (difficulty.toLowerCase()) {
            case "easy":
                // More starting money
                if (model.getPlayer() != null) {
                    model.getPlayer().updateCapital(500); // Bonus starting capital
                }
                break;

            case "medium":
                // Normal settings - no changes
                break;

            case "hard":
                // Less starting money
                if (model.getPlayer() != null) {
                    model.getPlayer().updateCapital(-200); // Start with less
                }
                break;
        }

        System.out.println("Applied difficulty effects for level: " + difficulty);
    }

    /**
     * Helper method to style buttons consistently.
     * <p>
     * Applies consistent visual styling to buttons in the game interface,
     * including fonts, colors, borders, and hover effects.
     * </p>
     *
     * @param button The JButton to style
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        button.setBackground(new Color(139, 69, 19)); // Brown
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 165, 0), 3), // Orange border
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding
        ));
        button.setPreferredSize(new Dimension(150, 50));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(160, 82, 45)); // Lighter brown
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(139, 69, 19)); // Back to original
            }
        });
    }
}
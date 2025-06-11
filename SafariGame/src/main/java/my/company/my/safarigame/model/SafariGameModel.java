package my.company.my.safarigame.model;

/**
 * Main model class for the Safari Game.
 * <p>
 * Following MVC pattern, this class only contains data and business logic.
 * It serves as the central component that manages the game state, including
 * the safari map, player, market, time, and scoring system. The model handles
 * core game mechanics such as starting, pausing, and updating the game state.
 * </p>
 */
public class SafariGameModel {
    /** The safari map representing the game world and its objects. */
    private SafariMap map;
    
    /** The player participating in the game. */
    private Player player;
    
    /** The market system for buying and selling items. */
    private Market market;
    
    /** 
     * The game speed setting.
     * <p>1 = Hour, 2 = Day, 3 = Week</p>
     */
    private int gameSpeed;
    
    /** The current time in the game world. */
    private Time currentTime;
    
    /** The current state of the game (e.g., "Not Started", "Running", "Paused", "Ended"). */
    private String gameState;
    
    /** The difficulty level of the game. */
    private String gameDifficulty;
    
    /** The player's current score. */
    private int score;
    
    /** Default grid file path. */
    private static final String DEFAULT_GRID_FILE = "/grids/grid1.txt";
    
    /** Default cell size in pixels. */
    private static final int DEFAULT_CELL_SIZE = 48;

    /**
     * Constructor initializes the model with default values.
     * <p>
     * Creates a game with the default grid file, initializes the market,
     * sets the starting time to day 1, hour 1, and sets the game state
     * to "Not Started". The default game speed is set to the Day cycle (2).
     * </p>
     */
    public SafariGameModel() {
        // Initialize map with the default grid file
        this.map = new SafariMap(DEFAULT_GRID_FILE, DEFAULT_CELL_SIZE);
        
        // Initialize other components
        this.market = new Market();
        this.currentTime = new Time(1, 1);
        this.gameState = "Not Started";
        this.score = 0;
        
        // Default game speed to Day cycle
        this.gameSpeed = 2;
    }
    
    /**
     * Alternate constructor that allows specifying a different grid file.
     * <p>
     * Creates a game with the specified grid file while using default values
     * for other components.
     * </p>
     *
     * @param gridFilePath Path to the grid file to use for the game map
     */
    public SafariGameModel(String gridFilePath) {
        // Initialize map with the specified grid file
        this.map = new SafariMap(gridFilePath, DEFAULT_CELL_SIZE);
        
        // Initialize other components
        this.market = new Market();
        this.currentTime = new Time(1, 1);
        this.gameState = "Not Started";
        this.score = 0;
        
        // Default game speed to Day cycle
        this.gameSpeed = 2;
    }

    /**
     * Starts the game by changing its state to "Running".
     * <p>
     * This method should be called to begin gameplay after initialization.
     * </p>
     */
    public void startGame() {
        this.gameState = "Running";
    }

    /**
     * Pauses the game by changing its state to "Paused".
     * <p>
     * This method temporarily suspends gameplay without ending the game.
     * </p>
     */
    public void pauseGame() {
        this.gameState = "Paused";
    }

    /**
     * Ends the game by changing its state to "Ended".
     * <p>
     * This method should be called when the game concludes, either through
     * player victory, defeat, or manual termination.
     * </p>
     */
    public void endGame() {
        this.gameState = "Ended";
    }

    /**
     * Updates the game state during each game tick.
     * <p>
     * This method updates all game components, including the map (which updates
     * all landscape objects like animals and plants) and the game time.
     * Future implementations could include win/loss condition checks.
     * </p>
     */
    public void updateGameState() {
        map.updateMap(); // Map will update objects like animals, plants, etc.
        updateTime();
        // Add win/loss check logic here in future
    }

    /**
     * Updates the game time based on the current game speed setting.
     * <p>
     * The amount of time advancement depends on the game speed:
     * 1 = Hourly, 2 = Daily, 3 = Weekly
     * </p>
     */
    public void updateTime() {
        currentTime.advanceTime(gameSpeed);
    }

    /**
     * Checks if it's currently daytime in the game world.
     * <p>
     * This can be used to determine visibility, animal behavior,
     * or other time-dependent game mechanics.
     * </p>
     *
     * @return true if it's daytime, false if it's nighttime
     */
    public boolean isDaytime() {
        return currentTime.isDaytime(); // Could check day/night cycle
    }

    /**
     * Gets the safari map representing the game world.
     *
     * @return The SafariMap object
     */
    public SafariMap getMap() {
        return map;
    }
    
    /**
     * Sets a new safari map for the game.
     *
     * @param map The new SafariMap object
     */
    public void setMap(SafariMap map) {
        this.map = map;
    }

    /**
     * Gets the current player.
     *
     * @return The Player object
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Creates and sets a new player with the specified name.
     *
     * @param name The name for the new player
     */
    public void setPlayer(String name) {
        this.player = new Player(name);
    }
    
    /**
     * Sets an existing player object.
     *
     * @param player The Player object to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the market system.
     *
     * @return The Market object
     */
    public Market getMarket() {
        return market;
    }
    
    /**
     * Sets a new market system.
     *
     * @param market The new Market object
     */
    public void setMarket(Market market) {
        this.market = market;
    }

    /**
     * Gets the current game speed setting.
     *
     * @return The game speed (1=Hour, 2=Day, 3=Week)
     */
    public int getGameSpeed() {
        return gameSpeed;
    }

    /**
     * Sets the game speed setting.
     * <p>
     * Valid values are:
     * </p>
     * <ul>
     *   <li>1 = Hourly time advancement</li>
     *   <li>2 = Daily time advancement</li>
     *   <li>3 = Weekly time advancement</li>
     * </ul>
     *
     * @param gameSpeed The new game speed (must be between 1 and 3)
     * @throws IllegalArgumentException If the provided game speed is not between 1 and 3
     */
    public void setGameSpeed(int gameSpeed) {
        // Validate the input
        if (gameSpeed < 1 || gameSpeed > 3) {
            throw new IllegalArgumentException("Game speed must be between 1 and 3");
        }
        this.gameSpeed = gameSpeed;
    }
    
    /**
     * Gets the current game difficulty setting.
     *
     * @return The game difficulty as a string
     */
    public String getGameDifficulty() {
        return this.gameDifficulty;
    }
    
    /**
     * Sets the game difficulty.
     *
     * @param gameDifficulty The new game difficulty
     */
    public void setGameDifficulty(String gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    /**
     * Gets the current game time.
     *
     * @return The Time object representing the current game time
     */
    public Time getCurrentTime() {
        return currentTime;
    }
    
    /**
     * Sets the current game time.
     *
     * @param currentTime The new Time object
     */
    public void setCurrentTime(Time currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * Gets the current game state.
     *
     * @return The game state as a string ("Not Started", "Running", "Paused", or "Ended")
     */
    public String getGameState() {
        return gameState;
    }
    
    /**
     * Sets the game state.
     *
     * @param gameState The new game state
     */
    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    /**
     * Gets the current score.
     *
     * @return The current score as an integer
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Sets the score to a specific value.
     *
     * @param score The new score value
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Increases the score by the specified amount.
     * <p>
     * This method can be used to reward the player for achievements
     * or successful actions in the game.
     * </p>
     *
     * @param amount The amount to increase the score by
     */
    public void increaseScore(int amount) {
        this.score += amount;
    }
}
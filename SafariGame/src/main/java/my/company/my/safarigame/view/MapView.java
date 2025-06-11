package my.company.my.safarigame.view;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import my.company.my.safarigame.controller.GameController;
import my.company.my.safarigame.model.Cell;
import my.company.my.safarigame.model.Coordinate;
import my.company.my.safarigame.model.Grid;
import my.company.my.safarigame.model.LandScapeObject;
import my.company.my.safarigame.model.Plant;
import my.company.my.safarigame.model.Player;
import my.company.my.safarigame.model.Road;
import my.company.my.safarigame.model.SafariMap;
import my.company.my.safarigame.model.TradeableItem;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import my.company.my.safarigame.model.Animal;
import my.company.my.safarigame.model.Carnivore;
import my.company.my.safarigame.model.Herbivore;
import my.company.my.safarigame.model.Jeep;
import my.company.my.safarigame.model.Ranger;
import my.company.my.safarigame.model.Tourist;
import my.company.my.safarigame.model.WaterArea;

/**
 * MapView class that displays the main game map and handles navigation Follows
 * MVC pattern by receiving the Grid model from the controller
 */
public class MapView implements MiniMapView.NavigationCallback {

    // Main components
    private JPanel mapPanel;
    private JScrollPane scrollPane;
    private GameController controller;

    // Map dimensions and viewport
    private int GRID_ROWS;
    private int GRID_COLS;
    private int CELL_SIZE = 60;
    private int MAP_WIDTH;
    private int MAP_HEIGHT;

    // Visible cells on screen
    private final int VISIBLE_CELLS = 10;

    // Current viewport position
    private int viewportX = 0;
    private int viewportY = 0;

    // Reference to mini map for updates
    private MiniMapView miniMapView;

    // Map content array
    private JLabel[][] mapCells;

    // Grid model reference
    private Grid grid;

    private JPanel nightOverlayPanel;
    private float nightOpacity = 0.6f;

    // Terrain images
    private ImageIcon wallIcon;
    private ImageIcon grassIcon;
    private ImageIcon lwallIcon;
    private ImageIcon swallIcon;
    private ImageIcon awallIcon;
    private ImageIcon tgateIcon;
    private ImageIcon bgateIcon;
    private ImageIcon plantIcon;
    private ImageIcon bushIcon;
    private ImageIcon shrubIcon;
    private ImageIcon verticalRoadIcon;
    private ImageIcon horizontalRoadIcon;
    private ImageIcon rightupRoadIcon;
    private ImageIcon rightdownRoadIcon;
    private ImageIcon leftupRoadIcon;
    private ImageIcon leftdownRoadIcon;
    private ImageIcon cowIcon;
    private ImageIcon deerIcon;
    private ImageIcon lionIcon;
    private ImageIcon wolfIcon;
    private ImageIcon pondIcon;
    private ImageIcon rangerIcon;
    private ImageIcon jeepIcon;
    private ImageIcon mountainIcon;
    private ImageIcon riverIcon;
    private ImageIcon jeepStraight;
    private ImageIcon jeepRight;
    private ImageIcon jeep_1;
    private ImageIcon jeep_2;
    private ImageIcon jeep_3;
    private ImageIcon jeep_4;
    private ImageIcon jeepRightReverse;
    private ImageIcon jeepStraightReverse;
    private ImageIcon hillIcon;

    // Placement of item.
    private boolean placementMode = false;
    private TradeableItem itemToPlace = null;
    private ImageIcon placementIcon = null;
    private Point hoverCell = null;
    private final Color VALID_PLACEMENT_COLOR = new Color(0, 255, 0, 80);
    private final Color INVALID_PLACEMENT_COLOR = new Color(255, 0, 0, 80);

    private List<Animal> allAnimals = new ArrayList<>();
    // This will keep track of animal groups (leader -> followers)
    private Map<Animal, List<Animal>> carnivoreGroups = new HashMap<>();
    private Map<Animal, List<Animal>> herbivoreGroups = new HashMap<>();
    private int waitingTourists = 0;

    private Map<Animal, Timer> animalTimers = new HashMap<>();

    // removing an item.
    private boolean removalMode = false;
    private JToggleButton removalButton;

    // Add a method to create and get the removal button
    public JToggleButton getRemovalButton() {
        if (removalButton == null) {
            removalButton = new JToggleButton("Remove Items");
            removalButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
            removalButton.setBackground(new Color(220, 100, 100));
            removalButton.setForeground(Color.WHITE);
            removalButton.setFocusPainted(false);

            // Toggle removal mode when button is clicked
            removalButton.addActionListener(e -> {
                removalMode = removalButton.isSelected();
                System.out.print("I am here boss");
                // Change cursor to indicate mode
                if (removalMode) {
                    mapPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    mapPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                // Disable placement mode if active
                if (removalMode && placementMode) {
                    disablePlacementMode();
                }
            });
        }
        return removalButton;
    }

    // Movement key constants
    private final int MOVE_AMOUNT = 32;

    /**
     * Constructor that receives a Grid model
     */
    public MapView(Grid grid, GameController controller) {
        this.grid = grid;
        this.controller = controller;

        this.allAnimals = new ArrayList<>();

        if (grid != null) {
            // Set dimensions based on the grid
            GRID_ROWS = grid.getRows();
            GRID_COLS = grid.getColumns();

            MAP_WIDTH = GRID_COLS * CELL_SIZE;
            MAP_HEIGHT = GRID_ROWS * CELL_SIZE;

            // Initialize the cell labels array
            mapCells = new JLabel[GRID_ROWS][GRID_COLS];

            // Load images and create the map interface
            loadTerrainImages();
            createMapContent();
            createMapPanel();
            createNightOverlay();
            createScrollPane();

            // Start at the center of the map
            centerViewport();
        } else {
            // Fallback to default dimensions if grid is null
            GRID_ROWS = 50;
            GRID_COLS = 50;
            MAP_WIDTH = GRID_COLS * CELL_SIZE;
            MAP_HEIGHT = GRID_ROWS * CELL_SIZE;

            // Initialize the cell labels array
            mapCells = new JLabel[GRID_ROWS][GRID_COLS];

            // Load images and create the map interface
            loadTerrainImages();
            createDefaultMapContent();
            createMapPanel();
            createScrollPane();
        }

        // Always add mouse listener for removal mode
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (removalMode) {
                    // Calculate cell coordinates
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;

                    // Make sure we're within grid bounds
                    if (col >= 0 && col < GRID_COLS && row >= 0 && row < GRID_ROWS) {
                        // Check if cell is occupied and has a landscape object
                        if (grid != null && grid.isOccupied(row, col)) {
                            // Get cell type before removal
                            char cellType = grid.getCellType(row, col);

                            System.out.print(row);
                            System.out.print(cellType);

                            // Only remove landscape objects (non-wall items)
                            if (isRemovableItem(cellType)) {
                                removeItem(row, col, cellType);
                            } else {
                                JOptionPane.showMessageDialog(mapPanel,
                                        "This item cannot be removed!",
                                        "Cannot Remove Item",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        // Start at the center of the map
        centerViewport();
    }

    // Modified version of the createNightOverlay method
    private void createNightOverlay() {
        // Create an overlay panel that will be shown during night time
        nightOverlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Create a semi-transparent dark color - increased opacity for testing
                Color nightColor = new Color(10, 10, 40, 180); // Using a higher alpha value (180 out of 255)

                // Fill the entire panel
                g2d.setColor(nightColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Add some debug text to verify it's rendering
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.drawString("NIGHT MODE ACTIVE", 100, 100);
            }
        };

        // Make it transparent for mouse events but keep background
        nightOverlayPanel.setOpaque(false);

        // Size it to cover the entire map
        int width = mapPanel.getWidth() > 0 ? mapPanel.getWidth() : MAP_WIDTH;
        int height = mapPanel.getHeight() > 0 ? mapPanel.getHeight() : MAP_HEIGHT;
        nightOverlayPanel.setBounds(0, 0, width, height);

        // Set to null layout to ensure absolute positioning
        mapPanel.setLayout(null);

        // Add it to the map panel - make sure it's the LAST component added
        // so it appears on top of everything
        mapPanel.add(nightOverlayPanel, JLayeredPane.DRAG_LAYER); // Try to bring to front

        // Initially show it for testing, then we'll toggle it
        nightOverlayPanel.setVisible(true);

        // Print debug info
        System.out.println("Night overlay created with bounds: " + nightOverlayPanel.getBounds());
        System.out.println("Map panel size: " + mapPanel.getSize());
    }

    /**
     * Create a default map with empty cells
     */
    private void createDefaultMapContent() {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                // Create a precisely sized JLabel
                JLabel cell = new JLabel();

                // Set size properties
                cell.setSize(CELL_SIZE, CELL_SIZE);
                cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setVerticalAlignment(SwingConstants.CENTER);

                // Create basic boundary walls
                if (row == 0 || row == GRID_ROWS - 1 || col == 0 || col == GRID_COLS - 1) {
                    cell.setIcon(wallIcon);
                } else {
                    cell.setIcon(grassIcon);
                }

                // Store the cell
                mapCells[row][col] = cell;
            }
        }
    }

    /**
     * Create the map panel with all terrain cells
     */
    private void createMapPanel() {
        // Create a panel with null layout for absolute positioning
        mapPanel = new JPanel();
        mapPanel.setLayout(null);
        mapPanel.setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
        mapPanel.setSize(MAP_WIDTH, MAP_HEIGHT);

        // Add all map cells to the panel with precise positions
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                JLabel cell = mapCells[row][col];
                cell.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                mapPanel.add(cell);
            }
        }
    }

    /**
     * Navigate to a specific point on the map Implements
     * MiniMapView.NavigationCallback
     */
    @Override
    public void navigateToPoint(int mapX, int mapY) {
        try {
            // Calculate the size of the viewport
            int viewportWidth = scrollPane.getViewport().getWidth();
            int viewportHeight = scrollPane.getViewport().getHeight();

            // Center the viewport around the clicked position
            int centerX = mapX - (viewportWidth / 2);
            int centerY = mapY - (viewportHeight / 2);

            // Ensure within bounds
            int mapWidth = mapPanel.getPreferredSize().width;
            int mapHeight = mapPanel.getPreferredSize().height;

            centerX = Math.max(0, Math.min(mapWidth - viewportWidth, centerX));
            centerY = Math.max(0, Math.min(mapHeight - viewportHeight, centerY));

            // Scroll to the position
            scrollTo(centerX, centerY);
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }

    public void enablePlacementMode(TradeableItem item) {
        this.placementMode = true;
        this.itemToPlace = item;

        // Set placement icon based on item type
        if (item instanceof Plant) {
            String species = ((Plant) item).getDescription().toLowerCase();
            if (species.contains("bush")) {
                placementIcon = bushIcon;
            } else if (species.contains("shrub")) {
                placementIcon = shrubIcon;
            } else {
                placementIcon = plantIcon;
            }
        } else if (item instanceof Road) {
            String roadType = ((Road) item).getRoadType().toLowerCase();
            if (roadType.equals("horizontal")) {
                placementIcon = horizontalRoadIcon;
            } else if (roadType.equals("vertical")) {
                placementIcon = verticalRoadIcon;
            } else if (roadType.contains("rightup")) {
                placementIcon = rightupRoadIcon;
            } else if (roadType.contains("rightdown")) {
                placementIcon = rightdownRoadIcon;
            } else if (roadType.contains("leftup")) {
                placementIcon = leftupRoadIcon;
            } else if (roadType.contains("leftdown")) {
                placementIcon = leftdownRoadIcon;
            }
        } else if (item instanceof Herbivore) {
            String species = ((Herbivore) item).getDescription().toLowerCase();
            if (species.contains("cow")) {
                placementIcon = cowIcon;
            } else if (species.contains("deer")) {
                placementIcon = deerIcon;
            } else if (species.contains("wolf")) {
                placementIcon = wolfIcon;
            } else if (species.contains("lion")) {
                placementIcon = lionIcon;
            } else {
                placementIcon = plantIcon;
            }
        } else if (item instanceof Carnivore) {
            String species = ((Carnivore) item).getDescription().toLowerCase();
            if (species.contains("wolf")) {
                placementIcon = wolfIcon;
            } else if (species.contains("lion")) {
                placementIcon = lionIcon;
            } else {
                placementIcon = plantIcon;
            }
        } else if (item instanceof Jeep) {
            String species = ((Jeep) item).getDescription().toLowerCase();
            if (species.contains("jeep")) {
                placementIcon = jeepIcon;
            }

        } else if (item instanceof Ranger) {
            String species = ((Ranger) item).getDescription().toLowerCase();
            if (species.contains("ranger")) {
                placementIcon = rangerIcon;
            }

        } else if (item instanceof WaterArea) {
            placementIcon = pondIcon; // You already have pondIcon loaded
        }

        // Add mouse listeners for hover and click
        setupPlacementMouseListeners();

        // Change cursor to indicate placement mode
        mapPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void disablePlacementMode() {
        this.placementMode = false;
        this.itemToPlace = null;
        this.placementIcon = null;
        this.hoverCell = null;

        // Reset cursor
        mapPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        mapPanel.repaint();
    }

    private void setupPlacementMouseListeners() {
        // Add hover effect to show where item will be placed
        mapPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (placementMode) {
                    // Calculate cell coordinates
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;

                    // Make sure we're within grid bounds
                    if (col >= 0 && col < GRID_COLS && row >= 0 && row < GRID_ROWS) {
                        hoverCell = new Point(col, row);
                        mapPanel.repaint();
                    }
                }
            }
        });

        // Add click listener to place the item
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (placementMode && itemToPlace != null) {
                    // Calculate cell coordinates
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;

                    // Make sure we're within grid bounds
                    if (col >= 0 && col < GRID_COLS && row >= 0 && row < GRID_ROWS) {
                        // Check if cell is not occupied
                        if (!grid.isOccupied(row, col)) {
                            placeItem(row, col);
                        } else {
                            // Show error message - cell is occupied
                            JOptionPane.showMessageDialog(mapPanel,
                                    "This cell is already occupied!",
                                    "Cannot Place Item",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void moveHerbivoreFollowerTowardLeader(Animal follower, int leaderX, int leaderY) {
        Coordinate followerPos = follower.getPosition();
        int fx = followerPos.getX();
        int fy = followerPos.getY();

        int dx = Integer.compare(leaderX, fx);
        int dy = Integer.compare(leaderY, fy);

        int newFollowerX = fx + dx;
        int newFollowerY = fy + dy;

        if (isValidCell(newFollowerX, newFollowerY) && !grid.getCell(newFollowerX, newFollowerY).isOccupied()) {
            moveAnimalTo(follower, newFollowerX, newFollowerY);
        }
    }

    private List<Animal> findNearbySameSpeciesHerbivores(Animal leader) {
        List<Animal> nearbyHerbivores = new ArrayList<>();
        Coordinate pos = leader.getPosition();
        int x = pos.getX();
        int y = pos.getY();

        System.out.println("Looking for nearby same-species herbivores around (" + x + "," + y + ")...");

        // Check all 8 surrounding cells
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue; // Skip self
                }
                int newX = x + dx;
                int newY = y + dy;

                // Check if the cell contains an animal
                if (isAnimalCell(newX, newY)) {
                    Animal animal = getAnimalAtPosition(newX, newY); // Get the animal at this position

                    // Check if the animal is of the same species
                    if (animal != null && animal.getDescription().equalsIgnoreCase(leader.getDescription())) {
                        System.out.println("Same species match! Adding to nearby list (Herbivore).");
                        nearbyHerbivores.add(animal);
                    }
                }
            }
        }

        System.out.println("Nearby same-species herbivores found: " + nearbyHerbivores.size());
        return nearbyHerbivores;
    }

    private char getAnimalTypeAtPosition(int row, int col) {
        if (row < 0 || row >= GRID_ROWS || col < 0 || col >= GRID_COLS) {
            return '-'; // out of bounds
        }
        return grid.getCellType(row, col);
    }

    private void moveHerbivoreRandomly(Animal animal) {

        if (!(animal instanceof Herbivore)) {
            return;
        }

        if (animal.isBeingRemoved) {
            return;
        }
        Herbivore herbivore = (Herbivore) animal;

        if (herbivore.isDrinking()) {
            System.out.println(herbivore.getDescription() + " is drinking. Skipping movement.");
            return;
        }

        Coordinate position = herbivore.getPosition();
        int x = position.getX();
        int y = position.getY();

        if (herbivore.isDead()) {
            fixRemoveDeadAnimal(x, y, herbivore);
            return;
        }

        // If thirsty, handle water in a direct way
        if (herbivore.isThirsty()) {
            System.out.println(herbivore.getDescription() + " is thirsty (thirst: " + herbivore.getThirst() + ")");

            // 1. Check if already on water
            char currentCell = grid.getCellType(x, y);
            if (currentCell == 'P') {
                // Already on water - force drinking
                forceAnimalToDrink(herbivore, x, y);
                return; // Stop movement
            }

            // 2. Check adjacent cells for water
            boolean foundWater = false;
            int waterX = -1;
            int waterY = -1;

            // Loop through all 8 surrounding cells
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue; // Skip self
                    }
                    int nx = x + dx;
                    int ny = y + dy;

                    // Check if in bounds
                    if (nx >= 0 && nx < GRID_ROWS && ny >= 0 && ny < GRID_COLS) {
                        if (grid.getCellType(nx, ny) == 'P') {
                            // Found water!
                            foundWater = true;
                            waterX = nx;
                            waterY = ny;
                            break;
                        }
                    }
                }
                if (foundWater) {
                    break;
                }
            }

            if (foundWater) {
                System.out.println("Found nearby water at (" + waterX + ", " + waterY + ") - moving there");

                // Force movement to water cell regardless of normal movement restrictions
                // Clear current cell
                grid.getCell(x, y).setOccupied(false);

                // Move animal object
                herbivore.setPosition(waterX, waterY);

                // Handle UI update
                try {
                    // Clear old position
                    if (mapCells[x][y] != null) {
                        mapPanel.remove(mapCells[x][y]);
                    }

                    // Set old cell to grass
                    JLabel grassLabel = new JLabel();
                    grassLabel.setSize(CELL_SIZE, CELL_SIZE);
                    grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                    grassLabel.setIcon(grassIcon);
                    grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    grassLabel.setVerticalAlignment(SwingConstants.CENTER);
                    grassLabel.setBounds(y * CELL_SIZE, x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    mapCells[x][y] = grassLabel;
                    mapPanel.add(grassLabel);

                    // Clear new position if needed
                    if (mapCells[waterX][waterY] != null) {
                        mapPanel.remove(mapCells[waterX][waterY]);
                    }

                    // Create new animal label
                    AnimalLabel animalLabel = new AnimalLabel(herbivore, CELL_SIZE);

                    // Set appropriate icon
                    if (herbivore.getDescription().toLowerCase().contains("cow")) {
                        animalLabel.setIcon(cowIcon);
                    } else {
                        animalLabel.setIcon(deerIcon);
                    }

                    // Set position
                    animalLabel.setBounds(waterY * CELL_SIZE, waterX * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    // Add to panel
                    mapCells[waterX][waterY] = animalLabel;
                    mapPanel.add(animalLabel);

                    // Set water cell as occupied
                    grid.getCell(waterX, waterY).setOccupied(true);

                    // Update UI
                    mapPanel.revalidate();
                    mapPanel.repaint();

                    System.out.println("Successfully moved to water cell");
                } catch (Exception e) {
                    System.err.println("Error moving to water: " + e.getMessage());
                    e.printStackTrace();
                }

                // Force drinking at the water cell
                forceAnimalToDrink(herbivore, waterX, waterY);

                return; // Stop further movement
            }

            // 3. If we've previously found water, try to move toward it
            if (herbivore.getLastWaterSourceLocation() != null) {
                Coordinate waterPos = herbivore.getLastWaterSourceLocation();
                System.out.println("Remembers water at " + waterPos + ", trying to move there");

                // Calculate movement direction
                int dirX = Integer.compare(waterPos.getX(), x);
                int dirY = Integer.compare(waterPos.getY(), y);

                int newX = x + dirX;
                int newY = y + dirY;

                // Check if valid move
                if (newX >= 0 && newX < GRID_ROWS && newY >= 0 && newY < GRID_COLS) {
                    char destCell = grid.getCellType(newX, newY);

                    // Allow movement to water or empty cells
                    if ((destCell == '-' || destCell == 'w') && !grid.getCell(newX, newY).isOccupied()) {
                        moveAnimalTo(animal, newX, newY);

                        // If we moved to water, force drinking
                        if (destCell == 'w') {
                            forceAnimalToDrink(herbivore, newX, newY);
                        }

                        return; // Stop further movement
                    }
                }
            }
        }

        // Check for nearby vegetation first (in adjacent cells)
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        for (int i = 0; i < dx.length; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            // Check if the new cell is within grid bounds
            if (newX >= 0 && newX < GRID_ROWS && newY >= 0 && newY < GRID_COLS) {
                char cellType = grid.getCellType(newX, newY);

                // Check if the cell contains vegetation
                if (cellType == 'p' || cellType == 'b' || cellType == 'h') {
                    // Attempt to eat the plant
                    herbivore.grazePlant(cellType);

                    // Remove the plant from the map
                    removeVegetation(newX, newY, cellType);

                    System.out.println(herbivore.getDescription() + " ate vegetation at (" + newX + ", " + newY + ")");

                    // If in a group, move followers towards the leader
                    if (herbivoreGroups.containsKey(animal)) {
                        List<Animal> group = herbivoreGroups.get(animal);
                        for (Animal follower : group) {
                            moveFollowerTowardLeader(follower, newX, newY);
                        }
                    }

                    return; // Stop movement after eating
                }
            }
        }

        // Existing group movement logic for herbivores
        if (herbivoreGroups.containsKey(animal)) {
            List<Animal> group = herbivoreGroups.get(animal);

            int randomDirection = (int) (Math.random() * 4);
            int leaderNewX = x + dx[randomDirection * 2];
            int leaderNewY = y + dy[randomDirection * 2];

            if (isValidCell(leaderNewX, leaderNewY) && !grid.getCell(leaderNewX, leaderNewY).isOccupied()) {
                moveAnimalTo(animal, leaderNewX, leaderNewY);

                // Move followers
                for (Animal follower : group) {
                    moveFollowerTowardLeader(follower, leaderNewX, leaderNewY);
                }
            } else {
                System.out.println("Herbivore leader move invalid, skipping move.");
            }
        } else {
            // Not yet in a group, find nearby same-species
            List<Animal> nearbySameSpecies = findNearbySameSpeciesHerbivores(animal);

            if (!nearbySameSpecies.isEmpty()) {
                herbivoreGroups.put(animal, nearbySameSpecies);
                moveHerbivoreRandomly(animal); // Retry move with group
            } else {
                // Move randomly alone
                moveAlone(animal);
            }
        }
    }

    private boolean moveAnimalToWater(Animal animal) {
        if (animal == null) {
            return false;
        }

        System.out.println("Attempting to move " + animal.getDescription() + " to water...");

        Coordinate position = animal.getPosition();
        int x = position.getX();
        int y = position.getY();

        // FIRST CHECK: Is the animal already on a water cell?
        char currentCellType = grid.getCellType(x, y);
        if (currentCellType == 'P') {
            System.out.println(animal.getDescription() + " is already on water at (" + x + ", " + y + ")");

            // Find the water area object
            WaterArea waterArea = findWaterAreaAt(x, y);
            if (waterArea != null) {
                // IMPORTANT: Make the animal drink!
                System.out.println("Starting drinking at current position");
                animal.startContinuousDrinking(waterArea);

                // Force thirst reduction
                int thirstBefore = animal.getThirst();
                animal.thirst = Math.max(0, animal.getThirst() - 30);

                // Show drinking animation
                if (mapCells[x][y] instanceof AnimalLabel) {
                    AnimalLabel animalLabel = (AnimalLabel) mapCells[x][y];
                    animalLabel.showDrinkingAnimation();
                    animalLabel.updateThirstIndicator();
                }

                System.out.println("*** DRINKING SUCCESS: Thirst reduced from " + thirstBefore
                        + " to " + animal.getThirst() + " ***");
                return true;
            }
        }

        // SECOND CHECK: Is there water in adjacent cells?
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        // For priority, first check direct adjacent (non-diagonal)
        for (int i = 1; i < 8; i += 2) {  // 1, 3, 5, 7 are the direct adjacent indices
            int newX = x + dx[i];
            int newY = y + dy[i];

            if (newX >= 0 && newX < GRID_ROWS && newY >= 0 && newY < GRID_COLS) {
                if (grid.getCellType(newX, newY) == 'P' && !grid.isOccupied(newX, newY)) {
                    System.out.println("Found water in directly adjacent cell at (" + newX + ", " + newY + ")");

                    // Move to the water cell
                    boolean moveSuccess = false;

                    // Use direct update methods to avoid interference
                    try {
                        // Clear old position
                        grid.setCellType(x, y, '-');
                        grid.getCell(x, y).setOccupied(false);

                        // Update the animal's position in the model
                        animal.move(new Coordinate(newX, newY));

                        // Set new position
                        grid.getCell(newX, newY).setOccupied(true);

                        // Update UI
                        updateAnimalOnMapWithWaterCheck(animal, newX, newY);

                        moveSuccess = true;
                        System.out.println("Successfully moved to water at (" + newX + ", " + newY + ")");
                    } catch (Exception e) {
                        System.err.println("Error moving to water: " + e.getMessage());
                        e.printStackTrace();
                    }

                    if (moveSuccess) {
                        // Now that we're on water, make the animal drink
                        WaterArea waterArea = findWaterAreaAt(newX, newY);
                        if (waterArea != null) {
                            // IMPORTANT: Make the animal drink!
                            System.out.println("Starting drinking at new position");
                            animal.startContinuousDrinking(waterArea);

                            // Force thirst reduction
                            int thirstBefore = animal.getThirst();
                            animal.thirst = Math.max(0, animal.getThirst() - 30);

                            // Show drinking animation
                            if (mapCells[newX][newY] instanceof AnimalLabel) {
                                AnimalLabel animalLabel = (AnimalLabel) mapCells[newX][newY];
                                animalLabel.showDrinkingAnimation();
                                animalLabel.updateThirstIndicator();
                            }

                            System.out.println("*** DRINKING SUCCESS: Thirst reduced from " + thirstBefore
                                    + " to " + animal.getThirst() + " ***");
                            return true;
                        }
                    }
                }
            }
        }

        // Then check diagonal adjacents
        for (int i = 0; i < 8; i += 2) {  // 0, 2, 4, 6 are diagonal indices
            int newX = x + dx[i];
            int newY = y + dy[i];

            if (newX >= 0 && newX < GRID_ROWS && newY >= 0 && newY < GRID_COLS) {
                if (grid.getCellType(newX, newY) == 'w' && !grid.isOccupied(newX, newY)) {
                    System.out.println("Found water in diagonally adjacent cell at (" + newX + ", " + newY + ")");

                    // Move to the water cell using direct update
                    boolean moveSuccess = false;

                    try {
                        // Clear old position
                        grid.setCellType(x, y, '-');
                        grid.getCell(x, y).setOccupied(false);

                        // Update the animal's position in the model
                        animal.move(new Coordinate(newX, newY));

                        // Set new position
                        grid.getCell(newX, newY).setOccupied(true);

                        // Update UI
                        updateAnimalOnMapWithWaterCheck(animal, newX, newY);

                        moveSuccess = true;
                        System.out.println("Successfully moved to water at (" + newX + ", " + newY + ")");
                    } catch (Exception e) {
                        System.err.println("Error moving to water: " + e.getMessage());
                        e.printStackTrace();
                    }

                    if (moveSuccess) {
                        // Now that we're on water, make the animal drink
                        WaterArea waterArea = findWaterAreaAt(newX, newY);
                        if (waterArea != null) {
                            // IMPORTANT: Make the animal drink!
                            System.out.println("Starting drinking at new position");
                            animal.startContinuousDrinking(waterArea);

                            // Force thirst reduction
                            int thirstBefore = animal.getThirst();
                            animal.thirst = Math.max(0, animal.getThirst() - 30);

                            // Show drinking animation
                            if (mapCells[newX][newY] instanceof AnimalLabel) {
                                AnimalLabel animalLabel = (AnimalLabel) mapCells[newX][newY];
                                animalLabel.showDrinkingAnimation();
                                animalLabel.updateThirstIndicator();
                            }

                            System.out.println("*** DRINKING SUCCESS: Thirst reduced from " + thirstBefore
                                    + " to " + animal.getThirst() + " ***");
                            return true;
                        }
                    }
                }
            }
        }

        // THIRD CHECK: Check for remembered water or search wider area
        if (animal.getLastWaterSourceLocation() != null) {
            Coordinate waterPos = animal.getLastWaterSourceLocation();
            System.out.println("Animal remembers water at " + waterPos + ", attempting to move there");

            // Calculate direction to remembered water
            int directionX = Integer.compare(waterPos.getX(), x);
            int directionY = Integer.compare(waterPos.getY(), y);

            // Only move if we're not already there
            if (directionX != 0 || directionY != 0) {
                int newX = x + directionX;
                int newY = y + directionY;

                // Make sure the move is valid
                if (newX >= 0 && newX < GRID_ROWS && newY >= 0 && newY < GRID_COLS
                        && !grid.isOccupied(newX, newY)) {

                    // Move the animal toward the remembered water
                    moveAnimalTo(animal, newX, newY);
                    System.out.println("Moved toward remembered water: now at (" + newX + ", " + newY + ")");

                    // Check if we've reached water
                    if (grid.getCellType(newX, newY) == 'P') {
                        // Handle drinking at the remembered location
                        WaterArea waterArea = findWaterAreaAt(newX, newY);
                        if (waterArea != null) {
                            // IMPORTANT: Make the animal drink!
                            System.out.println("Found remembered water source, drinking");
                            animal.startContinuousDrinking(waterArea);

                            // Force thirst reduction
                            int thirstBefore = animal.getThirst();
                            animal.thirst = Math.max(0, animal.getThirst() - 30);

                            // Show drinking animation
                            if (mapCells[newX][newY] instanceof AnimalLabel) {
                                AnimalLabel animalLabel = (AnimalLabel) mapCells[newX][newY];
                                animalLabel.showDrinkingAnimation();
                                animalLabel.updateThirstIndicator();
                            }

                            System.out.println("*** DRINKING SUCCESS: Thirst reduced from " + thirstBefore
                                    + " to " + animal.getThirst() + " ***");
                            return true;
                        }
                    }

                    return true; // We moved closer to water
                }
            }
        }

        // Couldn't find water
        System.out.println("No accessible water found for " + animal.getDescription());
        return false;
    }

    private void updateAnimalOnMapWithWaterCheck(Animal animal, int newX, int newY) {
        try {
            // Get the old position
            Coordinate oldPos = animal.getPosition();
            int oldRow = oldPos.getX();
            int oldCol = oldPos.getY();

            // Update map cells UI
            if (mapCells[oldRow][oldCol] != null) {
                mapPanel.remove(mapCells[oldRow][oldCol]);
            }

            // Create grass label for old position
            JLabel grassLabel = new JLabel();
            grassLabel.setSize(CELL_SIZE, CELL_SIZE);
            grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
            grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
            grassLabel.setVerticalAlignment(SwingConstants.CENTER);
            grassLabel.setIcon(grassIcon);
            grassLabel.setBounds(oldCol * CELL_SIZE, oldRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            mapCells[oldRow][oldCol] = grassLabel;
            mapPanel.add(grassLabel);

            // Remove any existing component at new position
            if (mapCells[newX][newY] != null) {
                mapPanel.remove(mapCells[newX][newY]);
            }

            // Create and add animal label at new position
            AnimalLabel animalLabel = new AnimalLabel(animal, CELL_SIZE);

            // Set appropriate icon
            if (animal instanceof Herbivore) {
                String species = animal.getDescription().toLowerCase();
                if (species.contains("cow")) {
                    animalLabel.setIcon(cowIcon);
                } else if (species.contains("deer")) {
                    animalLabel.setIcon(deerIcon);
                }
            } else if (animal instanceof Carnivore) {
                String species = animal.getDescription().toLowerCase();
                if (species.contains("lion")) {
                    animalLabel.setIcon(lionIcon);
                } else if (species.contains("wolf")) {
                    animalLabel.setIcon(wolfIcon);
                }
            }

            animalLabel.setBounds(newY * CELL_SIZE, newX * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            mapCells[newX][newY] = animalLabel;
            mapPanel.add(animalLabel);

            // Check if we're on water and should be drinking
            if (grid.getCellType(newX, newY) == 'P') {
                WaterArea waterArea = findWaterAreaAt(newX, newY);
                if (waterArea != null) {
                    // Force animal to drink
                    animal.startContinuousDrinking(waterArea);

                    // Force thirst reduction
                    animal.thirst = Math.max(0, animal.getThirst() - 30);

                    // Show drinking animation
                    animalLabel.showDrinkingAnimation();
                    animalLabel.updateThirstIndicator();

                    System.out.println("Animal is now on water and drinking. Thirst: " + animal.getThirst());
                }
            }

            // Force UI update
            mapPanel.revalidate();
            mapPanel.repaint();

        } catch (Exception e) {
            System.err.println("Error in updateAnimalOnMapWithWaterCheck: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeVegetation(int row, int col, char cellType) {
        try {
            // Verify grid and controller exist
            if (grid == null || controller == null || controller.getModel() == null) {
                System.err.println("Cannot remove vegetation: Grid or Controller is null");
                return;
            }

            SafariMap map = controller.getModel().getMap();
            Player player = controller.getModel().getPlayer();

            if (map == null || player == null) {
                System.err.println("Cannot remove vegetation: Map or Player is null");
                return;
            }

            // Create a plant item based on the cell type
            TradeableItem removedItem = null;
            switch (cellType) {
                case 'p':
                    removedItem = new Plant(new Coordinate(row, col), "plant", 150.0);
                    break;
                case 'b':
                    removedItem = new Plant(new Coordinate(row, col), "bush", 100.0);
                    break;
                case 'h':
                    removedItem = new Plant(new Coordinate(row, col), "shrub", 200.0);
                    break;
                default:
                    return;
            }

            // Remove the landscape object from the map
            map.getLandscapeObjects().removeIf(obj
                    -> obj instanceof Plant
                    && obj.getPosition().x == row
                    && obj.getPosition().y == col
            );

            // Reset the cell in the grid
            grid.setCellType(row, col, '-'); // Set to grass
            Cell cell = grid.getCell(row, col);
            if (cell != null) {
                cell.setOccupied(false);
            }

            // Update the visual representation
            if (mapCells[row][col] != null) {
                mapCells[row][col].setIcon(grassIcon);
            }

            // Notify the controller about the item removal if needed
            if (controller != null) {
                controller.onItemRemoved(row, col);
            }

            // Optional: Add removed vegetation to player's inventory
            // player.addItemToInventory(removedItem);
        } catch (Exception e) {
            System.err.println("Error removing vegetation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // New method to update animal position without changing health
    private void updateAnimalOnMapWithoutHealthChange(Animal animal, int newX, int newY) {
        // Get the old position
        Coordinate oldPos = animal.getPosition();
        int oldRow = oldPos.getX();
        int oldCol = oldPos.getY();

        // Update the animal's position in the model
        animal.move(new Coordinate(newX, newY));

        // 1. Handle the old cell - return it to grass
        if (mapCells[oldRow][oldCol] != null) {
            mapPanel.remove(mapCells[oldRow][oldCol]);
        }

        JLabel grassLabel = new JLabel();
        grassLabel.setSize(CELL_SIZE, CELL_SIZE);
        grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
        grassLabel.setVerticalAlignment(SwingConstants.CENTER);

        // *** FIX HERE: Check if night mode is active ***
        if (isNightMode) {
            grassLabel.setIcon(createDarkModeIcon());
        } else {
            grassLabel.setIcon(grassIcon);
        }

        grassLabel.setBounds(oldCol * CELL_SIZE, oldRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        mapCells[oldRow][oldCol] = grassLabel;
        mapPanel.add(grassLabel);

        // Reset the old cell's state in the grid
        Cell oldCell = grid.getCell(oldRow, oldCol);
        if (oldCell != null) {
            oldCell.setOccupied(false);
            oldCell.setCellType('-'); // Set to grass
        }

        // 2. Handle the new cell - place the animal
        // Remove any existing label at the new position
        if (mapCells[newX][newY] != null) {
            mapPanel.remove(mapCells[newX][newY]);
        }

        // Create a new animal label
        AnimalLabel animalLabel = new AnimalLabel(animal, CELL_SIZE);

        // Determine which icon to use based on animal type
        if (animal instanceof Herbivore) {
            String species = animal.getDescription().toLowerCase();
            if (species.contains("cow")) {
                animalLabel.setIcon(cowIcon);
            } else if (species.contains("deer")) {
                animalLabel.setIcon(deerIcon);
            }
        } else if (animal instanceof Carnivore) {
            String species = animal.getDescription().toLowerCase();
            if (species.contains("lion")) {
                animalLabel.setIcon(lionIcon);
            } else if (species.contains("wolf")) {
                animalLabel.setIcon(wolfIcon);
            }
        }

        // *** ADDITIONAL FIX: Check if animal should be hidden in night mode ***
        if (isNightMode) {
            char cellType = grid.getCellType(newX, newY);
            if (!isVisibleAtNight(cellType, newX, newY)) {
                animalLabel.setIcon(createDarkModeIcon());
            }
        }

        // Position the animal label
        animalLabel.setBounds(newY * CELL_SIZE, newX * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Store and add the new animal label
        mapCells[newX][newY] = animalLabel;
        mapPanel.add(animalLabel);

        // Set the new cell state in the grid
        Cell newCell = grid.getCell(newX, newY);
        if (newCell != null) {
            newCell.setOccupied(true);

            // Set cell type based on animal type
            if (animal instanceof Herbivore) {
                String species = animal.getDescription().toLowerCase();
                if (species.contains("cow")) {
                    grid.setCellType(newX, newY, 'c');
                } else if (species.contains("deer")) {
                    grid.setCellType(newX, newY, 'd');
                }
            } else if (animal instanceof Carnivore) {
                String species = animal.getDescription().toLowerCase();
                if (species.contains("lion")) {
                    grid.setCellType(newX, newY, 'y');
                } else if (species.contains("wolf")) {
                    grid.setCellType(newX, newY, 'z');
                }
            }
        }

        // Make sure all components are properly updated visually
        animalLabel.revalidate();
        animalLabel.repaint();

        // Ensure the panel is updated
        // At the end of the method
        notifyMiniMapUpdate();
        mapPanel.revalidate();
        mapPanel.repaint();
    }

    public void printGridToConsole() {
        for (int row = 0; row < GRID_ROWS; row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < GRID_COLS; col++) {
                Cell cell = grid.getCell(row, col);
                char type = cell.getCellType(); // or grid.getCellType(row, col);
                line.append(type).append(' ');
            }
            System.out.println(line.toString());
        }
    }

// Method to check if a cell is valid for the animal to move to (i.e., green space or road)
// Method to check if a cell is valid for the animal to move to (i.e., green space or road)
    private boolean isValidCell(int row, int col) {
        if (row < 0 || row >= GRID_ROWS || col < 0 || col >= GRID_COLS) {
            System.out.println("Cell (" + row + ", " + col + ") is out of bounds.");
            return false; // Out of bounds
        }

        char cellType = grid.getCellType(row, col);

        // IMPORTANT: Add 'w' (water) as a valid cell type
        if (cellType == '-' || cellType == 'P') {
            System.out.println("Cell (" + row + ", " + col + ") is a valid cell.");
            return true;
        } else {
            System.out.println("Cell (" + row + ", " + col + ") is not a valid cell (not grass/water/road/etc.).");
            return false;
        }
    }

    private void forceAnimalToDrink(Animal animal, int x, int y) {
        System.out.println("FORCING " + animal.getDescription() + " TO DRINK at (" + x + ", " + y + ")");

        // First check if we need to create a water area
        WaterArea waterArea = findWaterAreaAt(x, y);
        if (waterArea == null) {
            // No water area object exists - create one
            waterArea = new WaterArea(new Coordinate(x, y), 100);
            controller.getModel().getMap().addLandscapeObject(waterArea);
            System.out.println("Created new WaterArea at (" + x + ", " + y + ")");
        }

        // Now force the animal to drink
        animal.isDrinking = true;
        animal.drinkingDuration = 5;

        // DIRECTLY modify thirst
        int oldThirst = animal.getThirst();
        animal.thirst = Math.max(0, animal.getThirst() - 30);

        // Remember this water location
        animal.lastWaterSourceLocation = new Coordinate(x, y);

        // Update visual display
        if (mapCells[x][y] instanceof AnimalLabel) {
            AnimalLabel label = (AnimalLabel) mapCells[x][y];
            label.showDrinkingAnimation();
            label.updateThirstIndicator();
        }

        System.out.println("SUCCESSFULLY REDUCED THIRST from " + oldThirst + " to " + animal.getThirst());
    }
// Method to update the animal's position on the map
    // Replace the existing updateAnimalOnMap method with this implementation:

    private void updateAnimalOnMap(Animal animal, int newX, int newY) {
        System.out.println("Updating animal position to: (" + newX + ", " + newY + ")");

        // Get the old position
        Coordinate oldPos = animal.getPosition();
        int oldRow = oldPos.getX();
        int oldCol = oldPos.getY();

        // Update the animal's position in the model
        animal.move(new Coordinate(newX, newY));

        // 1. Handle the old cell - return it to grass
        if (mapCells[oldRow][oldCol] != null) {
            mapPanel.remove(mapCells[oldRow][oldCol]);
        }

        JLabel grassLabel = new JLabel();
        grassLabel.setSize(CELL_SIZE, CELL_SIZE);
        grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
        grassLabel.setVerticalAlignment(SwingConstants.CENTER);
        grassLabel.setIcon(grassIcon);
        grassLabel.setBounds(oldCol * CELL_SIZE, oldRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        mapCells[oldRow][oldCol] = grassLabel;
        mapPanel.add(grassLabel);

        // Reset the old cell's state in the grid
        Cell oldCell = grid.getCell(oldRow, oldCol);
        if (oldCell != null) {
            oldCell.setOccupied(false);
            oldCell.setCellType('-'); // Set to grass
        }

        // 2. Handle the new cell - place the animal
        // Get the current cell type at the new position
        char currentCellType = grid.getCellType(newX, newY);
        boolean isVegetation = (currentCellType == 'p' || currentCellType == 'b' || currentCellType == 'h');

        // Track old health for logging
        int oldHealth = 0;
        boolean wasAtMaxHealth = false;
        if (animal instanceof Herbivore) {
            Herbivore herbivore = (Herbivore) animal;
            oldHealth = herbivore.getHealth();
            wasAtMaxHealth = herbivore.isAtMaxHealth();
        }

        // Remove any existing label at the new position
        if (mapCells[newX][newY] != null) {
            mapPanel.remove(mapCells[newX][newY]);
        }

        // Create a new animal label
        AnimalLabel animalLabel = new AnimalLabel(animal, CELL_SIZE);

        // Determine which icon to use based on animal type
        if (animal instanceof Herbivore) {
            String species = animal.getDescription().toLowerCase();
            if (species.contains("cow")) {
                animalLabel.setIcon(cowIcon);
            } else if (species.contains("deer")) {
                animalLabel.setIcon(deerIcon);
            }
        } else if (animal instanceof Carnivore) {
            String species = animal.getDescription().toLowerCase();
            if (species.contains("lion")) {
                animalLabel.setIcon(lionIcon);
            } else if (species.contains("wolf")) {
                animalLabel.setIcon(wolfIcon);
            }
        }

        // Position the animal label
        animalLabel.setBounds(newY * CELL_SIZE, newX * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Store and add the new animal label
        mapCells[newX][newY] = animalLabel;
        mapPanel.add(animalLabel);

        // Set the new cell state in the grid
        Cell newCell = grid.getCell(newX, newY);
        if (newCell != null) {
            newCell.setOccupied(true);

            // Set cell type based on animal type
            if (animal instanceof Herbivore) {
                String species = animal.getDescription().toLowerCase();
                if (species.contains("cow")) {
                    grid.setCellType(newX, newY, 'c');
                } else if (species.contains("deer")) {
                    grid.setCellType(newX, newY, 'd');
                }
            } else if (animal instanceof Carnivore) {
                String species = animal.getDescription().toLowerCase();
                if (species.contains("lion")) {
                    grid.setCellType(newX, newY, 'y');
                } else if (species.contains("wolf")) {
                    grid.setCellType(newX, newY, 'z');
                }
            }
        }

        // If herbivore, check if it can eat at new position
        if (animal instanceof Herbivore) {
            Herbivore herbivore = (Herbivore) animal;

            // If we moved onto vegetation, handle eating
            if (isVegetation) {
                // Check if already at max health
                if (wasAtMaxHealth) {
                    System.out.println("Animal at max health, can't increase further");

                    // Just call grazePlant for animation purposes
                    herbivore.grazePlant(currentCellType);

                    // Show max health indicator
                    animalLabel.showMaxHealthReached();
                } else {
                    herbivore.grazePlant(currentCellType);
                    System.out.println("Herbivore is eating vegetation at: (" + newX + ", " + newY + ")");
                    System.out.println("Health increased from " + oldHealth + " to " + herbivore.getHealth()
                            + " (+" + (herbivore.getHealth() - oldHealth) + ")");
                }
            } // Otherwise check if it can eat grass
            else if (currentCellType == '-') {
                // Check if already at max health
                if (wasAtMaxHealth) {
                    System.out.println("Animal at max health, can't increase further");

                    // Just call graze for animation purposes
                    herbivore.graze();

                    // Show max health indicator
                    animalLabel.showMaxHealthReached();
                } else {
                    // Just regular grazing on grass
                    herbivore.graze();
                    System.out.println("Herbivore is eating grass at: (" + newX + ", " + newY + ")");
                    System.out.println("Health increased from " + oldHealth + " to " + herbivore.getHealth()
                            + " (+" + (herbivore.getHealth() - oldHealth) + ")");
                }
            }
        }

        currentCellType = grid.getCellType(newX, newY);
        if (currentCellType == 'w' && animal.isThirsty()) {
            // Animal is on water and thirsty
            WaterArea waterArea = findWaterAreaAt(newX, newY);
            if (waterArea != null) {
                // Start drinking
                if (animal instanceof Herbivore) {
                    ((Herbivore) animal).drinkFromWaterArea(waterArea);
                } else if (animal instanceof Carnivore) {
                    ((Carnivore) animal).drinkFromWaterArea(waterArea);
                }

                // Update visual representation
                if (mapCells[newX][newY] instanceof AnimalLabel) {
//                    AnimalLabel animalLabel;
                    animalLabel = (AnimalLabel) mapCells[newX][newY];
                    animalLabel.showDrinkingAnimation();
                    animalLabel.updateThirstIndicator();
                }

                System.out.println(animal.getDescription() + " found water at new position and started drinking");
            }
        }

        // Make sure all components are properly updated visually
        animalLabel.revalidate();
        animalLabel.repaint();

        // Ensure the panel is updated
        mapPanel.revalidate();
        mapPanel.repaint();
        // At the end of the method
        notifyMiniMapUpdate();
    }

    public JButton createDecreaseHealthButton() {
        JButton button = new JButton("Decrease Animal Health");
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        button.setBackground(new Color(220, 100, 100));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        button.addActionListener(e -> {
            // Find all animals on the map
            for (int row = 0; row < GRID_ROWS; row++) {
                for (int col = 0; col < GRID_COLS; col++) {
                    if (mapCells[row][col] instanceof AnimalLabel) {
                        AnimalLabel animalLabel = (AnimalLabel) mapCells[row][col];
                        Animal animal = animalLabel.getAnimal();

                        if (animal instanceof Herbivore) {
                            Herbivore herbivore = (Herbivore) animal;
                            // Decrease health by 20%
                            herbivore.decreaseHealth(20);
                            // Force repaint
                            animalLabel.repaint();
                        }
                    }
                }
            }
        });

        return button;
    }

// In MapView.java - Modify the placeItem method to handle positioning properly
    private void placeItem(int row, int col) {
        // First, properly update the grid
        Cell cell = grid.getCell(row, col);
        if (cell == null) {
            System.err.println("Error: Invalid cell at position (" + row + ", " + col + ")");
            return;
        }

        // Mark the cell as occupied early
        cell.setOccupied(true);

        // Debug output - before placement
        System.out.println("Before placement - Grid at (" + row + "," + col + "): "
                + "Type=" + grid.getCellType(row, col)
                + ", Occupied=" + grid.isOccupied(row, col));

        startTouristArrivalTimer();

        // Set specific cell type based on the item type
        if (itemToPlace instanceof Plant) {
            // Update grid with plant type
            char cellType = getPlanCellTypeChar(itemToPlace.getDescription());
            grid.setCellType(row, col, cellType);
            mapCells[row][col].setIcon(placementIcon);

            // Update the plant's position - Plants use a Coordinate constructor rather than setPosition
            ((Plant) itemToPlace).position = new Coordinate(row, col);

        } else if (itemToPlace instanceof Road) {
            // Update grid with road type
            char cellType = getRoadCellTypeChar(((Road) itemToPlace).getRoadType());
            grid.setCellType(row, col, cellType);
            mapCells[row][col].setIcon(placementIcon);

            // Update the road's position
            ((Road) itemToPlace).position = new Coordinate(row, col);

        } else if (itemToPlace instanceof Herbivore) {
            Animal animal = (Animal) itemToPlace;
            // Set grid cell type for this herbivore
            grid.setCellType(row, col, getAnimalCellTypeChar(((Herbivore) itemToPlace).getDescription()));

            // Create AnimalLabel for health bar display
            AnimalLabel animalLabel = new AnimalLabel(animal, CELL_SIZE);
            animalLabel.setIcon(placementIcon);

            // Update UI
            mapPanel.remove(mapCells[row][col]);
            mapCells[row][col] = animalLabel;
            animalLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            mapPanel.add(animalLabel);

            // Set position on the animal object - Animals have setPosition method
            animal.setPosition(row, col);

            // Add to tracking collections
            allAnimals.add(animal);

            // Start animal movement
            startAnimalMovement(row, col, animal);

        } else if (itemToPlace instanceof Carnivore) {
            Animal animal = (Animal) itemToPlace;
            // Set grid cell type for this carnivore
            grid.setCellType(row, col, getAnimalCellTypeChar(((Carnivore) itemToPlace).getDescription()));

            // Create AnimalLabel
            AnimalLabel animalLabel = new AnimalLabel(animal, CELL_SIZE);
            animalLabel.setIcon(placementIcon);

            // Update UI
            mapPanel.remove(mapCells[row][col]);
            mapCells[row][col] = animalLabel;
            animalLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            mapPanel.add(animalLabel);

            // Set position on the animal object
            animal.setPosition(row, col);

            // Add to tracking collections
            allAnimals.add(animal);

            // Start animal movement
            startAnimalMovement(row, col, animal);

        } else if (itemToPlace instanceof WaterArea) {
            // Update grid with water type
            grid.setCellType(row, col, 'P');

            // Update UI
            mapPanel.remove(mapCells[row][col]);
            JLabel waterLabel = new JLabel();
            waterLabel.setSize(CELL_SIZE, CELL_SIZE);
            waterLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
            waterLabel.setHorizontalAlignment(SwingConstants.CENTER);
            waterLabel.setVerticalAlignment(SwingConstants.CENTER);
            waterLabel.setIcon(placementIcon);
            waterLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            mapCells[row][col] = waterLabel;
            mapPanel.add(waterLabel);

            // Update the water area's position
            ((WaterArea) itemToPlace).position = new Coordinate(row, col);

        } else if (itemToPlace instanceof Ranger) {
            Ranger ranger = (Ranger) itemToPlace;
            // Update grid with ranger type
            grid.setCellType(row, col, 'R');
            mapCells[row][col].setIcon(rangerIcon);

            // Set ranger position
            ranger.setPosition(row, col);

            // Start ranger movement
            startRangerMovement(ranger);

        } else if (itemToPlace instanceof Jeep) {
            // Special handling for Jeep with fixed position
            int jeepRow = 48;
            int jeepCol = 7;

            // Get cell at jeep position and update
            Cell jeepCell = grid.getCell(jeepRow, jeepCol);
            jeepCell.setOccupied(true);

            // Update grid
            grid.setCellType(jeepRow, jeepCol, 'j');

            Jeep jeep = (Jeep) itemToPlace;
            char currentTileType = grid.getCellType(jeepRow, jeepCol);

            mapCells[jeepRow][jeepCol].setIcon(placementIcon);

            // Set jeep position
            jeep.setPosition(jeepRow, jeepCol);
            jeep.setLastPosition(new Coordinate(jeepRow, jeepCol));

            startJeepMovement(jeepRow, jeepCol, jeep, currentTileType);
        }

        // Debug output - after placement
        System.out.println("After placement - Grid at (" + row + "," + col + "): "
                + "Type=" + grid.getCellType(row, col)
                + ", Occupied=" + grid.isOccupied(row, col));

        // Exit placement mode
        disablePlacementMode();

        // Notify controller to update the model
        if (controller != null) {
            controller.onItemPlaced(itemToPlace, row, col);
        }
    }

// Add a helper method to print the local grid state for debugging
    private void printLocalGridState(int centerRow, int centerCol) {
        System.out.println("Local grid state around (" + centerRow + "," + centerCol + "):");

        int radius = 1; // Check 1 cell in each direction
        for (int r = centerRow - radius; r <= centerRow + radius; r++) {
            if (r < 0 || r >= GRID_ROWS) {
                continue;
            }

            StringBuilder line = new StringBuilder();
            for (int c = centerCol - radius; c <= centerCol + radius; c++) {
                if (c < 0 || c >= GRID_COLS) {
                    continue;
                }

                line.append("(").append(r).append(",").append(c).append("): ");
                line.append("Type=").append(grid.getCellType(r, c));
                line.append(", Occupied=").append(grid.isOccupied(r, c));
                line.append("  ");
            }
            System.out.println(line.toString());
        }
    }

    private void startRangerMovement(Ranger ranger) {
        Coordinate position = ranger.getPosition();
        System.out.println("Starting ranger movement at position: " + position);

        Timer timer = new Timer(controller.getMovementDelayBasedOnGameSpeed(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Timer triggered to move ranger.");
                moveRangerRandomly(ranger);
            }
        });

        timer.start();
        System.out.println("Timer started for ranger movement.");
    }

    private void moveRangerRandomly(Ranger ranger) {
        Coordinate position = ranger.getPosition();
        int x = position.getX();
        int y = position.getY();

        // First check for nearby carnivores to attack
        boolean killedCarnivore = rangerKillsNearbyCarnivores(ranger);

        // If we killed a carnivore, skip movement this turn
        if (killedCarnivore) {
            return;
        }

        // Normal random movement if no carnivores were nearby
        int[] dx = {-1, 1, 0, 0}; // directions
        int[] dy = {0, 0, -1, 1};

        int randomDirection = (int) (Math.random() * 4);
        int newX = x + dx[randomDirection];
        int newY = y + dy[randomDirection];

        if (isValidCell(newX, newY) && !grid.getCell(newX, newY).isOccupied()) {
            moveRangerTo(ranger, newX, newY);
        } else {
            System.out.println("Ranger move invalid, skipping move.");
        }
    }

    private boolean rangerKillsNearbyCarnivores(Ranger ranger) {
        Coordinate rangerPos = ranger.getPosition();
        int x = rangerPos.getX();
        int y = rangerPos.getY();

        System.out.println("Ranger at (" + x + "," + y + ") checking for carnivores nearby");

        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        boolean killedAnyCarnivore = false;

        for (int i = 0; i < dx.length; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            // Ensure coordinates are within bounds
            if (newX >= 0 && newX < GRID_ROWS && newY >= 0 && newY < GRID_COLS) {
                // Check if cell contains a carnivore
                char cellType = grid.getCellType(newX, newY);
                if (cellType == 'y' || cellType == 'z') {  // Lion or wolf

                    // Get the animal at this position
                    if (mapCells[newX][newY] instanceof AnimalLabel) {
                        AnimalLabel animalLabel = (AnimalLabel) mapCells[newX][newY];
                        Animal animal = animalLabel.getAnimal();

                        if (animal instanceof Carnivore) {
                            Carnivore carnivore = (Carnivore) animal;

                            // IMPORTANT: Only attack if carnivore is still alive and not being removed
                            if (carnivore.getHealth() > 0 && !carnivore.isBeingRemoved) {
                                System.out.println("Ranger found living carnivore at (" + newX + "," + newY + ")");

                                // Record health before attack
                                int oldHealth = carnivore.getHealth();

                                // Set health to 0 - this "kills" the carnivore
                                carnivore.setHealth(0);

                                // Show attack animation on the carnivore
                                animalLabel.showAttackAnimation();

                                // Use our improved removal method
                                fixRemoveDeadAnimal(newX, newY, carnivore);

                                System.out.println("Ranger killed carnivore: Health changed from "
                                        + oldHealth + " to 0");

                                killedAnyCarnivore = true;
                            }
                        }
                    }
                }
            }
        }

        return killedAnyCarnivore;
    }

    private void moveRangerTo(Ranger ranger, int newX, int newY) {
        Coordinate currentPos = ranger.getPosition();
        int oldX = currentPos.getX();
        int oldY = currentPos.getY();

        // Clear old position
        grid.setCellType(oldX, oldY, '-');
        grid.getCell(oldX, oldY).setOccupied(false);
        mapCells[oldX][oldY].setIcon(grassIcon);

        // Update to new position
        ranger.setPosition(newX, newY);
        grid.setCellType(newX, newY, 'R');
        grid.getCell(newX, newY).setOccupied(true);
        mapCells[newX][newY].setIcon(rangerIcon);

        mapPanel.repaint();
    }

    private List<Coordinate> findPath(int startX, int startY, int targetX, int targetY) {
        if (!isValidJeepCell(startX, startY)) {
            System.out.println("Starting point is a gate, adjusting to nearest road.");
            Coordinate adjustedStart = findNearestRoad(startX, startY);
            startX = adjustedStart.getX();
            startY = adjustedStart.getY();
        }

        if (!isValidJeepCell(targetX, targetY)) {
            System.out.println("Target point is a gate, adjusting to nearest road.");
            Coordinate adjustedTarget = findNearestRoad(targetX, targetY);
            targetX = adjustedTarget.getX();
            targetY = adjustedTarget.getY();
        }
        Queue<Coordinate> queue = new LinkedList<>();
        boolean[][] visited = new boolean[GRID_ROWS][GRID_COLS];
        Coordinate[][] parent = new Coordinate[GRID_ROWS][GRID_COLS];  // To track the parent coordinates

        // Log the start and target to confirm the coordinates
        System.out.println("Starting Point: (" + startX + ", " + startY + ")");
        System.out.println("Target Point: (" + targetX + ", " + targetY + ")");

        // Add the start coordinate to the queue
        queue.add(new Coordinate(startX, startY));
        visited[startX][startY] = true;
        parent[startX][startY] = null;  // Start has no parent

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right

        // If start is the same as the target, return immediately
        if (startX == targetX && startY == targetY) {
            List<Coordinate> immediatePath = new ArrayList<>();
            immediatePath.add(new Coordinate(startX, startY));
            return immediatePath;
        }

        // Perform BFS
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            System.out.println("Visiting: (" + current.getX() + ", " + current.getY() + ")"); // Debugging visit

            if (current.getX() == targetX && current.getY() == targetY) {
                System.out.println("Target reached: (" + current.getX() + ", " + current.getY() + ")");
                break; // Reached destination
            }

            // Check all 4 directions
            for (int[] dir : directions) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];

                // If valid and not visited
                if (isValidJeepCell(newX, newY) && !visited[newX][newY]) {
                    visited[newX][newY] = true;
                    Coordinate neighbor = new Coordinate(newX, newY);
                    queue.add(neighbor);
                    parent[newX][newY] = current;  // Store the parent of this coordinate
                    System.out.println("Added to queue: (" + newX + ", " + newY + ")");
                }
            }
        }

        // Now reconstruct the path from the target to the start
        List<Coordinate> path = new ArrayList<>();
        Coordinate current = new Coordinate(targetX, targetY);

// Debug log: Check if the target is reachable
        System.out.println("Target: (" + targetX + ", " + targetY + ") -> Parent: " + parent[targetX][targetY]);

// If target is not reachable, return an empty path
        if (parent[targetX][targetY] == null) {
            System.out.println("No path found. The target is not reachable.");
            return path;
        }

// Reconstruct the path by following the parent links
        while (current != null) {
            // Log the current coordinate
            System.out.println("Adding to path: (" + current.getX() + ", " + current.getY() + ")");
            path.add(current);

            // Debug log: Print parent of the current coordinate
            if (current.getX() >= 0 && current.getY() >= 0 && current.getX() < GRID_ROWS && current.getY() < GRID_COLS) {
                System.out.println("Parent of (" + current.getX() + ", " + current.getY() + "): " + parent[current.getX()][current.getY()]);
            }

            // Move to the parent coordinate
            current = parent[current.getX()][current.getY()];
        }

// After exiting the loop, log whether the current was null and check the path list
        System.out.println("Exited while loop. Current: " + current);
        System.out.println("Path before reversing:");
        for (Coordinate coord : path) {
            System.out.println("(" + coord.getX() + ", " + coord.getY() + ")");
        }

// Reverse the list to go from start to end
        Collections.reverse(path);

// Log the final path
        System.out.println("Reversed Path:");
        for (Coordinate coord : path) {
            System.out.println("(" + coord.getX() + ", " + coord.getY() + ")");
        }

        return path;

    }

    public List<Animal> getNearbyAnimals(int centerX, int centerY, int radius) {
        Set<Coordinate> seen = new HashSet<>();
        List<Animal> nearby = new ArrayList<>();

        for (Animal animal : allAnimals) {
            Coordinate pos = animal.getPosition();
            int dx = Math.abs(pos.getX() - centerX);
            int dy = Math.abs(pos.getY() - centerY);

            if (dx <= radius && dy <= radius) {
                // Prevent duplicate positions from being counted twice
                if (seen.add(pos)) {
                    nearby.add(animal);
                    System.out.println("Found " + animal.getDescription() + " at (" + pos.getX() + ", " + pos.getY() + ")");
                }
            }
        }

        return nearby;
    }

    public int calculateSatisfactionScore(List<Animal> nearbyAnimals) {
        Set<String> uniqueSpecies = new HashSet<>();
        for (Animal a : nearbyAnimals) {
            uniqueSpecies.add(a.getDescription());
        }

        int speciesCount = uniqueSpecies.size();
        int totalAnimals = nearbyAnimals.size();

        // You can weight diversity more if needed
        return (speciesCount * 50) + totalAnimals;
    }

    int capital = 0;

    private void updateJeepOnMap(Jeep jeep, int newX, int newY, boolean isForward) {
//        System.out.println("Updating jeep position to: (" + newX + ", " + newY + ")");

        Coordinate oldPos = jeep.getLastPosition();
        char oldType = grid.getCellType(oldPos.getX(), oldPos.getY());

        grid.getCell(oldPos.getX(), oldPos.getY()).setOccupied(false);
        mapCells[oldPos.getX()][oldPos.getY()].setIcon(getRoadIcon(oldType));
//        System.out.println("Cleared old position at: (" + oldPos.getX() + ", " + oldPos.getY() + ")");

        jeep.moveTo(new Coordinate(newX, newY));
        char newType = grid.getCellType(newX, newY);

        // Use different icons based on moving direction
        if (newType == 'r') {
            mapCells[newX][newY].setIcon(isForward ? jeepRight : jeepRightReverse);
        } else if (newType == '|' || newType == 's') {
            mapCells[newX][newY].setIcon(isForward ? jeepStraight : jeepStraightReverse);
        }

        grid.getCell(newX, newY).setOccupied(true);
        jeep.setLastPosition(new Coordinate(newX, newY));
//        System.out.println("Moved jeep to: (" + newX + ", " + newY + ")");

        List<Animal> animalsInRange = getNearbyAnimals(newX, newY, 20);

        mapPanel.repaint();
    }

    private void startTouristArrivalTimer() {
        Timer touristTimer = new Timer(5000, e -> {
            waitingTourists++;
            System.out.println("Tourist arrived. Total: " + waitingTourists);

            if (controller != null) {
                controller.updateTouristDisplay(waitingTourists);  // ✅ Move it inside the timer action
            }
        });
        touristTimer.start();
    }

    private void startJeepMovement(int row, int col, Jeep jeep, char startCellType) {
        List<Coordinate> path = findPath(row, col, 0, 39);

        if (path.isEmpty()) {
            return;
        }

        Timer timer = new Timer(controller.getMovementDelayBasedOnGameSpeed(), null);
        final int[] currentIndex = {0};
        final boolean[] forward = {true};

        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When jeep reaches end/start of path
                if (currentIndex[0] < 0 || currentIndex[0] >= path.size()) {
                    timer.stop();

                    List<Animal> animalsInRange = getNearbyAnimals(jeep.getPosition().getX(), jeep.getPosition().getY(), 20);
                    int earned = calculateSatisfactionScore(animalsInRange);

                    if (controller != null) {
                        controller.handleJeepCapitalUpdate(jeep.getPosition());
                    }

                    // Wait until 4 tourists arrive before moving again
                    Timer checkTouristsTimer = new Timer(500, null);
                    checkTouristsTimer.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            if (waitingTourists >= 4) {
                                waitingTourists -= 4;
                                System.out.println("Jeep is starting with 4 tourists.");

                                forward[0] = !forward[0];
                                currentIndex[0] = forward[0] ? 0 : path.size() - 1;

                                checkTouristsTimer.stop();
                                timer.start();
                            } else {
                                System.out.println("Waiting for tourists... Current: " + waitingTourists);
                            }
                        }
                    });
                    checkTouristsTimer.start();

                    return;
                }

                Coordinate next = path.get(currentIndex[0]);
                int newX = next.getX();
                int newY = next.getY();

                if (jeep.getPosition().getX() != newX || jeep.getPosition().getY() != newY) {
                    updateJeepOnMap(jeep, newX, newY, forward[0]);
                    jeep.setPosition(newX, newY);
                }

                if (forward[0]) {
                    currentIndex[0]++;
                } else {
                    currentIndex[0]--;
                }
            }
        });

        // 🚫 Don't start movement right away — wait for 4 tourists at the beginning
        Timer initialWait = new Timer(500, null);
        initialWait.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (waitingTourists >= 4) {
                    waitingTourists -= 4;
                    System.out.println("Initial jeep start with 4 tourists.");
                    controller.updateTouristDisplay(waitingTourists);

                    initialWait.stop();
                    timer.start();
                } else {
                    System.out.println("Waiting for tourists at start... Current: " + waitingTourists);
                }
            }
        });
        initialWait.start();
    }

    private Icon getRoadIcon(char roadType) {
        // This method returns the appropriate road icon based on the road type.
        if (roadType == 'r') {
            return horizontalRoadIcon; // Horizontal road icon
        } else if (roadType == '|') {
            return verticalRoadIcon; // Vertical road icon
        } else if (roadType == 's') {
            return verticalRoadIcon; // Vertical road icon
        }
        return null; // Default or unknown road type
    }

    private Coordinate findNearestRoad(int startX, int startY) {
        // Directions to check (up, down, left, right)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        Queue<Coordinate> queue = new LinkedList<>();
        boolean[][] visited = new boolean[GRID_ROWS][GRID_COLS];

        queue.add(new Coordinate(startX, startY));
        visited[startX][startY] = true;

        // Perform BFS to find the nearest road
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            // If we found a road tile, return it
            if (isValidJeepCell(current.getX(), current.getY())) {
                return current;
            }

            // Check all 4 directions
            for (int[] dir : directions) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];

                // If valid and not visited
                if (newX >= 0 && newX < GRID_ROWS && newY >= 0 && newY < GRID_COLS && !visited[newX][newY]) {
                    visited[newX][newY] = true;
                    queue.add(new Coordinate(newX, newY));
                }
            }
        }

        // Return the original coordinate if no road found (fallback, though this shouldn't happen)
        return new Coordinate(startX, startY);
    }

    private boolean isValidJeepCell(int row, int col) {
        if (row < 0 || row >= GRID_ROWS || col < 0 || col >= GRID_COLS) {
//            System.out.println("Cell (" + row + ", " + col + ") is out of bounds.");
            return false; // Out of bounds
        }

        char cellType = grid.getCellType(row, col);
        // Valid cells: Horizontal roads ('r') and vertical roads ('|')
        if (cellType == 'r' || cellType == '|') {
//            System.out.println("Cell (" + row + ", " + col + ") is a valid cell.");
            return true;
        } else {
//            System.out.println("Cell (" + row + ", " + col + ") is not a valid cell (not road).");
            return false;
        }
    }

    // Helper methods to determine cell type chars
    private char getPlanCellTypeChar(String description) {
        String desc = description.toLowerCase();
        if (desc.contains("bush")) {
            return 'b';
        }
        if (desc.contains("shrub")) {
            return 'h';
        }
        if (desc.contains("plant")) {
            return 'p';
        }
        return 'p'; // Default
    }

    private char getRoadCellTypeChar(String roadType) {
        String type = roadType.toLowerCase();
        if (type.equals("horizontal")) {
            return 'r';
        }
        if (type.equals("vertical")) {
            return '|';
        }
        if (type.contains("rightdown")) {
            return '1';
        }
        if (type.contains("rightup")) {
            return '2';
        }
        if (type.contains("leftup")) {
            return '3';
        }
        if (type.contains("leftdown")) {
            return '4';
        }
        if (type.contains("jeep")) {
            return 'j';
        }
        return 'r'; // Default
    }

    private char getAnimalCellTypeChar(String animalType) {
        String type = animalType.toLowerCase();
        if (type.equals("cow")) {
            return 'c';
        }
        if (type.equals("deer")) {
            return 'd';
        }
        if (type.contains("lion")) {
            return 'y';
        }
        if (type.contains("wolf")) {
            return 'z';
        }
        return 'r'; // Default
    }

    private boolean isRemovableItem(char cellType) {
        // Allow removing plants, roads, and other vegetation
        return cellType == 'b' || cellType == 'p' || cellType == 'h'
                || cellType == 'r' || cellType == '|'
                || cellType == '1' || cellType == '2' || cellType == '3' || cellType == '4' || cellType == 'w';
    }

    // Method to remove an item and add it to inventory
    private void removeItem(int row, int col, char cellType) {
        try {
            // Verify grid and controller exist
            if (grid == null || controller == null || controller.getModel() == null) {
                System.err.println("Cannot remove item: Grid or Controller is null");
                return;
            }

            SafariMap map = controller.getModel().getMap();
            Player player = controller.getModel().getPlayer();

            if (map == null || player == null) {
                System.err.println("Cannot remove item: Map or Player is null");
                return;
            }

            // Find the landscape object at this position
            TradeableItem removedItem = null;
            LandScapeObject objectToRemove = null;

            // Search through landscape objects
            for (Iterator<LandScapeObject> it = map.getLandscapeObjects().iterator(); it.hasNext();) {
                LandScapeObject obj = it.next();
                Coordinate pos = obj.getPosition();

                if (pos.x == row && pos.y == col) {
                    // Check if the object implements TradeableItem before casting
                    if (obj instanceof TradeableItem) {
                        removedItem = (TradeableItem) obj;
                        objectToRemove = obj;
                        it.remove(); // Remove from map's landscape objects
                        break;
                    }
                }
            }

            // If no object was found, create a new one based on the cell type
            if (removedItem == null) {
                removedItem = createItemFromCellType(row, col, cellType);
                System.out.print(cellType);
            }

            // Add the item to player's inventory
            if (removedItem != null) {
                // Use addItemToInventory instead of buyItem to avoid capital deduction
                player.addItemToInventory(removedItem);

                // Show success message
                showItemRemovedMessage(removedItem);
            }

            // Reset the cell in the grid
            grid.setCellType(row, col, '-'); // Set to grass
            Cell cell = grid.getCell(row, col);
            if (cell != null) {
                cell.setOccupied(false);
            }

            // Update the visual representation
            if (mapCells[row][col] != null) {
                mapCells[row][col].setIcon(grassIcon);
            }

            // Notify the controller about the item removal if needed
            if (controller != null) {
                controller.onItemRemoved(row, col);
            }
        } catch (Exception e) {
            System.err.println("Error removing item: " + e.getMessage());
            e.printStackTrace();
        }

        notifyMiniMapUpdate();
    }

    /**
     * Set the mini map that this map view will update
     */
    public void setMiniMapView(MiniMapView miniMapView) {
        this.miniMapView = miniMapView;

        if (miniMapView != null) {
            miniMapView.setNavigationCallback(this);
            miniMapView.setMapDimensions(MAP_WIDTH, MAP_HEIGHT);
            miniMapView.setMapPanelReference(mapPanel);

            // Share the map cells and cell size
            miniMapView.setMapCells(mapCells, CELL_SIZE, GRID_ROWS, GRID_COLS);

            updateMiniMap();
        }
    }

    // Helper method to create an item based on cell type
    private TradeableItem createItemFromCellType(int row, int col, char cellType) {
        switch (cellType) {
            case 'b': // Bush
                return new Plant(new Coordinate(row, col), "bush", 100.0);
            case 'p': // Plant
                return new Plant(new Coordinate(row, col), "plant", 150.0);
            case 'h': // Shrub
                return new Plant(new Coordinate(row, col), "shrub", 200.0);
            case 'r': // Horizontal Road
                return new Road(new Coordinate(row, col), 1.0, 50.0, "horizontal");
            case '|': // Vertical Road
                return new Road(new Coordinate(row, col), 1.0, 50.0, "vertical");
            case '1': // RightDown Road
                return new Road(new Coordinate(row, col), 1.0, 75.0, "rightDown");
            case '2': // RightUp Road
                return new Road(new Coordinate(row, col), 1.0, 75.0, "rightUp");
            case '3': // LeftUp Road
                return new Road(new Coordinate(row, col), 1.0, 75.0, "leftUp");
            case '4': // LeftDown Road
                return new Road(new Coordinate(row, col), 1.0, 75.0, "leftDown");
            case 'w':
                return new WaterArea(new Coordinate(row, col), 100); // initial capacity or whatever you want);

            default:
                return null;
        }
    }

    /**
     * Load terrain image icons
     */
    private void loadTerrainImages() {
        try {
            // Load images from resources
            wallIcon = loadAndResizeImage("/tiles/wall1.png");
            grassIcon = loadAndResizeImage("/tiles/grass.png");
            awallIcon = loadAndResizeImage("/tiles/wall4.png");
            swallIcon = loadAndResizeImage("/tiles/wall2.png");
            lwallIcon = loadAndResizeImage("/tiles/wall3.png");
            tgateIcon = loadAndResizeImage("/tiles/gatet.png");
            bgateIcon = loadAndResizeImage("/tiles/gateb.png");
            plantIcon = loadAndResizeImage("/tiles/grass2.png");
            shrubIcon = loadAndResizeImage("/tiles/grass3.png");
            bushIcon = loadAndResizeImage("/tiles/grass1.png");

            horizontalRoadIcon = loadAndResizeImage("/tiles/road-4.png");
            verticalRoadIcon = loadAndResizeImage("/tiles/road-2.png");
            rightupRoadIcon = loadAndResizeImage("/tiles/road-5.png");
            rightdownRoadIcon = loadAndResizeImage("/tiles/road-1.png");
            leftupRoadIcon = loadAndResizeImage("/tiles/road-3.png");
            leftdownRoadIcon = loadAndResizeImage("/tiles/road-6.png");

            cowIcon = loadAndResizeImage("/tiles/cow.png");
            deerIcon = loadAndResizeImage("/tiles/deer.png");
            lionIcon = loadAndResizeImage("/tiles/lion.png");
            wolfIcon = loadAndResizeImage("/tiles/wolf.png");

            pondIcon = loadAndResizeImage("/tiles/pond1.png");

            rangerIcon = loadAndResizeImage("/tiles/ranger.png");

            jeepIcon = loadAndResizeImage("/tiles/jeep.png");

            mountainIcon = loadAndResizeImage("/tiles/jeep1.png");

            jeepStraight = loadAndResizeImage("/tiles/jeep.png");
            jeepRight = loadAndResizeImage("/tiles/jeep-4.png");
            jeep_1 = loadAndResizeImage("/tiles/jeep-1.png");
            jeep_2 = loadAndResizeImage("/tiles/jeep-2.png");
            jeep_3 = loadAndResizeImage("/tiles/jeep-3.png");
            jeep_4 = loadAndResizeImage("/tiles/jeep-4.png");

            jeepStraightReverse = loadAndResizeImage("/tiles/jeepStraightReverse.png");
            jeepRightReverse = loadAndResizeImage("/tiles/jeepRightReverse.png");

            hillIcon = loadAndResizeImage("/tiles/hill.png");
            riverIcon = loadAndResizeImage("/tiles/river.png");

        } catch (Exception e) {
            // Fallback to colored squares if images can't be loaded
            wallIcon = createColorIcon(Color.DARK_GRAY);
            grassIcon = createColorIcon(Color.GREEN);
            awallIcon = createColorIcon(Color.DARK_GRAY);
            swallIcon = createColorIcon(Color.DARK_GRAY);
            lwallIcon = createColorIcon(Color.DARK_GRAY);
            tgateIcon = createColorIcon(Color.LIGHT_GRAY);
            bgateIcon = createColorIcon(Color.LIGHT_GRAY);
        }
    }

    /**
     * Get the scroll pane containing the map for display
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * Create a colored square icon as fallback
     */
    private ImageIcon createColorIcon(Color color) {
        BufferedImage img = new BufferedImage(CELL_SIZE, CELL_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Load and resize an image to cell size
     */
    private ImageIcon loadAndResizeImage(String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            // Use SCALE_SMOOTH for better quality resizing
            Image img = icon.getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return createColorIcon(Color.MAGENTA); // Error color as fallback
        }
    }
    // Display a message when an item is successfully removed

    private void showItemRemovedMessage(TradeableItem item) {
        JDialog successDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mapPanel), false);
        successDialog.setUndecorated(true);
        successDialog.setBackground(new Color(0, 0, 0, 0));

        JPanel messagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create rounded rectangle with gradient
                int w = getWidth();
                int h = getHeight();
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRoundRect(0, 0, w, h, 20, 20);

                // Add border
                g2d.setColor(new Color(0, 200, 0, 200));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, w - 3, h - 3, 20, 20);
            }
        };

        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        messagePanel.setOpaque(false);

        String itemName = item.getDescription();
        // Capitalize first letter of item name for nicer display
        if (itemName != null && !itemName.isEmpty()) {
            itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
        }

        JLabel messageLabel = new JLabel("Removed: " + itemName + " (Added to inventory)");
        messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);

        messagePanel.add(messageLabel, BorderLayout.CENTER);

        successDialog.setContentPane(messagePanel);
        successDialog.pack();
        successDialog.setLocationRelativeTo(mapPanel);

        // Show the dialog and close it after a short delay
        successDialog.setVisible(true);

        Timer timer = new Timer(1500, e -> successDialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Center the viewport in the middle of the map
     */
    private void centerViewport() {
        try {
            // Calculate center position
            int viewportWidth = scrollPane.getViewport().getWidth();
            int viewportHeight = scrollPane.getViewport().getHeight();

            // If viewport dimension is 0, use the preferred size
            if (viewportWidth <= 0) {
                viewportWidth = scrollPane.getPreferredSize().width;
            }
            if (viewportHeight <= 0) {
                viewportHeight = scrollPane.getPreferredSize().height;
            }

            int mapWidth = mapPanel.getPreferredSize().width;
            int mapHeight = mapPanel.getPreferredSize().height;

            viewportX = Math.max(0, (mapWidth - viewportWidth) / 2);
            viewportY = Math.max(0, (mapHeight - viewportHeight) / 2);

            // Update the view position
            scrollTo(viewportX, viewportY);
        } catch (Exception e) {
            System.err.println("Error in centerViewport: " + e.getMessage());
        }
    }

    /**
     * Handle keyboard navigation
     */
    private void handleKeyNavigation(KeyEvent e) {
        int delta = MOVE_AMOUNT;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                moveViewport(-delta, 0);
                break;
            case KeyEvent.VK_RIGHT:
                moveViewport(delta, 0);
                break;
            case KeyEvent.VK_UP:
                moveViewport(0, -delta);
                break;
            case KeyEvent.VK_DOWN:
                moveViewport(0, delta);
                break;
        }
    }

    /**
     * Move the viewport by specific amounts
     */
    private void moveViewport(int deltaX, int deltaY) {
        try {
            // Get current viewport dimensions
            int viewportWidth = scrollPane.getViewport().getWidth();
            int viewportHeight = scrollPane.getViewport().getHeight();

            // Calculate new viewport position
            int newX = Math.max(0, Math.min(mapPanel.getPreferredSize().width - viewportWidth, viewportX + deltaX));
            int newY = Math.max(0, Math.min(mapPanel.getPreferredSize().height - viewportHeight, viewportY + deltaY));

            // If position changed, update
            if (newX != viewportX || newY != viewportY) {
                scrollTo(newX, newY);
            }
        } catch (Exception e) {
            System.err.println("Error in moveViewport: " + e.getMessage());
        }
    }

    /**
     * Scroll to a specific position
     */
    private void scrollTo(int x, int y) {
        try {
            // Update internal state
            viewportX = x;
            viewportY = y;

            // Update scroll position
            SwingUtilities.invokeLater(() -> {
                scrollPane.getViewport().setViewPosition(new Point(x, y));
                scrollPane.requestFocusInWindow();
                scrollPane.revalidate();
                scrollPane.repaint();
            });

            // Update mini map
            updateMiniMap();
        } catch (Exception e) {
            System.err.println("Error in scrollTo: " + e.getMessage());
        }
    }

    /**
     * Update the mini map to show current viewport
     */
    private void updateMiniMap() {
        if (miniMapView != null) {
            try {
                // Calculate exact viewport size in pixels
                int viewWidth = scrollPane.getViewport().getWidth();
                int viewHeight = scrollPane.getViewport().getHeight();

                // Update minimap viewport
                miniMapView.updateViewport(viewportX, viewportY, viewWidth, viewHeight);
            } catch (Exception e) {
                System.err.println("Error updating mini map: " + e.getMessage());
            }
        }
    }

    /**
     * Create the scroll pane to view a portion of the map
     */
    private void createScrollPane() {
        // Create scroll pane that shows only part of the map
        scrollPane = new JScrollPane(mapPanel);

        // Set the visible area size
        scrollPane.setPreferredSize(new Dimension(
                VISIBLE_CELLS * CELL_SIZE,
                VISIBLE_CELLS * CELL_SIZE));

        // Hide scrollbars but keep the scrollability
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Add keyboard navigation support
        scrollPane.setFocusable(true);
        scrollPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyNavigation(e);
            }
        });

        // Add mouse wheel support for scrolling
        scrollPane.addMouseWheelListener(e -> {
            if (!e.isControlDown()) {
                int rotation = e.getWheelRotation();
                if (rotation < 0) {
                    moveViewport(0, -MOVE_AMOUNT); // Scroll up
                } else {
                    moveViewport(0, MOVE_AMOUNT);  // Scroll down
                }
            }
        });

        // Track viewport position changes
        scrollPane.getViewport().addChangeListener(e -> {
            Point viewPos = scrollPane.getViewport().getViewPosition();
            if (viewportX != viewPos.x || viewportY != viewPos.y) {
                viewportX = viewPos.x;
                viewportY = viewPos.y;
                updateMiniMap();
            }
        });
    }

    // In MapView.java
    private boolean isNightMode = false;

// Call this to refresh the map display
    private void repaintMap() {
        if (mapPanel != null) {
            mapPanel.repaint();
        }
    }

// Modify the paintComponent method of cell labels or override the createMapContent method
// Here's a conceptual approach for modifying createMapContent:
    private void createMapContent() {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                // Create a precisely sized JLabel
                JLabel cell = new JLabel();

                // Set size properties
                cell.setSize(CELL_SIZE, CELL_SIZE);
                cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setVerticalAlignment(SwingConstants.CENTER);

                // Get the cell type from the grid
                char cellType = grid.getCellType(row, col);

                // Determine visibility based on night mode and cell type
                boolean isVisible = !isNightMode
                        || isVisibleAtNight(cellType, row, col);

                if (isVisible) {
                    // Set the appropriate icon based on cell type
                    setAppropriateIcon(cell, cellType);
                } else {
                    // Set dark/hidden icon
                    cell.setIcon(createDarkModeIcon());
                }

                // Store the cell
                mapCells[row][col] = cell;
            }
        }
    }

    private boolean isVisibleAtNight(char cellType, int row, int col) {
        // Plants, water, or roads are visible at night
        if (cellType == 'p' || cellType == 'b' || cellType == 'h'
                || // Plants
                cellType == '|' || cellType == 'r' || cellType == '1'
                || cellType == '2' || cellType == '3' || cellType == '4'
                || // Roads
                cellType == 'w') { // Water

//            System.out.println("Checking visibility for cell type: " + cellType);
            return true;
        }

        // Check if the cell contains an animal
        if (cellType == 'c' || cellType == 'd'
                || // Herbivores
                cellType == 'y' || cellType == 'z') { // Carnivores

            // Get the animal at this position
            Animal animal = getAnimalAt(row, col);

            if (animal != null) {
                // Check if it has a location chip
                if (animal.hasLocationChip()) {
                    return true;
                }

                // Check if it's near a tourist or ranger
                if (isNearTouristOrRanger(row, col)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Animal getAnimalAt(int row, int col) {
        // Iterate through landscape objects to find the animal at this position
        if (controller != null && controller.getModel() != null) {
            SafariMap map = controller.getModel().getMap();
            for (LandScapeObject obj : map.getLandscapeObjects()) {
                if (obj instanceof Animal) {
                    Animal animal = (Animal) obj;
                    Coordinate pos = animal.getPosition();
                    if (pos.x == row && pos.y == col) {
                        return animal;
                    }
                }
            }
        }
        return null;
    }

    private boolean isNearTouristOrRanger(int row, int col) {
        // Check if there's a tourist or ranger within a certain distance
        if (controller != null && controller.getModel() != null) {
            SafariMap map = controller.getModel().getMap();

            // Check tourists
            for (Tourist tourist : map.getTourists()) {
                Coordinate pos = tourist.getPosition();
                if (isWithinRange(row, col, pos.x, pos.y, 3)) { // Visible within 3 cells
                    return true;
                }
            }

            // Check rangers
            for (Ranger ranger : map.getRangers()) {
                Coordinate pos = ranger.getPosition();
                if (isWithinRange(row, col, pos.x, pos.y, 5)) { // Visible within 5 cells
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWithinRange(int row1, int col1, int row2, int col2, int range) {
        int rowDiff = Math.abs(row1 - row2);
        int colDiff = Math.abs(col1 - col2);
        return rowDiff <= range && colDiff <= range;
    }

    private ImageIcon createDarkModeIcon() {
        // Create a dark icon that matches the night overlay exactly
        BufferedImage img = new BufferedImage(CELL_SIZE, CELL_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // First draw the grass background
        g2d.drawImage(grassIcon.getImage(), 0, 0, CELL_SIZE, CELL_SIZE, null);

        // Then apply the exact same night overlay color as used in the panel
        Color nightColor = new Color(10, 10, 40, 180); // Match night overlay exactly
        g2d.setColor(nightColor);
        g2d.fillRect(0, 0, CELL_SIZE, CELL_SIZE);

        g2d.dispose();

        return new ImageIcon(img);
    }

    /**
     * Set the appropriate icon for a cell based on its type
     *
     * @param cell The JLabel to set the icon for
     * @param cellType The type character of the cell
     */
    private void setAppropriateIcon(JLabel cell, char cellType) {
        switch (cellType) {
            case 'w':
                cell.setIcon(wallIcon);
                break;
            case 'a':
                cell.setIcon(awallIcon);
                break;
            case 's':
                cell.setIcon(swallIcon);
                break;
            case 'l':
                cell.setIcon(lwallIcon);
                break;
            case 'g':
                cell.setIcon(tgateIcon);
                break;
            case 't':
                cell.setIcon(bgateIcon);
                break;
            case 'p':
                cell.setIcon(plantIcon);
                break;
            case 'h':
                cell.setIcon(shrubIcon);
                break;
            case 'b':
                cell.setIcon(bushIcon);
                break;
            case 'r':
                cell.setIcon(horizontalRoadIcon);
                break;
            case '|':
                cell.setIcon(verticalRoadIcon);
                break;
            case '1':
                cell.setIcon(rightdownRoadIcon);
                break;
            case '2':
                cell.setIcon(rightupRoadIcon);
                break;
            case '3':
                cell.setIcon(leftupRoadIcon);
                break;
            case '4':
                cell.setIcon(leftdownRoadIcon);
                break;
            case 'c':
                cell.setIcon(cowIcon);
                break;
            case 'd':
                cell.setIcon(deerIcon);
                break;
            case 'y':
                cell.setIcon(lionIcon);
                break;
            case 'z':
                cell.setIcon(wolfIcon);
                break;
            case 'j':
                cell.setIcon(jeepIcon);
                break;
            case 'R':
                cell.setIcon(rangerIcon);
                break;
            case 'P':
                cell.setIcon(pondIcon);
                //System.out.print("I AM HERE IN MAP VIEW");
                break;
            case 'H':
                cell.setIcon(hillIcon);
                break;
            case 'D':
                cell.setIcon(riverIcon);
                break;
            default:
                cell.setIcon(grassIcon);
                break;
        }
    }

    // In MapView.java
    // Update the updateDayNightStatus method to ensure overlay is on top
    public void updateDayNightStatus(boolean isDaytime) {
        this.isNightMode = !isDaytime;

        // Toggle the night overlay panel
        if (nightOverlayPanel != null) {
            System.out.println("Setting night overlay visibility to: " + !isDaytime);

            if (!isDaytime) {
                // Make sure overlay covers the entire map
                int width = mapPanel.getWidth();
                int height = mapPanel.getHeight();
                nightOverlayPanel.setBounds(0, 0, width, height);

                // Ensure overlay is at the top of the z-order
                mapPanel.setComponentZOrder(nightOverlayPanel, 0);
                // Bring to front explicitly 
                nightOverlayPanel.getParent().setComponentZOrder(nightOverlayPanel, 0);
            }

            // Set visibility last to ensure proper rendering
            nightOverlayPanel.setVisible(!isDaytime);
            nightOverlayPanel.repaint();
        }

        // Force a complete repaint
        mapPanel.revalidate();
        mapPanel.repaint();

        if (scrollPane != null) {
            scrollPane.revalidate();
            scrollPane.repaint();
        }

        notifyMiniMapUpdate();

        // Log for debugging
        System.out.println("Night mode updated: " + isNightMode);
        System.out.println("Night overlay visible: "
                + (nightOverlayPanel != null ? nightOverlayPanel.isVisible() : "null"));
    }

    // Method to move an animal to a specific position
    private void moveAnimalTo(Animal animal, int newX, int newY) {
        if (animal.isBeingRemoved) {
            return;
        }

        Coordinate currentPos = animal.getPosition();
        int oldX = currentPos.getX();
        int oldY = currentPos.getY();

        // First, handle clearing the old cell correctly
        if (mapCells[oldX][oldY] != null) {
            mapPanel.remove(mapCells[oldX][oldY]);
        }

        // Create a grass label for the old position
        JLabel grassLabel = new JLabel();
        grassLabel.setSize(CELL_SIZE, CELL_SIZE);
        grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
        grassLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Handle night mode properly for the old cell
        if (isNightMode) {
            grassLabel.setIcon(createDarkModeIcon());
        } else {
            grassLabel.setIcon(grassIcon);
        }

        grassLabel.setBounds(oldY * CELL_SIZE, oldX * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Add to map and update grid
        mapCells[oldX][oldY] = grassLabel;
        mapPanel.add(grassLabel);
        grid.setCellType(oldX, oldY, '-');
        grid.getCell(oldX, oldY).setOccupied(false);

        // Now update the animal's position using the normal method
        updateAnimalOnMap(animal, newX, newY);

        // Update minimap
        notifyMiniMapUpdate();
    }

    private void moveFollowerTowardLeader(Animal follower, int leaderX, int leaderY) {
        Coordinate followerPos = follower.getPosition();
        int fx = followerPos.getX();
        int fy = followerPos.getY();

        int dx = Integer.compare(leaderX, fx); // -1, 0, or 1
        int dy = Integer.compare(leaderY, fy); // -1, 0, or 1

        int newFollowerX = fx + dx;
        int newFollowerY = fy + dy;

        if (isValidCell(newFollowerX, newFollowerY) && !grid.getCell(newFollowerX, newFollowerY).isOccupied()) {
            moveAnimalTo(follower, newFollowerX, newFollowerY);
        }
    }

    private void moveAlone(Animal animal) {
        Coordinate position = animal.getPosition();
        int x = position.getX();
        int y = position.getY();

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        int randomDirection = (int) (Math.random() * 4);
        int newX = x + dx[randomDirection];
        int newY = y + dy[randomDirection];

        if (isValidCell(newX, newY) && !grid.getCell(newX, newY).isOccupied()) {
            moveAnimalTo(animal, newX, newY);
        } else {
            System.out.println("Animal move invalid, staying in place.");
        }
    }

    private List<Animal> findNearbySameSpeciesCarnivores(Animal leader) {
        List<Animal> nearbyCarnivores = new ArrayList<>();
        Coordinate pos = leader.getPosition();
        int x = pos.getX();
        int y = pos.getY();

        System.out.println("Looking for nearby same-species carnivores around (" + x + "," + y + ")...");

        // Check all 8 surrounding cells
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue; // Skip self
                }
                int newX = x + dx;
                int newY = y + dy;

                // Check if the cell contains an animal
                if (isAnimalCell(newX, newY)) {
                    Animal animal = getAnimalAtPosition(newX, newY);

                    // Check if the animal is of the same species
                    if (animal != null && animal.getDescription().equalsIgnoreCase(leader.getDescription())) {
                        System.out.println("Same species match! Adding to nearby list.");
                        nearbyCarnivores.add(animal);
                    }
                }
            }
        }

        System.out.println("Nearby same-species carnivores found: " + nearbyCarnivores.size());
        return nearbyCarnivores;
    }

    private void moveCarnivoreRandomly(Animal animal) {
        if (!(animal instanceof Carnivore)) {
            return;
        }

        if (animal.isBeingRemoved) {
            return;
        }
        Carnivore carnivore = (Carnivore) animal;

        // Skip if carnivore is dead
        if (carnivore.isDead()) {
            return;
        }

        Coordinate position = carnivore.getPosition();
        int x = position.getX();
        int y = position.getY();

        System.out.println("Trying to move carnivore from position: (" + x + ", " + y + ")");

        // Priority 1: If very thirsty, check for water
        if (carnivore.getThirst() > 70) {
            // Search for water in a larger radius (up to 5 cells away)
            Coordinate waterLocation = findNearbyWater(x, y, 5);
            if (waterLocation != null) {
                System.out.println("Carnivore is thirsty and found water at: ("
                        + waterLocation.getX() + ", " + waterLocation.getY() + ")");

                // Move toward the water
                moveTowardLocation(carnivore, waterLocation);

                // If now at water location, drink
                if (carnivore.getPosition().getX() == waterLocation.getX()
                        && carnivore.getPosition().getY() == waterLocation.getY()) {
                    carnivore.drinkFromWater();
                }
                return;
            } else {
                System.out.println("Carnivore is thirsty but couldn't find water nearby");
            }
        }

        // Priority 2: Find prey (existing hunting logic)
        Herbivore nearbyHerbivore = findNearbyHerbivore(carnivore);

        if (nearbyHerbivore != null) {
            // Found a herbivore to hunt!
            System.out.println("Carnivore found a herbivore to hunt at: "
                    + nearbyHerbivore.getPosition().getX() + ", "
                    + nearbyHerbivore.getPosition().getY());

            // Record health before hunting
            int oldHealth = carnivore.getHealth();

            // Attack the herbivore
            boolean killed = carnivore.hunt(nearbyHerbivore);

            // Log the health increase
            int healthGained = carnivore.getHealth() - oldHealth;
            System.out.println("Carnivore health increased from " + oldHealth + " to "
                    + carnivore.getHealth() + " (+" + healthGained + ")");

            // Get the targeted herbivore's position
            int targetX = nearbyHerbivore.getPosition().getX();
            int targetY = nearbyHerbivore.getPosition().getY();

            // Find and update the herbivore's visual representation
            if (mapCells[targetX][targetY] instanceof AnimalLabel) {
                AnimalLabel targetLabel = (AnimalLabel) mapCells[targetX][targetY];
                targetLabel.showAttackAnimation();

                // If the herbivore was killed, show death animation
                if (killed) {
                    targetLabel.showDeathAnimation();

                    // Schedule removal of the dead herbivore after animation
                    Timer removalTimer = new Timer(3000, e -> {
                        fixRemoveDeadAnimal(targetX, targetY, nearbyHerbivore);
                    });
                    removalTimer.setRepeats(false);
                    removalTimer.start();
                }
            }

            // Update carnivore's display to show hunting/health gain
            if (mapCells[x][y] instanceof AnimalLabel) {
                mapCells[x][y].repaint();
            }

            return; // Don't move if we're hunting
        }

        // Priority 3: If no prey found, move randomly towards potential prey
        int newX = x;
        int newY = y;

        // Define search radius (larger for carnivores)
        int searchRadius = 3;
        boolean foundPotentialPrey = false;

        // First, try to find any herbivore in a wider radius
        for (int r = 1; r <= searchRadius && !foundPotentialPrey; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    // Only check cells at exact distance r
                    if (Math.abs(dx) != r && Math.abs(dy) != r) {
                        continue;
                    }

                    int checkX = x + dx;
                    int checkY = y + dy;

                    // Ensure coordinates are within bounds
                    if (checkX >= 0 && checkX < GRID_ROWS && checkY >= 0 && checkY < GRID_COLS) {
                        char cellType = grid.getCellType(checkX, checkY);

                        // Check if cell contains a herbivore (cow or deer)
                        if (cellType == 'c' || cellType == 'd') {
                            // Move one step toward the prey
                            int moveX = (dx > 0) ? 1 : (dx < 0) ? -1 : 0;
                            int moveY = (dy > 0) ? 1 : (dy < 0) ? -1 : 0;

                            newX = x + moveX;
                            newY = y + moveY;

                            System.out.println("Carnivore is moving toward potential prey at: (" + checkX + ", " + checkY + ")");
                            foundPotentialPrey = true;
                            break;
                        }
                    }
                }
                if (foundPotentialPrey) {
                    break;
                }
            }
        }

        // If no potential prey found, just move randomly
        if (!foundPotentialPrey) {
            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};

            int randomDirection = (int) (Math.random() * 4);
            newX = x + dx[randomDirection];
            newY = y + dy[randomDirection];
        }

        // Check if the new position is valid
        if (isValidCell(newX, newY) && !grid.getCell(newX, newY).isOccupied()) {
            System.out.println("New position (" + newX + ", " + newY + ") is valid.");

            // Use moveAnimalTo method instead of manual cell updates
            moveAnimalTo(animal, newX, newY);
        } else {
            System.out.println("New position (" + newX + ", " + newY + ") is not valid.");
        }
    }

    private Coordinate findNearbyWater(int startX, int startY, int searchRadius) {
        System.out.println("Searching for water near (" + startX + ", " + startY + ")");

        // Search in expanding radius
        for (int radius = 1; radius <= searchRadius; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    // Skip if not at exact radius
                    if (Math.abs(dx) != radius && Math.abs(dy) != radius) {
                        continue;
                    }

                    int checkX = startX + dx;
                    int checkY = startY + dy;

                    // Check if valid coordinates
                    if (checkX >= 0 && checkX < GRID_ROWS && checkY >= 0 && checkY < GRID_COLS) {
                        // Check if cell contains water - look for both 'w' and 'P' since code seems inconsistent
                        char cellType = grid.getCellType(checkX, checkY);
                        if (cellType == 'w' || cellType == 'P') {
                            System.out.println("Found water at (" + checkX + ", " + checkY + ")");
                            return new Coordinate(checkX, checkY);
                        }
                    }
                }
            }
        }

        System.out.println("No water found near (" + startX + ", " + startY + ")");
        return null;  // No water found
    }

    private void makeAnimalDrink(Animal animal, int waterX, int waterY) {
        // Directly modify animal thirst
        animal.drinkDirectly();

        // Show drinking animation on the visual representation
        if (mapCells[waterX][waterY] instanceof AnimalLabel) {
            AnimalLabel animalLabel = (AnimalLabel) mapCells[waterX][waterY];
            animalLabel.showDrinkingAnimation();
            animalLabel.updateThirstIndicator();
        }

        // Remember this water location for future reference
        animal.lastWaterSourceLocation = new Coordinate(waterX, waterY);

        System.out.println("Animal successfully drank at (" + waterX + ", " + waterY + ")");
    }

    public void checkForDeadAnimals() {
        List<Animal> animalsToRemove = new ArrayList<>();

        // First pass: identify all dead animals
        for (Animal animal : new ArrayList<>(allAnimals)) {
            if (animal == null) {
                continue;
            }

            // Skip animals already being removed
            if (animal.isBeingRemoved) {
                continue;
            }

            boolean isDead = false;

            if (animal instanceof Herbivore) {
                Herbivore herbivore = (Herbivore) animal;
                isDead = herbivore.isDead() || animal.getHealth() <= 0;
            } else if (animal instanceof Carnivore) {
                isDead = animal.getHealth() <= 0;
            }

            if (isDead) {
                animalsToRemove.add(animal);
            }
        }

        // Second pass: remove the dead animals
        for (Animal deadAnimal : animalsToRemove) {
            // Get position
            Coordinate pos = deadAnimal.getPosition();
            if (pos != null) {
                System.out.println("Removing dead animal at: " + pos.getX() + ", " + pos.getY());
                fixRemoveDeadAnimal(pos.getX(), pos.getY(), deadAnimal);
            }
        }

        notifyMiniMapUpdate();
    }
// New helper method for consistent cell clearing

    private void clearAnimalCell(int row, int col) {
        // Remove existing component
        if (mapCells[row][col] != null) {
            mapPanel.remove(mapCells[row][col]);
        }

        // Create grass label
        JLabel grassLabel = new JLabel();
        grassLabel.setSize(CELL_SIZE, CELL_SIZE);
        grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
        grassLabel.setVerticalAlignment(SwingConstants.CENTER);

        if (isNightMode) {
            grassLabel.setIcon(createDarkModeIcon());
        } else {
            grassLabel.setIcon(grassIcon);
        }

        grassLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Update grid and map cells
        grid.setCellType(row, col, '-');
        grid.getCell(row, col).setOccupied(false);
        mapCells[row][col] = grassLabel;
        mapPanel.add(grassLabel);

        // Force immediate visual update
        mapPanel.revalidate();
        mapPanel.repaint();

        notifyMiniMapUpdate();
    }
// New method for immediate removal without animation

    private void forceRemoveAnimal(int row, int col, Animal deadAnimal) {
        System.out.println("FORCE REMOVING animal at: (" + row + ", " + col + ")");

        // Remove the old component
        if (mapCells[row][col] != null) {
            mapPanel.remove(mapCells[row][col]);
        }

        // Create and add a new grass label
        JLabel grassLabel = new JLabel();
        grassLabel.setSize(CELL_SIZE, CELL_SIZE);
        grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
        grassLabel.setVerticalAlignment(SwingConstants.CENTER);
        grassLabel.setIcon(grassIcon);

        // Position the new label
        grassLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Replace in our grid
        mapCells[row][col] = grassLabel;

        // Add to the panel
        mapPanel.add(grassLabel);

        // Reset the grid cell
        grid.setCellType(row, col, '-');
        Cell cell = grid.getCell(row, col);
        if (cell != null) {
            cell.setOccupied(false);
        }

        // Force immediate visual update
        mapPanel.revalidate();
        mapPanel.repaint();

        // Remove from model
        if (controller != null && controller.getModel() != null) {
            SafariMap map = controller.getModel().getMap();
            if (map != null) {
                map.getLandscapeObjects().remove(deadAnimal);
            }
        }

        notifyMiniMapUpdate();
    }

    private void removeDeadCarnivore(int row, int col, Carnivore deadAnimal) {
        System.out.println("Removing dead " + deadAnimal.getDescription() + " at position: (" + row + ", " + col + ")");

        // Remove from model first
        if (controller != null && controller.getModel() != null) {
            SafariMap map = controller.getModel().getMap();
            if (map != null) {
                map.getLandscapeObjects().remove(deadAnimal);
            }
        }

        // Remove the animal label
        if (mapCells[row][col] != null) {
            // Show death animation
            if (mapCells[row][col] instanceof AnimalLabel) {
                AnimalLabel animalLabel = (AnimalLabel) mapCells[row][col];
                animalLabel.showDeathAnimation();

                // Remove after a brief delay to allow animation to play
                Timer removalTimer = new Timer(1000, e -> {
                    // Remove from UI
                    mapPanel.remove(mapCells[row][col]);

                    // Create grass tile in its place
                    JLabel grassLabel = new JLabel();
                    grassLabel.setSize(CELL_SIZE, CELL_SIZE);
                    grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                    grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    grassLabel.setVerticalAlignment(SwingConstants.CENTER);

                    if (isNightMode) {
                        grassLabel.setIcon(createDarkModeIcon());
                    } else {
                        grassLabel.setIcon(grassIcon);
                    }

                    grassLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    mapCells[row][col] = grassLabel;
                    mapPanel.add(grassLabel);

                    // Reset cell data
                    grid.setCellType(row, col, '-');
                    Cell cell = grid.getCell(row, col);
                    if (cell != null) {
                        cell.setOccupied(false);
                    }

                    // Update UI
                    mapPanel.revalidate();
                    mapPanel.repaint();
                });

                removalTimer.setRepeats(false);
                removalTimer.start();
            } else {
                // No animation label, remove immediately
                mapPanel.remove(mapCells[row][col]);

                // Create grass tile
                JLabel grassLabel = new JLabel();
                grassLabel.setSize(CELL_SIZE, CELL_SIZE);
                grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
                grassLabel.setVerticalAlignment(SwingConstants.CENTER);
                grassLabel.setIcon(grassIcon);
                grassLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                mapCells[row][col] = grassLabel;
                mapPanel.add(grassLabel);

                // Reset cell data
                grid.setCellType(row, col, '-');
                grid.getCell(row, col).setOccupied(false);

                // Update UI
                mapPanel.revalidate();
                mapPanel.repaint();
            }
        }

        System.out.println("Successfully removed carnivore from map");
    }

    private void moveTowardLocation(Animal animal, Coordinate target) {
        Coordinate current = animal.getPosition();
        int dx = Integer.compare(target.getX() - current.getX(), 0);
        int dy = Integer.compare(target.getY() - current.getY(), 0);

        // Try to move one step closer
        int newX = current.getX() + dx;
        int newY = current.getY() + dy;

        // Check if valid move
        if (isValidCell(newX, newY)) {
            updateAnimalOnMapWithoutHealthChange(animal, newX, newY);
            System.out.println("Animal moved toward target location. New position: (" + newX + ", " + newY + ")");
        } else {
            System.out.println("Cannot move toward target - invalid cell: (" + newX + ", " + newY + ")");
        }
    }

    private WaterArea findWaterAreaAt(int row, int col) {
        System.out.println("Searching for WaterArea at (" + row + ", " + col + ")");

        // Check if the grid actually has water at this location
        if (grid.getCellType(row, col) != 'P') {
            System.out.println("Cell at (" + row + ", " + col + ") is not water. Type: " + grid.getCellType(row, col));
            return null;
        }

        try {
            // Try to find an existing water area
            SafariMap map = controller.getModel().getMap();
            if (map != null) {
                for (LandScapeObject obj : map.getLandscapeObjects()) {
                    if (obj instanceof WaterArea) {
                        WaterArea water = (WaterArea) obj;
                        Coordinate pos = water.getPosition();

                        if (pos != null && pos.getX() == row && pos.getY() == col) {
                            System.out.println("Found existing WaterArea at (" + row + ", " + col + ")");
                            return water;
                        }
                    }
                }
            }

            // If we get here, no water area was found - create one
            System.out.println("No WaterArea found at (" + row + ", " + col + ") - creating new one");
            WaterArea newWater = new WaterArea(new Coordinate(row, col), 100);
            controller.getModel().getMap().addLandscapeObject(newWater);
            return newWater;

        } catch (Exception e) {
            System.err.println("Error in findWaterAreaAt: " + e.getMessage());
            e.printStackTrace();

            // As a last resort, create and return a new water area
            try {
                WaterArea newWater = new WaterArea(new Coordinate(row, col), 100);
                return newWater;
            } catch (Exception ex) {
                System.err.println("Failed to create fallback WaterArea: " + ex.getMessage());
                return null;
            }
        }
    }

    private Herbivore findNearbyHerbivore(Carnivore carnivore) {
        if (carnivore == null || carnivore.getPosition() == null) {
            return null;
        }

        Coordinate position = carnivore.getPosition();
        int x = position.getX();
        int y = position.getY();

        // Define search radius (1 cell in all directions)
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        // Check surrounding cells
        for (int i = 0; i < dx.length; i++) {
            int checkX = x + dx[i];
            int checkY = y + dy[i];

            // Ensure coordinates are within bounds
            if (checkX >= 0 && checkX < GRID_ROWS && checkY >= 0 && checkY < GRID_COLS) {
                char cellType = grid.getCellType(checkX, checkY);

                // Check if cell contains a herbivore (cow or deer)
                if (cellType == 'c' || cellType == 'd') {
                    // Find the herbivore at this position
                    if (mapCells[checkX][checkY] instanceof AnimalLabel) {
                        AnimalLabel animalLabel = (AnimalLabel) mapCells[checkX][checkY];
                        Animal animal = animalLabel.getAnimal();

                        if (animal instanceof Herbivore) {
                            Herbivore herbivore = (Herbivore) animal;
                            if (!herbivore.isDead()) {
                                return herbivore;
                            }
                        }
                    }
                }
            }
        }

        return null; // No herbivore found nearby
    }

    private void removeDeadAnimal(int row, int col, Animal deadAnimal) {
        System.out.println("Removing dead " + deadAnimal.getDescription() + " at position: (" + row + ", " + col + ")");

        // Remove from groups first
        if (carnivoreGroups.containsKey(deadAnimal)) {
            carnivoreGroups.remove(deadAnimal);
        }
        if (herbivoreGroups.containsKey(deadAnimal)) {
            herbivoreGroups.remove(deadAnimal);
        }

        // Remove the animal label
        if (mapCells[row][col] != null) {
            // Only remove if it's not still showing death animation
            if (mapCells[row][col] instanceof AnimalLabel) {
                AnimalLabel animalLabel = (AnimalLabel) mapCells[row][col];
                if (!animalLabel.isShowingDeathAnimation()) {
                    mapPanel.remove(mapCells[row][col]);

                    // Create a grass tile in its place
                    JLabel grassLabel = new JLabel();
                    grassLabel.setSize(CELL_SIZE, CELL_SIZE);
                    grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                    grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    grassLabel.setVerticalAlignment(SwingConstants.CENTER);
                    grassLabel.setIcon(grassIcon);
                    grassLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    mapCells[row][col] = grassLabel;
                    mapPanel.add(grassLabel);

                    // Update the grid
                    grid.setCellType(row, col, '-'); // Set to grass
                    Cell cell = grid.getCell(row, col);
                    if (cell != null) {
                        cell.setOccupied(false);
                    }

                    // Ensure the panel is updated
                    mapPanel.revalidate();
                    mapPanel.repaint();

                    // Remove the dead animal from the map's landscape objects
                    SafariMap map = controller.getModel().getMap();
                    if (map != null) {
                        map.getLandscapeObjects().remove(deadAnimal);
                    }

                    // Remove from allAnimals list
                    allAnimals.remove(deadAnimal);
                } else {
                    // If still animating, try again later
                    Timer removalTimer = new Timer(1000, e -> {
                        fixRemoveDeadAnimal(row, col, deadAnimal);
                    });
                    removalTimer.setRepeats(false);
                    removalTimer.start();
                }
            }
        }

        // At the end of the method
        notifyMiniMapUpdate();
    }

    private boolean isAnimalCell(int row, int col) {
        if (row < 0 || row >= GRID_ROWS || col < 0 || col >= GRID_COLS) {
            return false;
        }

        char cellType = grid.getCellType(row, col);
        return cellType == 'c' || cellType == 'd' || cellType == 'y' || cellType == 'z';
    }

    private Animal getAnimalAtPosition(int x, int y) {
        for (Animal animal : allAnimals) {
            if (animal.getPosition().getX() == x && animal.getPosition().getY() == y) {
                return animal;
            }
        }
        return null;
    }

    private void startAnimalMovement(int row, int col, Animal animal) {
        Coordinate position = animal.getPosition();
        System.out.println("Starting animal movement at position: " + position);

        // Important: Check if this animal already has a timer and cancel it first
        if (animalTimers.containsKey(animal)) {
            Timer oldTimer = animalTimers.get(animal);
            oldTimer.stop();
            System.out.println("Stopped existing timer for animal at " + position);
        }

        // Create a new timer for this animal
        Timer timer = new Timer(controller.getMovementDelayBasedOnGameSpeed(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Skip if animal is being removed
                if (animal.isBeingRemoved) {
                    return;
                }

                // Debug logging to verify timer is active
                System.out.println("Timer triggered for " + animal.getDescription() + " at " + animal.getPosition());

                // Update lifecycle
                animal.updateLifecycle();

                // Get current position (may have changed since timer started)
                Coordinate currentPos = animal.getPosition();
                if (currentPos == null) {
                    return;
                }

                int currentRow = currentPos.getX();
                int currentCol = currentPos.getY();

                // Check if animal has died during lifecycle update
                boolean isDead = false;
                if (animal instanceof Herbivore) {
                    isDead = ((Herbivore) animal).isDead() || animal.getHealth() <= 0;
                } else if (animal instanceof Carnivore) {
                    isDead = animal.getHealth() <= 0;
                }

                // Handle death immediately if needed
                if (isDead && !animal.isBeingRemoved) {
                    fixRemoveDeadAnimal(currentRow, currentCol, animal);
                    return; // Skip movement if animal just died
                }

                // Update thirst indicator if the label is visible
                if (mapCells[currentRow][currentCol] instanceof AnimalLabel) {
                    AnimalLabel animalLabel = (AnimalLabel) mapCells[currentRow][currentCol];
                    animalLabel.updateThirstIndicator();
                }

                // If animal is currently drinking, skip movement
                if (animal.isDrinking()) {
                    return;
                }

                // Existing movement logic
                if (animal instanceof Carnivore) {
                    moveCarnivoreRandomly(animal);
                } else if (animal instanceof Herbivore) {
                    moveHerbivoreRandomly(animal);
                } else {
                    System.out.println("Unknown animal type, skipping movement.");
                }
            }
        });

        // Store the timer in our map
        animalTimers.put(animal, timer);

        // Start the timer
        timer.start();
        System.out.println("Timer started for animal movement. Total active timers: " + animalTimers.size());
    }

    /**
     * Initialize movement for all existing animals on the map This method
     * should be called after loading a saved game
     */
    public void initializeAllAnimalMovement() {
        System.out.println("Initializing movement for all animals on the map...");

        // Stop any existing timers before reinitializing
        stopAllAnimalTimers();

        // Clear existing animal list to avoid duplicates
        allAnimals.clear();
        carnivoreGroups.clear();
        herbivoreGroups.clear();

        // Scan entire grid to find and initialize all animals
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                char cellType = grid.getCellType(row, col);

                // Check if cell contains an animal
                if (cellType == 'c' || cellType == 'd' || cellType == 'y' || cellType == 'z') {
                    System.out.println("Found animal at (" + row + ", " + col + ") of type: " + cellType);

                    // Find animal in model
                    Animal animal = findAnimalInModel(row, col);

                    if (animal == null) {
                        // Create new animal if not found in model (fallback)
                        animal = createAnimalFromCellType(row, col, cellType);

                        // Add to model if it was created
                        if (animal != null && controller != null && controller.getModel() != null) {
                            controller.getModel().getMap().addLandscapeObject(animal);
                            System.out.println("Created and added new animal to model: " + animal.getDescription());
                        }
                    }

                    if (animal != null) {
                        // Create proper animal label with health indicators
                        createAnimalLabel(animal, row, col);

                        // Add to tracking list
                        allAnimals.add(animal);

                        // Start animal movement timer
                        startAnimalMovement(row, col, animal);

                        System.out.println("Successfully initialized " + animal.getDescription() + " at (" + row + ", " + col + ")");
                    }
                }
            }
        }
    }

    /**
     * Find an animal in the model at the specified grid position
     */
    private Animal findAnimalInModel(int row, int col) {
        if (controller == null || controller.getModel() == null || controller.getModel().getMap() == null) {
            return null;
        }

        SafariMap map = controller.getModel().getMap();

        // Search through landscape objects to find animal at position
        for (LandScapeObject obj : map.getLandscapeObjects()) {
            if (obj instanceof Animal) {
                Animal animal = (Animal) obj;
                Coordinate pos = animal.getPosition();

                if (pos != null && pos.getX() == row && pos.getY() == col) {
                    System.out.println("Found existing animal in model: " + animal.getDescription());
                    return animal;
                }
            }
        }

        return null;
    }

    /**
     * Create an animal based on cell type
     */
    private Animal createAnimalFromCellType(int row, int col, char cellType) {
        switch (cellType) {
            case 'c': // Cow
                return new Herbivore(new Coordinate(row, col), "cow");
            case 'd': // Deer
                return new Herbivore(new Coordinate(row, col), "deer");
            case 'y': // Lion
                return new Carnivore(new Coordinate(row, col), "lion");
            case 'z': // Wolf
                return new Carnivore(new Coordinate(row, col), "wolf");
            default:
                return null;
        }
    }

    /**
     * Create an animal label at the specified position
     */
    private void createAnimalLabel(Animal animal, int row, int col) {
        if (animal == null) {
            return;
        }

        // Remove any existing label
        if (mapCells[row][col] != null) {
            mapPanel.remove(mapCells[row][col]);
        }

        // Create a new AnimalLabel
        AnimalLabel animalLabel = new AnimalLabel(animal, CELL_SIZE);

        // Set appropriate icon
        if (animal instanceof Herbivore) {
            String species = animal.getDescription().toLowerCase();
            if (species.contains("cow")) {
                animalLabel.setIcon(cowIcon);
            } else if (species.contains("deer")) {
                animalLabel.setIcon(deerIcon);
            }
        } else if (animal instanceof Carnivore) {
            String species = animal.getDescription().toLowerCase();
            if (species.contains("lion")) {
                animalLabel.setIcon(lionIcon);
            } else if (species.contains("wolf")) {
                animalLabel.setIcon(wolfIcon);
            }
        }

        // Position the label
        animalLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Update the UI
        mapCells[row][col] = animalLabel;
        mapPanel.add(animalLabel);

        // Make sure grid cell is marked as occupied
        Cell cell = grid.getCell(row, col);
        if (cell != null) {
            cell.setOccupied(true);
        }
    }

    /**
     * Explicitly trigger a mini-map update
     */
    private void notifyMiniMapUpdate() {
        if (miniMapView != null) {
            miniMapView.updateMapCells(mapCells);
            updateMiniMap();
        }
    }

    // Replace the existing fixRemoveDeadAnimal method with this improved version
    private void fixRemoveDeadAnimal(int row, int col, Animal deadAnimal) {
        // Mark the animal as being removed to prevent multiple removal attempts
        deadAnimal.isBeingRemoved = true;

        // Stop and remove the timer for this animal
        if (animalTimers.containsKey(deadAnimal)) {
            Timer timer = animalTimers.get(deadAnimal);
            timer.stop();
            animalTimers.remove(deadAnimal);
            System.out.println("Stopped and removed timer for dead animal");
        }

        System.out.println("FIXED REMOVAL: Removing dead " + deadAnimal.getDescription()
                + " at position: (" + row + ", " + col + ")");

        // Step 1: Remove from tracking collections first
        allAnimals.remove(deadAnimal);
        carnivoreGroups.remove(deadAnimal);
        herbivoreGroups.remove(deadAnimal);

        // Step 2: Remove from model's landscape objects
        if (controller != null && controller.getModel() != null) {
            SafariMap map = controller.getModel().getMap();
            if (map != null) {
                map.getLandscapeObjects().remove(deadAnimal);
            }
        }

        // Step 3: Update the UI 
        if (mapCells[row][col] != null) {
            if (mapCells[row][col] instanceof AnimalLabel) {
                AnimalLabel animalLabel = (AnimalLabel) mapCells[row][col];

                // Show death animation
                animalLabel.showDeathAnimation();

                // Schedule removal after animation completes
                Timer removalTimer = new Timer(1500, e -> {
                    try {
                        // Double-check that this cell still contains our animal
                        if (mapCells[row][col] == animalLabel) {
                            // Remove the animal label
                            mapPanel.remove(mapCells[row][col]);

                            // Create grass label for this position
                            // IMPORTANT: Always use standard grass icon regardless of night mode
                            JLabel grassLabel = new JLabel();
                            grassLabel.setSize(CELL_SIZE, CELL_SIZE);
                            grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                            grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
                            grassLabel.setVerticalAlignment(SwingConstants.CENTER);

                            // Always use the standard grass icon - night overlay will handle darkening
                            grassLabel.setIcon(grassIcon);
                            System.out.println("Set standard grass icon on replacement cell (night mode handled by overlay)");

                            // Position the grass label
                            grassLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                            // Update the mapCells array
                            mapCells[row][col] = grassLabel;
                            mapPanel.add(grassLabel);

                            // Update the grid data
                            grid.setCellType(row, col, '-');
                            Cell cell = grid.getCell(row, col);
                            if (cell != null) {
                                cell.setOccupied(false);
                            }

                            // Force UI update - add to the bottom of the z-order, leaving overlay on top
                            mapPanel.setComponentZOrder(grassLabel, mapPanel.getComponentCount() - 1);
                            mapPanel.revalidate();
                            mapPanel.repaint();

                            // Update minimap
                            notifyMiniMapUpdate();

                            System.out.println("Successfully removed dead animal and replaced with standard grass");
                        }
                    } catch (Exception ex) {
                        System.err.println("Error during animal removal: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });

                removalTimer.setRepeats(false);
                removalTimer.start();
            } else {
                // No animal label found - clear cell immediately
                try {
                    mapPanel.remove(mapCells[row][col]);

                    // Create grass label - always use standard grass icon
                    JLabel grassLabel = new JLabel();
                    grassLabel.setSize(CELL_SIZE, CELL_SIZE);
                    grassLabel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                    grassLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    grassLabel.setVerticalAlignment(SwingConstants.CENTER);

                    // Always use standard grass icon
                    grassLabel.setIcon(grassIcon);

                    grassLabel.setBounds(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    // Update mapCells and grid
                    mapCells[row][col] = grassLabel;
                    mapPanel.add(grassLabel);
                    grid.setCellType(row, col, '-');
                    grid.getCell(row, col).setOccupied(false);

                    // Force UI update
                    mapPanel.revalidate();
                    mapPanel.repaint();
                    notifyMiniMapUpdate();
                } catch (Exception ex) {
                    System.err.println("Error during immediate animal removal: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    public void stopAllAnimalTimers() {
        for (Timer timer : animalTimers.values()) {
            timer.stop();
        }
        animalTimers.clear();
        System.out.println("All animal timers stopped and cleared");
    }

}

package my.company.my.safarigame.view;

import my.company.my.safarigame.model.Market;
import my.company.my.safarigame.model.Player;
import my.company.my.safarigame.model.TradeableItem;
import my.company.my.safarigame.model.Plant;
import my.company.my.safarigame.model.Road;
import my.company.my.safarigame.controller.GameController;
import my.company.my.safarigame.model.Coordinate;
import my.company.my.safarigame.model.Herbivore;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import my.company.my.safarigame.model.Animal;
import my.company.my.safarigame.model.Carnivore;
import my.company.my.safarigame.model.Jeep;
import my.company.my.safarigame.model.Ranger;
import my.company.my.safarigame.model.TerrainObstacle;
import my.company.my.safarigame.model.WaterArea;

/**
 * MarketView class that displays a market popup showing items that can be bought/sold
 * Follows MVC pattern by receiving market data from the model via controller
 */
public class MarketView extends JDialog {
    // Market model 
    private Market market;
    private Player player;
    private GameController controller;
    
    // UI Components
    private JPanel mainPanel;
    private JPanel itemsPanel;
    private JLabel playerCapitalLabel;
    private JPanel sellPanel;      // New panel for selling items
    private JTabbedPane tabbedPane;  // For switching between buy and sell tabs
    
    // Styling
    private Font titleFont = new Font("Comic Sans MS", Font.BOLD, 26);
    private Font headingFont = new Font("Comic Sans MS", Font.BOLD, 22);
    private Font normalFont = new Font("Comic Sans MS", Font.PLAIN, 16);
    private Color safariOrange = new Color(255, 165, 0);
    private Color darkBrown = new Color(139, 69, 19);
    private Color lightBeige = new Color(245, 222, 179);
    private Color panelBg = new Color(50, 50, 50);
    
    // Cached images
    private ImageIcon plantIcon;
    private ImageIcon shrubIcon;
    private ImageIcon bushIcon;
    private ImageIcon horizontalRoadIcon;
    private ImageIcon verticalRoadIcon;
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
    
    // Reference items for unlimited buying
    private Map<String, TradeableItem> referenceItems = new HashMap<>();
    
    /**
     * Constructor
     * @param owner The parent frame
     * @param market The market model
     * @param player The player model
     * @param controller The game controller
     */
    public MarketView(JFrame owner, Market market, Player player, GameController controller) {
        super(owner, "Safari Market", true);
        this.market = market;
        this.player = player;
        this.controller = controller;
        
        // Load images first
        loadImages();
        
        // Ensure market has items
        if (market != null && market.listAvailableItems().isEmpty()) {
            // Pre-populate market with sample items
            addSampleItems();
            System.out.println("Added sample items to empty market");
        }
        
        // Store reference items for unlimited buying
        storeReferenceItems();
        
        setBackground(panelBg);

    // Create panel with gradient background
    mainPanel = new JPanel(new BorderLayout(10, 10)) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Create gradient background
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(40, 40, 40),
                0, h, new Color(70, 70, 70)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    };

    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    JPanel headerPanel = createHeaderPanel();
    mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Initialize UI components
        initializeUI();
        
        populateItems();
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel to the dialog
        setContentPane(mainPanel);
        
        // Set dialog properties
        setSize(850, 650);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Make sure UI can't be resized too small
        setMinimumSize(new Dimension(600, 450));
    }
    
    /**
     * Store reference items for unlimited buying
     */
    private void storeReferenceItems() {
        List<TradeableItem> items = market.listAvailableItems();
        for (TradeableItem item : items) {
            String key = getItemKey(item);
            if (!referenceItems.containsKey(key)) {
                referenceItems.put(key, item);
            }
        }
    }
    
    /**
     * Get a unique key for an item
     */
    private String getItemKey(TradeableItem item) {
        if (item instanceof Plant) {
            return "plant_" + item.getDescription();
        } else if (item instanceof Road) {
            return "road_" + ((Road)item).getRoadType();
        } else {
            return "item_" + item.getDescription();
        }
    }
    
    /**
     * Load images for market items
     */
    private void loadImages() {
        try {
            plantIcon = loadAndResizeImage("/tiles/grass2.png", 64, 64);
            shrubIcon = loadAndResizeImage("/tiles/grass3.png", 64, 64);
            bushIcon = loadAndResizeImage("/tiles/grass1.png", 64, 64);
            
            horizontalRoadIcon = loadAndResizeImage("/tiles/road-4.png", 64, 64);
            verticalRoadIcon = loadAndResizeImage("/tiles/road-2.png", 64, 64);
            rightupRoadIcon = loadAndResizeImage("/tiles/road-5.png", 64, 64);
            rightdownRoadIcon = loadAndResizeImage("/tiles/road-1.png", 64, 64);
            leftupRoadIcon = loadAndResizeImage("/tiles/road-3.png", 64, 64);
            leftdownRoadIcon = loadAndResizeImage("/tiles/road-6.png", 64, 64);
            
            cowIcon = loadAndResizeImage("/tiles/cow1.png", 64, 64);
            deerIcon = loadAndResizeImage("/tiles/deer1.png", 64, 64);
            lionIcon = loadAndResizeImage("/tiles/lion1.png", 64, 64);
            wolfIcon = loadAndResizeImage("/tiles/wolf1.png", 64, 64);
            
            pondIcon = loadAndResizeImage("/tiles/pond1.png", 64, 64);
            
            rangerIcon = loadAndResizeImage("/tiles/ranger1.png", 64, 64);
            
            jeepIcon = loadAndResizeImage("/tiles/jeep1.png", 64, 64);
            
            mountainIcon = loadAndResizeImage("/tiles/jeep1.png", 64, 64);
            riverIcon = loadAndResizeImage("/tiles/jeep1.png", 64, 64);
            
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }
    
    /**
     * Load and resize an image
     */
    private ImageIcon loadAndResizeImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        /// Create tabbed pane for Buy and Sell tabs
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(headingFont);
        tabbedPane.setBackground(new Color(60, 60, 60));
        tabbedPane.setForeground(Color.WHITE);

        // Configure tab style
        UIManager.put("TabbedPane.selected", new Color(180, 115, 20));
        UIManager.put("TabbedPane.contentAreaColor", panelBg);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        // Create Buy tab content
        JPanel buyTabPanel = new JPanel(new BorderLayout());
        buyTabPanel.setOpaque(false);

        // Create items panel with a scroll pane for buying
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        JScrollPane buyScrollPane = new JScrollPane(itemsPanel);
        buyScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkBrown, 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        buyScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        buyScrollPane.setOpaque(false);
        buyScrollPane.getViewport().setOpaque(false);

        buyTabPanel.add(buyScrollPane, BorderLayout.CENTER);

        // Create Sell tab content
        JPanel sellTabPanel = new JPanel(new BorderLayout());
        sellTabPanel.setOpaque(false);

        // Create sell panel with a scroll pane
        sellPanel = new JPanel();
        sellPanel.setLayout(new BoxLayout(sellPanel, BoxLayout.Y_AXIS));
        sellPanel.setOpaque(false);

        JScrollPane sellScrollPane = new JScrollPane(sellPanel);
        sellScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkBrown, 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        sellScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        sellScrollPane.setOpaque(false);
        sellScrollPane.getViewport().setOpaque(false);

        sellTabPanel.add(sellScrollPane, BorderLayout.CENTER);

        // Add tabs to tabbed pane
        tabbedPane.addTab("Buy Items", buyTabPanel);
        tabbedPane.addTab("Sell Items", sellTabPanel);

        // Add tab change listener to update content
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                // Buy tab selected
                populateItems();
            } else {
                // Sell tab selected
                populateInventoryItems();
            }
        });

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Create the header panel with title and player capital
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Create a fancy title panel with a background
        JPanel titlePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Create rounded rectangle with gradient
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(180, 115, 20),
                    0, h, safariOrange
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 20, 20);
                
                // Add border
                g2d.setColor(darkBrown);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, w-3, h-3, 20, 20);
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Title label
        JLabel titleLabel = new JLabel("Safari Market", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Player capital panel
        JPanel capitalPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Create rounded rectangle with gradient
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(110, 50, 0),
                    0, h, darkBrown
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 15, 15);
                
                // Add border
                g2d.setColor(safariOrange);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, w-3, h-3, 15, 15);
            }
        };
        capitalPanel.setOpaque(false);
        capitalPanel.setLayout(new BorderLayout());
        capitalPanel.setBorder(new EmptyBorder(8, 15, 8, 15));
        capitalPanel.setPreferredSize(new Dimension(200, 40));
        
        // Player capital label
        playerCapitalLabel = new JLabel("Your Capital: $" + player.getCapital(), SwingConstants.RIGHT);
        playerCapitalLabel.setFont(normalFont);
        playerCapitalLabel.setForeground(Color.WHITE);
        capitalPanel.add(playerCapitalLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.CENTER);
        panel.add(capitalPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Create the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton closeButton = createStyledButton("Close Market", 160, 50);
        closeButton.addActionListener(e -> dispose());
        
        panel.add(closeButton);
        return panel;
    }
    
    /**
     * Create a styled button
     */
    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    // Pressed state
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(200, 120, 0),
                        0, getHeight(), new Color(150, 80, 0)
                    );
                    g2d.setPaint(gp);
                } else if (getModel().isRollover()) {
                    // Hover state
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 200, 100),
                        0, getHeight(), new Color(255, 180, 50)
                    );
                    g2d.setPaint(gp);
                } else {
                    // Default state
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 180, 50),
                        0, getHeight(), safariOrange
                    );
                    g2d.setPaint(gp);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Add border
                g2d.setColor(getModel().isRollover() ? Color.WHITE : darkBrown);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(normalFont);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2d.drawString(getText(), (getWidth() - textWidth) / 2, 
                             (getHeight() - textHeight) / 2 + fm.getAscent());
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(normalFont);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }
    
    /**
     * Populate the items panel with market items
     */
    private void populateItems() {
        itemsPanel.removeAll();
        
        // Use reference items for display
        List<Plant> plants = new ArrayList<>();
        List<Road> roads = new ArrayList<>();
        List<Animal> animals = new ArrayList<>();
        List<WaterArea> waterArea = new ArrayList<>();
        List<Ranger> rangers = new ArrayList<>();
        List<Jeep> jeeps = new ArrayList<>();
        List<TerrainObstacle> obstacles = new ArrayList<>();
        
        // Sort reference items into categories
        for (TradeableItem item : referenceItems.values()) {
            if (item instanceof Plant plant) {
                plants.add(plant);
            } else if (item instanceof Road road) {
                roads.add(road);
            } else if (item instanceof Animal) {  // Add animals to list
            animals.add((Animal) item);
        }
            else if (item instanceof WaterArea) {  // Add animals to list
            waterArea.add((WaterArea) item);
        }
            else if (item instanceof Ranger) {  // Add animals to list
            rangers.add((Ranger) item);
        }
            else if (item instanceof Jeep) {  // Add animals to list
            jeeps.add((Jeep) item);
        }
            else if (item instanceof TerrainObstacle) {  // Add animals to list
            obstacles.add((TerrainObstacle) item);
        }
        }
        
        // Display plants section
        addSectionTitle(itemsPanel, "Plants");
        
        if (plants.isEmpty()) {
            JLabel noPlants = new JLabel("No plants available in market", SwingConstants.CENTER);
            noPlants.setForeground(Color.WHITE);
            noPlants.setFont(normalFont);
            noPlants.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsPanel.add(noPlants);
        } else {
            // Group plants by species for better organization
            Map<String, Plant> uniquePlants = new HashMap<>();
            
            for (Plant plant : plants) {
                String species = extractSpecies(plant);
                uniquePlants.put(species, plant);
            }
            
            // Display plants by species groups
            for (String species : uniquePlants.keySet()) {
                Plant representative = uniquePlants.get(species);
                
                // Create an item panel for each unique plant type (unlimited)
                addItemPanel(representative, getPlantIcon(representative),true);
            }
        }
        
        // Display roads section
        addSectionTitle(itemsPanel,"Roads");
        
        if (roads.isEmpty()) {
            JLabel noRoads = new JLabel("No roads available in market", SwingConstants.CENTER);
            noRoads.setForeground(Color.WHITE);
            noRoads.setFont(normalFont);
            noRoads.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsPanel.add(noRoads);
        } else {
            // Group roads by type for better organization
            Map<String, Road> uniqueRoads = new HashMap<>();
            
            for (Road road : roads) {
                String type = road.getRoadType();
                uniqueRoads.put(type, road);
            }
            
            // Display roads by type groups
            for (String type : uniqueRoads.keySet()) {
                Road representative = uniqueRoads.get(type);
                
                // Create an item panel for each unique road type (unlimited)
                addItemPanel(representative, getRoadIcon(representative),true);
            }
        }
        
        // Display animals section
        addSectionTitle(itemsPanel,"Animals");
        if (animals.isEmpty()) {
            JLabel noAnimals = new JLabel("No animals available in market", SwingConstants.CENTER);
            noAnimals.setForeground(Color.WHITE);
            noAnimals.setFont(normalFont);
            noAnimals.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsPanel.add(noAnimals);
        } else {
            for (Animal animal : animals) {
                // Display each animal with a representative icon
                // You can either choose one icon per animal or use a general one depending on your requirement
                addItemPanel(animal, getAnimalIcon(animal),true);  // Replace with the actual method to fetch the animal icon
            }
        }

        // Display water areas section (like ponds)
        addSectionTitle(itemsPanel,"Water Areas");
        if (waterArea.isEmpty()) {
            JLabel noWaterAreas = new JLabel("No water areas available in market", SwingConstants.CENTER);
            noWaterAreas.setForeground(Color.WHITE);
            noWaterAreas.setFont(normalFont);
            noWaterAreas.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsPanel.add(noWaterAreas);
        } else {
            for (WaterArea waterAreas : waterArea) {
                // Display each water area with a representative icon
                addItemPanel(waterAreas, pondIcon,true);  // Replace with the actual method to fetch the water area icon
            }
        }
        
        //Display Rangers area section
        addSectionTitle(itemsPanel,"Rangers");
        if (rangers.isEmpty()) {
            JLabel noRangers = new JLabel("No rangers available in market", SwingConstants.CENTER);
            noRangers.setForeground(Color.WHITE);
            noRangers.setFont(normalFont);
            noRangers.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsPanel.add(noRangers);
        } else {
            for (Ranger ranger : rangers) {
                addItemPanel(ranger, rangerIcon, true);
            }
        }
        
        //Display Jeep area section
        addSectionTitle(itemsPanel,"Jeep");
        if (jeeps.isEmpty()) {
            JLabel noJeeps = new JLabel("No jeeps available in market", SwingConstants.CENTER);
            noJeeps.setForeground(Color.WHITE);
            noJeeps.setFont(normalFont);
            noJeeps.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsPanel.add(noJeeps);
        } else {
            for (Jeep jeep : jeeps) {
                addItemPanel(jeep, jeepIcon, true);
            }
        }

               
        
        // Revalidate the panel to update UI
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
    
    /**
     * Extract species from a plant
     */
    private String extractSpecies(Plant plant) {
        String description = plant.getDescription().toLowerCase();
        if (description.contains("bush")) {
            return "bush";
        } else if (description.contains("shrub")) {
            return "shrub";
        } else if (description.contains("plant")) {
            return "plant";
        } else {
            return "unknown";
        }
    }
    
 // Replace the incomplete addItemPanel method with this complete implementation
private void addItemPanel(TradeableItem item, ImageIcon icon, boolean forBuying) {
    JPanel panel = forBuying ? itemsPanel : sellPanel;
    
    JPanel itemPanel = new JPanel(new BorderLayout(15, 0)) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create rounded rectangle with gradient
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(60, 60, 60),
                0, h, new Color(80, 80, 80)
            );
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, w, h, 15, 15);
            
            // Add border
            g2d.setColor(darkBrown);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(1, 1, w-3, h-3, 15, 15);
        }
    };
    
    itemPanel.setOpaque(false);
    itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
    
    // Icon panel with background
    JPanel iconContainer = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create rounded rectangle background
            int w = getWidth();
            int h = getHeight();
            g2d.setColor(new Color(40, 40, 40));
            g2d.fillRoundRect(0, 0, w, h, 10, 10);
            
            // Add border
            g2d.setColor(new Color(100, 100, 100));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(0, 0, w-1, h-1, 10, 10);
        }
    };
    iconContainer.setOpaque(false);
    iconContainer.setLayout(new BorderLayout());
    iconContainer.setPreferredSize(new Dimension(80, 80));
    
    JLabel iconLabel = new JLabel(icon);
    iconLabel.setHorizontalAlignment(JLabel.CENTER);
    iconContainer.add(iconLabel, BorderLayout.CENTER);
    
    // Description panel
    JPanel descPanel = new JPanel(new GridLayout(2, 1, 0, 5));
    descPanel.setOpaque(false);
    
    // Create a cleaner display name
    String displayName = getDisplayName(item);
    
    JLabel nameLabel = new JLabel(displayName + (forBuying ? " (Unlimited)" : ""));
    nameLabel.setForeground(Color.WHITE);
    nameLabel.setFont(normalFont);
    
    JLabel priceLabel = new JLabel("Price: $" + item.getPrice());
    priceLabel.setForeground(new Color(200, 255, 200));
    priceLabel.setFont(normalFont);
    
    descPanel.add(nameLabel);
    descPanel.add(priceLabel);
    
    // Buy/Sell button
    JButton actionButton = createStyledButton(forBuying ? "Buy" : "Sell", 100, 40);
    
    if (forBuying) {
        // Add buy action
        actionButton.addActionListener(e -> {
            if (player.getCapital() >= item.getPrice()) {
                try {
                    // Create a new instance of the item to add to player's inventory
                    TradeableItem newItem = createNewItemInstance(item);
                    
                    // Use the player to buy the item directly
                    player.buyItem(newItem);
                    
                    // Update the player capital display
                    playerCapitalLabel.setText("Your Capital: $" + player.getCapital());
                    
                    // Show success message with animation
                    showPurchaseSuccessMessage(displayName);
                    
                } catch (Exception ex) {
                    // Handle any exceptions during purchase
                    JOptionPane.showMessageDialog(
                        this,
                        "Error during purchase: " + ex.getMessage(),
                        "Transaction Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                // Show insufficient funds message
                showInsufficientFundsMessage(item.getPrice());
            }
        });
    }
    
    // Add components to item panel
    itemPanel.add(iconContainer, BorderLayout.WEST);
    itemPanel.add(descPanel, BorderLayout.CENTER);
    itemPanel.add(actionButton, BorderLayout.EAST);
    
    // Add to the appropriate panel with some spacing
    itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(itemPanel);
    
    // Add a gap between items
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
}
    
    /**
     * Create a new instance of an item for unlimited buying
     */
    private TradeableItem createNewItemInstance(TradeableItem original) {
        if (original instanceof Plant) {
            Plant plant = (Plant) original;
            return new Plant(
                new Coordinate(0, 0), // Placeholder coordinate
                plant.getDescription(),
                plant.getPrice()
            );
        } else if (original instanceof Road) {
            Road road = (Road) original;
            return new Road(
                new Coordinate(0, 0), // Placeholder coordinate
                1.0, // Default length
                road.getPrice(),
                road.getRoadType()
            );
            
        } 
        else if (original instanceof Herbivore) {
            Herbivore herbivore = (Herbivore) original;
            return new Herbivore(
                new Coordinate(0, 0), // Placeholder coordinate
                herbivore.getDescription()
            );
        }
        else if (original instanceof Carnivore) {
            Carnivore carnivore = (Carnivore) original;
            return new Carnivore(
                new Coordinate(0, 0), // Placeholder coordinate
                carnivore.getDescription()
            );
        }
        else if (original instanceof Ranger) {
            Ranger ranger = (Ranger) original;
            return new Ranger(
                new Coordinate(0, 0) // Placeholder coordinate
            );
        }
        else if (original instanceof WaterArea) {
            WaterArea waterArea = (WaterArea) original;
            return new WaterArea(
                new Coordinate(0, 0), 100.0 // Placeholder coordinate
            );
        }
        else if (original instanceof Jeep) {
            Jeep jeep = (Jeep) original;
            return new Jeep(
                new Coordinate(0, 0) // Placeholder coordinate
            );
        }
        else if (original instanceof TerrainObstacle) {
            TerrainObstacle obstacle = (TerrainObstacle) original;
            return new TerrainObstacle(
                new Coordinate(0, 0), obstacle.getDescription()// Placeholder coordinate
            );
        }
        else {
            // Generic case - should not happen with your current models
            throw new IllegalArgumentException("Unknown item type: " + original.getClass().getName());
        }
    }
    
    /**
     * Show an animated success message when purchasing an item
     */
    private void showPurchaseSuccessMessage(String itemName) {
        JDialog successDialog = new JDialog(this, false);
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
                g2d.drawRoundRect(1, 1, w-3, h-3, 20, 20);
            }
        };
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        messagePanel.setOpaque(false);
        
        JLabel messageLabel = new JLabel("Purchased: " + itemName);
        messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        successDialog.setContentPane(messagePanel);
        successDialog.pack();
        successDialog.setLocationRelativeTo(this);
        
        // Show the dialog and close it after a short delay
        successDialog.setVisible(true);
        
        Timer timer = new Timer(1200, e -> successDialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Show animated insufficient funds message
     */
    private void showInsufficientFundsMessage(double price) {
        JDialog errorDialog = new JDialog(this, false);
        errorDialog.setUndecorated(true);
        errorDialog.setBackground(new Color(0, 0, 0, 0));
        
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
                g2d.setColor(new Color(200, 0, 0, 200));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, w-3, h-3, 20, 20);
            }
        };
        messagePanel.setLayout(new BorderLayout(0, 10));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        messagePanel.setOpaque(false);
        
        JLabel messageLabel = new JLabel("Insufficient Funds");
        messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel detailLabel = new JLabel("You need $" + price + " but only have $" + player.getCapital());
        detailLabel.setFont(normalFont);
        detailLabel.setForeground(Color.WHITE);
        detailLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        labelPanel.setOpaque(false);
        labelPanel.add(messageLabel);
        labelPanel.add(detailLabel);
        
        messagePanel.add(labelPanel, BorderLayout.CENTER);
        
        errorDialog.setContentPane(messagePanel);
        errorDialog.pack();
        errorDialog.setLocationRelativeTo(this);
        
        // Show the dialog and close it after a short delay
        errorDialog.setVisible(true);
        
        Timer timer = new Timer(2000, e -> errorDialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Get a clean display name for an item
     */
    private String getDisplayName(TradeableItem item) {
        if (item instanceof Plant) {
            Plant plant = (Plant) item;
            String species = extractSpecies(plant);
            return capitalizeFirstLetter(species);
        } else if (item instanceof Road) {
            Road road = (Road) item;
            String type = road.getRoadType();
            
            if (type.equals("horizontal")) {
                return "Horizontal Road";
            } else if (type.equals("vertical")) {
                return "Vertical Road";
            } else if (type.equals("rightDown")) {
                return "Right Down Corner Road";
            } else if (type.equals("rightUp")) {
                return "Right Up Corner Road";
            } else if (type.equals("leftDown")) {
                return "Left Down Corner Road";
            } else if (type.equals("leftUp")) {
                return "Left Up Corner Road";
            } else {
                return capitalizeFirstLetter(type) + " Road";
            }
        } else {
            return item.getDescription();
        }
    }
    
    /**
     * Capitalize the first letter of a string
     */
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    
    /**
     * Add a section title to the items panel
     */
    private void addSectionTitle(JPanel panel,String title) {
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create rounded rectangle with gradient
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(120, 70, 0),
                    0, h, darkBrown
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 10, 10);
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.LEFT);
        titleLabel.setFont(headingFont);
        titleLabel.setForeground(safariOrange);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Add to items panel with spacing
            titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(titlePanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    /**
     * Add sample items to the market for testing
     */
    private void addSampleItems() {
        // Add plants with different species - similar to those loaded in GridLoader
        market.addItem(new my.company.my.safarigame.model.Plant(
            new my.company.my.safarigame.model.Coordinate(0, 0), "bush", 100.0));
        market.addItem(new my.company.my.safarigame.model.Plant(
            new my.company.my.safarigame.model.Coordinate(0, 0), "plant", 150.0));
        market.addItem(new my.company.my.safarigame.model.Plant(
            new my.company.my.safarigame.model.Coordinate(0, 0), "shrub", 200.0));
        
        // Add roads with different types - matching those in GridLoader
        market.addItem(new my.company.my.safarigame.model.Road(
            new my.company.my.safarigame.model.Coordinate(0, 0), 1.0, 50.0, "horizontal"));
        market.addItem(new my.company.my.safarigame.model.Road(
            new my.company.my.safarigame.model.Coordinate(0, 0), 1.0, 50.0, "vertical"));
//        market.addItem(new my.company.my.safarigame.model.Road(
//            new my.company.my.safarigame.model.Coordinate(0, 0), 1.0, 75.0, "rightDown"));
//        market.addItem(new my.company.my.safarigame.model.Road(
//            new my.company.my.safarigame.model.Coordinate(0, 0), 1.0, 75.0, "rightUp"));
//        market.addItem(new my.company.my.safarigame.model.Road(
//            new my.company.my.safarigame.model.Coordinate(0, 0), 1.0, 75.0, "leftUp"));
//        market.addItem(new my.company.my.safarigame.model.Road(
//            new my.company.my.safarigame.model.Coordinate(0, 0), 1.0, 75.0, "leftDown"));
        
        // Add animals 
        market.addItem(new my.company.my.safarigame.model.Herbivore(
        new my.company.my.safarigame.model.Coordinate(0, 0), "Cow"));
        market.addItem(new my.company.my.safarigame.model.Herbivore(
        new my.company.my.safarigame.model.Coordinate(0, 0), "Deer"));
        market.addItem(new my.company.my.safarigame.model.Carnivore(
        new my.company.my.safarigame.model.Coordinate(0, 0), "Lion"));
        market.addItem(new my.company.my.safarigame.model.Carnivore(
        new my.company.my.safarigame.model.Coordinate(0, 0), "Wolf"));
        
        // Add WaterArea
        market.addItem(new my.company.my.safarigame.model.WaterArea(
            new my.company.my.safarigame.model.Coordinate(0, 0), 100.0));
        
        // Add Ranger 
        market.addItem(new my.company.my.safarigame.model.Ranger(
            new my.company.my.safarigame.model.Coordinate(0, 0)));
        
        //Add Jeep
        market.addItem(new my.company.my.safarigame.model.Jeep(
            new my.company.my.safarigame.model.Coordinate(0, 0)));
        
        //Add Obstacles
        market.addItem(new my.company.my.safarigame.model.TerrainObstacle(
            new my.company.my.safarigame.model.Coordinate(0, 0), "River"));
        market.addItem(new my.company.my.safarigame.model.TerrainObstacle(
            new my.company.my.safarigame.model.Coordinate(0, 0), "Mountain"));
        

    }
    
    /**
     * Get the appropriate icon for a plant based on its species
     */
    private ImageIcon getPlantIcon(Plant plant) {
        String species = plant.getDescription().toLowerCase();
        
        if (species.contains("bush")) {
            return bushIcon;
        } else if (species.contains("shrub")) {
            return shrubIcon;
        } else if (species.contains("plant")) {
            return plantIcon;
        } else {
            return plantIcon; // Default for other types
        }
    }
    
    /**
     * Get the appropriate icon for a road based on its type
     */
    private ImageIcon getRoadIcon(Road road) {
        String roadType = road.getRoadType().toLowerCase();
        
        // Match the exact types used in GridLoader
        if (roadType.equals("horizontal")) {
            return horizontalRoadIcon;
        } else if (roadType.equals("vertical")) {
            return verticalRoadIcon;
        } else if (roadType.equals("rightup")) {
            return rightupRoadIcon;
        } else if (roadType.equals("rightdown")) {
            return rightdownRoadIcon;
        } else if (roadType.equals("leftup")) {
            return leftupRoadIcon;
        } else if (roadType.equals("leftdown")) {
            return leftdownRoadIcon;
        } else {
            return horizontalRoadIcon; // Default
        }
    }
    
    private ImageIcon getObstacleIcon(TerrainObstacle obstacle) {
    String type = obstacle.getObstacleType().toLowerCase();

    if (type.contains("river")) {
        return riverIcon;
    } else {
        return mountainIcon;
    }
}

    
    /**
 * Get the appropriate icon for an animal based on its species or type
 */
    private ImageIcon getAnimalIcon(Animal animal) {
        String species = animal.getDescription().toLowerCase();  // Assuming there's a getSpecies method in the Animal class

        // Match the species or type of animal for specific icons
        if (species.contains("cow")) {
            return cowIcon;
        } else if (species.contains("deer")) {
            return deerIcon;
        } else if (species.contains("lion")) {
            return lionIcon;
        } else {
            return wolfIcon;
        }
    }

    
    /**
     * Update the display with current market items
     */
    public void refreshMarket() {
        // Update player capital display
        if (player != null) {
            playerCapitalLabel.setText("Your Capital: $" + player.getCapital());
        }

        // Repopulate the currently visible tab
        if (tabbedPane.getSelectedIndex() == 0) {
            // Buy tab is visible
            populateItems();
        } else {
            // Sell tab is visible
            populateInventoryItems();
        }

        // Ensure UI repaints correctly
        revalidate();
        repaint();
    }
    
    /**
    * Populate the sell panel with inventory items
    */
   private void populateInventoryItems() {
       sellPanel.removeAll();

       // Get player's inventory
       List<TradeableItem> inventory = player.getInventory();

       if (inventory.isEmpty()) {
           // Show empty inventory message
           JLabel emptyLabel = new JLabel("Your inventory is empty", SwingConstants.CENTER);
           emptyLabel.setForeground(Color.WHITE);
           emptyLabel.setFont(headingFont);
           emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
           sellPanel.add(Box.createVerticalStrut(30));
           sellPanel.add(emptyLabel);

           JLabel infoLabel = new JLabel("Purchase items to sell them later", SwingConstants.CENTER);
           infoLabel.setForeground(new Color(200, 200, 200));
           infoLabel.setFont(normalFont);
           infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
           sellPanel.add(Box.createVerticalStrut(15));
           sellPanel.add(infoLabel);

           sellPanel.revalidate();
           sellPanel.repaint();
           return;
       }

       // Group items by type and description for display
       Map<String, List<TradeableItem>> groupedItems = new HashMap<>();
       Map<String, String> displayNames = new HashMap<>();
       Map<String, ImageIcon> displayIcons = new HashMap<>();

       // Group items
       for (TradeableItem item : inventory) {
           String key = getItemKey(item);
           String displayName = getDisplayName(item);
           ImageIcon icon = getItemImage(item);

           if (!groupedItems.containsKey(key)) {
               groupedItems.put(key, new ArrayList<>());
               displayNames.put(key, displayName);
               displayIcons.put(key, icon);
           }

           groupedItems.get(key).add(item);
       }

       // Add section title
       addSectionTitle(sellPanel, "Your Inventory");

       // Display each group of items
       for (String key : groupedItems.keySet()) {
           List<TradeableItem> items = groupedItems.get(key);
           String displayName = displayNames.get(key);
           ImageIcon icon = displayIcons.get(key);

           if (!items.isEmpty()) {
               // Use the first item as a representative
               TradeableItem representative = items.get(0);

               // Create a special sell panel that shows quantity
               addSellItemPanel(representative, icon, items.size());
           }
       }

       sellPanel.revalidate();
       sellPanel.repaint();
   }
   
   /**
 * Add a sell panel for an inventory item
 */
private void addSellItemPanel(TradeableItem item, ImageIcon icon, int quantity) {
    JPanel itemPanel = new JPanel(new BorderLayout(15, 0)) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create rounded rectangle with gradient
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(60, 60, 60),
                0, h, new Color(80, 80, 80)
            );
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, w, h, 15, 15);
            
            // Add border
            g2d.setColor(darkBrown);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(1, 1, w-3, h-3, 15, 15);
        }
    };
    
    itemPanel.setOpaque(false);
    itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
    
    // Icon panel with background
    JPanel iconContainer = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create rounded rectangle background
            int w = getWidth();
            int h = getHeight();
            g2d.setColor(new Color(40, 40, 40));
            g2d.fillRoundRect(0, 0, w, h, 10, 10);
            
            // Add border
            g2d.setColor(new Color(100, 100, 100));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(0, 0, w-1, h-1, 10, 10);
        }
    };
    iconContainer.setOpaque(false);
    iconContainer.setLayout(new BorderLayout());
    iconContainer.setPreferredSize(new Dimension(80, 80));
    
    JLabel iconLabel = new JLabel(icon);
    iconLabel.setHorizontalAlignment(JLabel.CENTER);
    iconContainer.add(iconLabel, BorderLayout.CENTER);
    
    // Description panel
    JPanel descPanel = new JPanel(new GridLayout(3, 1, 0, 5));
    descPanel.setOpaque(false);
    
    // Create a cleaner display name
    String displayName = getDisplayName(item);
    
    JLabel nameLabel = new JLabel(displayName);
    nameLabel.setForeground(Color.WHITE);
    nameLabel.setFont(normalFont);
    
    JLabel priceLabel = new JLabel("Price: $" + item.getPrice());
    priceLabel.setForeground(new Color(200, 255, 200));
    priceLabel.setFont(normalFont);
    
    JLabel quantityLabel = new JLabel("Quantity: " + quantity);
    quantityLabel.setForeground(new Color(255, 255, 200));
    quantityLabel.setFont(normalFont);
    
    descPanel.add(nameLabel);
    descPanel.add(priceLabel);
    descPanel.add(quantityLabel);
    
    // Sell button
    JButton sellButton = createStyledButton("Sell", 100, 40);
    
    // Add action to sell button
    sellButton.addActionListener(e -> {
        if (player.getInventory().contains(item)) {
            try {
                // Use the player to sell the item directly
                player.sellItem(item);
                
                // Update the player capital display
                playerCapitalLabel.setText("Your Capital: $" + player.getCapital());
                
                // Refresh the sell tab
                populateInventoryItems();
                
                // Show success message with animation
                showSellSuccessMessage(displayName, item.getPrice());
                
            } catch (Exception ex) {
                // Handle any exceptions during sale
                JOptionPane.showMessageDialog(
                    this,
                    "Error during sale: " + ex.getMessage(),
                    "Transaction Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    });
    
    // Add components to item panel
    itemPanel.add(iconContainer, BorderLayout.WEST);
    itemPanel.add(descPanel, BorderLayout.CENTER);
    itemPanel.add(sellButton, BorderLayout.EAST);
    
    // Add to sell panel with some spacing
    itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    sellPanel.add(itemPanel);
    
    // Add a gap between items
    sellPanel.add(Box.createRigidArea(new Dimension(0, 10)));
}

/**
 * Show an animated success message when selling an item
 */
private void showSellSuccessMessage(String itemName, double price) {
    JDialog successDialog = new JDialog(this, false);
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
            g2d.drawRoundRect(1, 1, w-3, h-3, 20, 20);
        }
    };
    messagePanel.setLayout(new BorderLayout());
    messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
    messagePanel.setOpaque(false);
    
    JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 5));
    labelPanel.setOpaque(false);
    
    JLabel messageLabel = new JLabel("Sold: " + itemName);
    messageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
    messageLabel.setForeground(Color.WHITE);
    messageLabel.setHorizontalAlignment(JLabel.CENTER);
    
    JLabel priceLabel = new JLabel("Received: $" + price);
    priceLabel.setFont(normalFont);
    priceLabel.setForeground(new Color(200, 255, 200));
    priceLabel.setHorizontalAlignment(JLabel.CENTER);
    
    labelPanel.add(messageLabel);
    labelPanel.add(priceLabel);
    
    messagePanel.add(labelPanel, BorderLayout.CENTER);
    
    successDialog.setContentPane(messagePanel);
    successDialog.pack();
    successDialog.setLocationRelativeTo(this);
    
    // Show the dialog and close it after a short delay
    successDialog.setVisible(true);
    
    Timer timer = new Timer(1200, e -> successDialog.dispose());
    timer.setRepeats(false);
    timer.start();
}

    /**
     * Get an image icon for the item based on its description or type
     * (Used in the sell tab to display inventory items)
     */
    private ImageIcon getItemImage(TradeableItem item) {
        // Get class and description to determine appropriate icon
        String className = item.getClass().getSimpleName();
        String description = item.getDescription().toLowerCase();

        if (item instanceof Plant) {
            return getPlantIcon((Plant)item);
        } else if (item instanceof Road) {
            return getRoadIcon((Road)item);
        } else if (item instanceof Animal) {
            return getAnimalIcon((Animal)item);
        } else if (item instanceof TerrainObstacle) {
            return getObstacleIcon((TerrainObstacle)item);
        } else if (item instanceof WaterArea) {
            return pondIcon;
        } else if (item instanceof Ranger) {
            return rangerIcon;
        } else if (item instanceof Jeep) {
            return jeepIcon;
        }

        // Default to plant icon if no match
        return plantIcon;
    }
    }
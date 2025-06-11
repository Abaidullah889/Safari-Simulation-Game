package my.company.my.safarigame.view;

import my.company.my.safarigame.model.Player;
import my.company.my.safarigame.model.TradeableItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.util.*;
import java.util.List;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Enhanced inventory interface for displaying the player's items in the safari game.
 * <p>
 * This dialog displays a visually appealing inventory with images, prices, and quantities
 * of all items owned by the player. It provides functionality to view items and place them
 * on the game map. The inventory is presented in a table format with custom styling to match
 * the safari theme, including gradient backgrounds, rounded borders, and hover effects.
 * </p>
 * <p>
 * The dialog communicates with the game's placement system through an ItemPlacementHandler
 * to allow players to select and place items from their inventory onto the game map.
 * </p>
 */
public class InventoryView extends JDialog {
    /** Reference to the player whose inventory is being displayed. */
    private Player player;
    
    /** Table component for displaying inventory items. */
    private JTable inventoryTable;
    
    /** Data model for the inventory table. */
    private DefaultTableModel tableModel;
    
    /** Label showing the total count of items in inventory. */
    private JLabel countLabel;
    
    /** Font used for titles in the interface. */
    private Font titleFont = new Font("Comic Sans MS", Font.BOLD, 26);
    
    /** Font used for headings in the interface. */
    private Font headingFont = new Font("Comic Sans MS", Font.BOLD, 18);
    
    /** Font used for content text in the interface. */
    private Font contentFont = new Font("Comic Sans MS", Font.PLAIN, 16);
    
    /** Orange color used in the safari theme. */
    private Color safariOrange = new Color(255, 165, 0);
    
    /** Dark brown color used in the safari theme. */
    private Color darkBrown = new Color(139, 69, 19);
    
    /** Light beige color used in the safari theme. */
    private Color lightBeige = new Color(245, 222, 179);
    
    /** Background color for panels. */
    private Color panelBg = new Color(50, 50, 50);
    
    /** Column names for the inventory table. */
    private final String[] columnNames = {"Image", "Value ($)", "Quantity"};
    
    /** Size of item images in pixels. */
    private final int IMAGE_SIZE = 60;
   
    /** Icon representing grass terrain. */
    private ImageIcon grassIcon;
    
    /** Icon representing plant terrain. */
    private ImageIcon plantIcon;
    
    /** Icon representing shrub terrain. */
    private ImageIcon shrubIcon;
    
    /** Icon representing bush terrain. */
    private ImageIcon bushIcon;
    
    /** Icon representing wall terrain. */
    private ImageIcon wallIcon;
    
    /** Icon representing top gate terrain. */
    private ImageIcon tgateIcon;
    
    /** Icon representing bottom gate terrain. */
    private ImageIcon bgateIcon;
    
    /** Icon representing corner wall (top) terrain. */
    private ImageIcon awallIcon;
    
    /** Icon representing corner wall (bottom) terrain. */
    private ImageIcon swallIcon;
    
    /** Icon representing side wall terrain. */
    private ImageIcon lwallIcon;
    
    /** Icon representing horizontal road terrain. */
    private ImageIcon horizontalRoadIcon;
    
    /** Icon representing vertical road terrain. */
    private ImageIcon verticalRoadIcon;
    
    /** Icon representing right-up curved road terrain. */
    private ImageIcon rightupRoadIcon;
    
    /** Icon representing right-down curved road terrain. */
    private ImageIcon rightdownRoadIcon;
    
    /** Icon representing left-up curved road terrain. */
    private ImageIcon leftupRoadIcon;
    
    /** Icon representing left-down curved road terrain. */
    private ImageIcon leftdownRoadIcon;
    
    /** Icon representing cow animal. */
    private ImageIcon cowIcon;
    
    /** Icon representing deer animal. */
    private ImageIcon deerIcon;
    
    /** Icon representing lion animal. */
    private ImageIcon lionIcon;
    
    /** Icon representing wolf animal. */
    private ImageIcon wolfIcon;
    
    /** Icon representing water area/pond terrain. */
    private ImageIcon pondIcon;
    
    /** Icon representing ranger character. */
    private ImageIcon rangerIcon;
    
    /** Icon representing jeep vehicle. */
    private ImageIcon jeepIcon;
    
    /** Icon representing mountain obstacle. */
    private ImageIcon mountainIcon;
    
    /** Icon representing river obstacle. */
    private ImageIcon riverIcon;
    
    /** Handler for placing items on the map. */
    private ItemPlacementHandler placementHandler;
    
    /** Button for placing selected items. */
    private JButton placeItemButton;
    
    /** Currently selected item for placement. */
    private TradeableItem selectedItem;
    
    /** Quantity of the currently selected item. */
    private int selectedItemQuantity;
    
    /** List of unique keys for items in the table. */
    private List<String> tableKeys = new ArrayList<>();
    
    /**
     * Sets the item placement handler.
     * <p>
     * This method connects the inventory view to the item placement system,
     * allowing selected items to be placed on the game map.
     * </p>
     *
     * @param handler The handler to use for item placement
     */
    public void setItemPlacementHandler(ItemPlacementHandler handler) {
        this.placementHandler = handler;
    }
    
    /**
     * Constructs a new InventoryView dialog.
     * <p>
     * Creates and initializes the inventory interface for the specified player,
     * loading item images and setting up the UI components.
     * </p>
     *
     * @param parent The parent frame for this dialog
     * @param player The player whose inventory will be displayed
     */
    public InventoryView(JFrame parent, Player player) {
        super(parent, "Safari Inventory", true);
        this.player = player;
        
        // Load images first
        loadTerrainImages();
        
        // Create and set up the dialog
        createUI();
        
        // Position relative to parent
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            setLocationByPlatform(true);
        }
    }
    
    /**
     * Creates the user interface for the inventory dialog.
     * <p>
     * Sets up the main layout and components, including the title panel,
     * inventory table, and button panel.
     * </p>
     */
    private void createUI() {
        // Set dialog size and layout
        setSize(650, 550);
        setLayout(new BorderLayout(10, 10));
        
        // Create a custom panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);
        
        // Add title panel at top
        mainPanel.add(createTitlePanel(), BorderLayout.NORTH);
        
        // Add inventory table in center
        mainPanel.add(createInventoryPanel(), BorderLayout.CENTER);
        
        // Add button panel at bottom
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        // Handle close dialog with X button
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    /**
     * Creates the title panel with inventory heading and item count.
     * <p>
     * The title panel includes a stylized heading with the "Safari Inventory" title
     * and a counter showing the total number of items in the inventory.
     * </p>
     *
     * @return The configured title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Create a fancy title panel with a background
        JPanel titlePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
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
        JLabel titleLabel = new JLabel("Safari Inventory", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Item count panel
        JPanel countPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
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
        countPanel.setOpaque(false);
        countPanel.setLayout(new BorderLayout());
        countPanel.setBorder(new EmptyBorder(8, 15, 8, 15));
        countPanel.setPreferredSize(new Dimension(180, 40));
        
        // Count label
        int totalItems = player.getInventory().size();
        countLabel = new JLabel("Items: " + totalItems, SwingConstants.CENTER);
        countLabel.setFont(contentFont);
        countLabel.setForeground(Color.WHITE);
        countPanel.add(countLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.CENTER);
        panel.add(countPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the inventory display panel with the scrollable table.
     * <p>
     * This panel contains the main inventory table showing items, their values,
     * and quantities, with custom rendering for images and styling.
     * </p>
     *
     * @return The configured inventory panel
     */
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Create table model with columns
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                // Make first column render as icon
                return column == 0 ? ImageIcon.class : Object.class;
            }
        };
        
        // Create custom cell renderer for images with background
        TableCellRenderer imageCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                label.setText(""); // No text, just the icon
                label.setIcon((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER);
                
                if (isSelected) {
                    label.setBackground(new Color(100, 70, 30));
                    label.setBorder(BorderFactory.createLineBorder(safariOrange, 2));
                } else {
                    label.setBackground(new Color(60, 60, 60));
                    label.setBorder(BorderFactory.createLineBorder(darkBrown, 1));
                }
                
                return label;
            }
        };
        
        // Custom cell renderer for text columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(contentFont);
                
                if (isSelected) {
                    label.setBackground(new Color(100, 70, 30));
                    label.setForeground(Color.WHITE);
                    label.setBorder(BorderFactory.createLineBorder(safariOrange, 1));
                } else {
                    label.setBackground(new Color(70, 70, 70));
                    label.setForeground(Color.WHITE);
                    label.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                }
                
                return label;
            }
        };
        
        // Create and configure table
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(contentFont);
        inventoryTable.setRowHeight(IMAGE_SIZE + 10); // Make rows tall enough for images
        inventoryTable.setBackground(new Color(50, 50, 50));
        inventoryTable.setForeground(Color.WHITE);
        inventoryTable.setSelectionBackground(new Color(120, 80, 30));
        inventoryTable.setSelectionForeground(Color.WHITE);
        inventoryTable.setGridColor(new Color(100, 100, 100));
        inventoryTable.setBorder(BorderFactory.createLineBorder(darkBrown, 2));
        inventoryTable.setShowGrid(true);
        
        // Configure column headers
        inventoryTable.getTableHeader().setFont(headingFont);
        inventoryTable.getTableHeader().setBackground(safariOrange);
        inventoryTable.getTableHeader().setForeground(Color.WHITE);
        inventoryTable.getTableHeader().setBorder(BorderFactory.createLineBorder(darkBrown, 2));
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(IMAGE_SIZE + 30); // Image
        inventoryTable.getColumnModel().getColumn(0).setCellRenderer(imageCellRenderer);
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Value
        inventoryTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Quantity
        inventoryTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        
        // Set up table selection
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setRowSelectionAllowed(true);
        setupTableSelection();
        
        
        // Load inventory data
        loadInventoryData();
        
        // Create custom scroll pane for table
        JScrollPane scrollPane = new JScrollPane(inventoryTable) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(darkBrown);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
            }
        };
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBackground(new Color(0, 0, 0, 0));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = safariOrange;
                this.trackColor = new Color(70, 70, 70);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = super.createDecreaseButton(orientation);
                button.setBackground(darkBrown);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = super.createIncreaseButton(orientation);
                button.setBackground(darkBrown);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });
        
        // Add to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the button panel with Place Item and Close buttons.
     * <p>
     * This panel contains buttons for placing selected items on the map
     * and for closing the inventory dialog.
     * </p>
     *
     * @return The configured button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Create Place Item button
        placeItemButton = createStyledButton("Place Item", 140, 45);
        placeItemButton.setEnabled(false); // Disabled until an item is selected
        placeItemButton.addActionListener(e -> {
            if (selectedItem != null && placementHandler != null) {
                placementHandler.handleItemPlacement(selectedItem);
                // Remove item from inventory
                player.removeInventoryItem(selectedItem);
                // Update the inventory display
                loadInventoryData();
                // Disable the place button
                placeItemButton.setEnabled(false);
                dispose(); // Close inventory after selecting item to place
            }
        });
        
        // Create styled close button
        JButton closeButton = createStyledButton("Close", 140, 45);
        closeButton.addActionListener(e -> dispose());
        
        panel.add(placeItemButton);
        panel.add(closeButton);
        return panel;
    }
    
    /**
     * Creates a styled button with gradients and effects.
     * <p>
     * This method creates a custom JButton with safari-themed styling,
     * including gradient backgrounds, rounded corners, and hover effects.
     * </p>
     *
     * @param text The text to display on the button
     * @param width The button width in pixels
     * @param height The button height in pixels
     * @return The styled JButton
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
                g2d.setFont(contentFont);
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
        button.setFont(contentFont);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }
    
    /**
     * Loads the terrain image icons from resources.
     * <p>
     * Attempts to load images from resource files, with fallback to colored
     * rectangles if images cannot be loaded.
     * </p>
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
            
            cowIcon = loadAndResizeImage("/tiles/cow1.png");
            deerIcon = loadAndResizeImage("/tiles/deer1.png");
            lionIcon = loadAndResizeImage("/tiles/lion1.png");
            wolfIcon = loadAndResizeImage("/tiles/wolf1.png");
            
            pondIcon = loadAndResizeImage("/tiles/pond1.png");
            
            rangerIcon = loadAndResizeImage("/tiles/ranger1.png");
            
            jeepIcon = loadAndResizeImage("/tiles/jeep1.png");
            
            mountainIcon = loadAndResizeImage("/tiles/jeep1.png");
            riverIcon = loadAndResizeImage("/tiles/jeep1.png");
            
            
        } catch (Exception e) {
            // Fallback to colored squares if images can't be loaded
            wallIcon = createColorIcon(Color.DARK_GRAY);
            grassIcon = createColorIcon(Color.GREEN);
            awallIcon = createColorIcon(Color.DARK_GRAY);
            swallIcon = createColorIcon(Color.DARK_GRAY);
            lwallIcon = createColorIcon(Color.DARK_GRAY);
            tgateIcon = createColorIcon(Color.LIGHT_GRAY);
            bgateIcon = createColorIcon(Color.LIGHT_GRAY);
            plantIcon = createColorIcon(new Color(0, 180, 0));
            shrubIcon = createColorIcon(new Color(0, 100, 0));
            bushIcon = createColorIcon(new Color(100, 200, 100));
            horizontalRoadIcon = createColorIcon(Color.GRAY);
            verticalRoadIcon = createColorIcon(Color.GRAY);
            rightupRoadIcon = createColorIcon(Color.GRAY);
            rightdownRoadIcon = createColorIcon(Color.GRAY);
            leftupRoadIcon = createColorIcon(Color.GRAY);
            leftdownRoadIcon = createColorIcon(Color.GRAY);
        }
    }
    
    /**
     * Loads an image from resources and resizes it to the appropriate cell size.
     *
     * @param path The resource path to the image
     * @return The loaded and resized ImageIcon
     */
    private ImageIcon loadAndResizeImage(String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image img = icon.getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return createColorIcon(Color.MAGENTA); // Error color as fallback
        }
    }
    
    /**
     * Creates a colored square icon as a fallback when image loading fails.
     *
     * @param color The color for the fallback icon
     * @return The colored square icon
     */
    private ImageIcon createColorIcon(Color color) {
        BufferedImage img = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        g.dispose();
        return new ImageIcon(img);
    } 
    
    /**
     * Loads inventory data into the table.
     * <p>
     * Populates the inventory table with the player's items, grouping identical
     * items together and displaying appropriate images, prices, and quantities.
     * </p>
     */
    private void loadInventoryData() {
        // Clear existing table data
        tableModel.setRowCount(0);
        tableKeys.clear(); // Clear the keys list
        
        // Get the player's inventory
        List<TradeableItem> inventory = player.getInventory();
        
        if (inventory.isEmpty()) {
            // Show empty inventory message
            showEmptyInventoryMessage();
            return;
        }
        
        // Count items by combination of class name AND description
        Map<String, Integer> itemCounts = new HashMap<>();
        Map<String, TradeableItem> uniqueItems = new HashMap<>();
        
        for (TradeableItem item : inventory) {
            // Create a composite key using both class name and description
            String compositeKey = getItemKey(item);
            
            // Add this item to our counts
            itemCounts.put(compositeKey, itemCounts.getOrDefault(compositeKey, 0) + 1);
            
            // Store a reference to this unique item
            if (!uniqueItems.containsKey(compositeKey)) {
                uniqueItems.put(compositeKey, item);
            }
        }
        
        // Add each unique item to the table with count and image
        for (Map.Entry<String, TradeableItem> entry : uniqueItems.entrySet()) {
            TradeableItem item = entry.getValue();
            String compositeKey = entry.getKey();
            int quantity = itemCounts.get(compositeKey);
            ImageIcon itemImage = getItemImage(item);
            
            Object[] row = {
                itemImage,
                String.format("%.2f", item.getPrice()),
                quantity
            };
            
            tableModel.addRow(row);
            tableKeys.add(compositeKey); // Store the key
        }
        
        // Update the total items count
        updateItemCount();
    }
    /**
     * Shows a message when the inventory is empty.
     * <p>
     * Displays a stylized panel with a message and icon indicating that
     * the player's inventory is empty.
     * </p>
     */
    private void showEmptyInventoryMessage() {
        // Clear the table first
        tableModel.setRowCount(0);
        
        // Create a panel to display in the center of the table area
        JPanel emptyPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create rounded rectangle with gradient
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(70, 70, 70),
                    0, h, new Color(50, 50, 50)
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 20, 20);
                
                // Add border
                g2d.setColor(darkBrown);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, w-3, h-3, 20, 20);
            }
        };
        emptyPanel.setOpaque(false);
        emptyPanel.setLayout(new BorderLayout());
        emptyPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create a message
        JLabel emptyLabel = new JLabel("Your inventory is empty", SwingConstants.CENTER);
        emptyLabel.setFont(headingFont);
        emptyLabel.setForeground(Color.WHITE);
        
        JLabel subLabel = new JLabel("Visit the market to purchase items", SwingConstants.CENTER);
        subLabel.setFont(contentFont);
        subLabel.setForeground(new Color(200, 200, 200));
        
        JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        labelPanel.setOpaque(false);
        labelPanel.add(emptyLabel);
        labelPanel.add(subLabel);
        
        // Add a grass icon
        JLabel iconLabel = new JLabel(grassIcon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        
        emptyPanel.add(iconLabel, BorderLayout.NORTH);
        emptyPanel.add(labelPanel, BorderLayout.CENTER);
        
        // Update the item count
        countLabel.setText("Items: 0");
    }
    
    /**
     * Updates the item count display.
     * <p>
     * Updates the label showing the total number of items in the player's inventory.
     * </p>
     */
    private void updateItemCount() {
        int totalItems = player.getInventory().size();
        countLabel.setText("Items: " + totalItems);
    }
    
    /**
     * Gets an appropriate image icon for an item based on its description or type.
     * <p>
     * This method analyzes the item's description and class name to determine
     * the most appropriate icon to display for it in the inventory.
     * </p>
     *
     * @param item The item to get an icon for
     * @return The appropriate ImageIcon for the item
     */
    private ImageIcon getItemImage(TradeableItem item) {
        // Get the description to help determine the item type
        String description = item.getDescription();
        
        // Try to match the description to the cell types in your grid loader
        if (description != null) {
            description = description.toLowerCase();
            
            // Check for plant types
            if (description.contains("bush")) {
                return bushIcon; // 'b' in your grid
            } else if (description.contains("plant")) {
                return plantIcon; // 'p' in your grid
            } else if (description.contains("shrub")) {
                return shrubIcon; // 'h' in your grid
            } 
            
            // Check for road types
            else if (description.contains("vertical")) {
                return verticalRoadIcon; // '|' in your grid
            } else if (description.contains("horizontal")) {
                return horizontalRoadIcon; // 'r' in your grid
            } else if (description.contains("rightdown")) {
                return rightdownRoadIcon; // '1' in your grid
            } else if (description.contains("rightup")) {
                return rightupRoadIcon; // '2' in your grid
            } else if (description.contains("leftup")) {
                return leftupRoadIcon; // '3' in your grid
            } else if (description.contains("leftdown")) {
                return leftdownRoadIcon; // '4' in your grid
            }
            
            // Check for wall/barrier types
            else if (description.contains("wall")) {
                return wallIcon; // 'w' in your grid
            } else if (description.contains("corner") && description.contains("top")) {
                return awallIcon; // 'a' in your grid
            } else if (description.contains("corner") && description.contains("bottom")) {
                return swallIcon; // 's' in your grid
            } else if (description.contains("side")) {
                return lwallIcon; // 'l' in your grid
            } else if (description.contains("gate") && description.contains("top")) {
                return tgateIcon; // 'g' in your grid
            } else if (description.contains("gate") && description.contains("bottom")) {
                return bgateIcon; // 't' in your grid
            }
            
            //Check for animals
            else if (description.contains("deer")) {
                return deerIcon; // 'w' in your grid
            } else if (description.contains("lion")) {
                return lionIcon; // 'a' in your grid
            } else if (description.contains("cow")) {
                return cowIcon; // 's' in your grid
            } else if (description.contains("wolf")) {
                return wolfIcon; // 'l' in your grid
            }
            
            //Check for water area
            else if (description.contains("waterarea")) {
                return pondIcon; // 'w' in your grid
            }
            
            //Check for ranger
            else if (description.contains("ranger")) {
                return rangerIcon; // 'w' in your grid
            }
            
            //Check for jeep
            else if (description.contains("jeep")) {
                return jeepIcon; // 'w' in your grid
            }
            
            //Check for Obstacles
            else if (description.contains("river")) {
                return riverIcon; // 'w' in your grid
            }
            else if (description.contains("mountain")) {
                return mountainIcon; // 'w' in your grid
            }
        }
        
        // If no description match, check the class name
        String className = item.getClass().getSimpleName();
        
        if (className.contains("Plant") || className.contains("Vegetation")) {
            // Check further to see what type of plant
            if (className.contains("Bush")) return bushIcon;
            if (className.contains("Shrub")) return shrubIcon;
            return plantIcon; // Default plant
        } 
        else if (className.contains("Road")) {
            // Default to horizontal road if not specific
            return horizontalRoadIcon;
        }
        else if (className.contains("Wall") || className.contains("Barrier")) {
            // Default to regular wall
            return wallIcon;
        }
        
        // Default to grass icon for unknown items
        return grassIcon;
    }
    
    /**
     * Sets up the selection tracking for the inventory table.
     * <p>
     * Configures the table selection listener to track which item is selected
     * and enable/disable the Place Item button accordingly.
     * </p>
     */
    private void setupTableSelection() {
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = inventoryTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) {
                    // Get the quantity from the table
                    int quantity = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
                    String key = tableKeys.get(selectedRow);
                    
                    // Find the item in the player's inventory
                    for (TradeableItem item : player.getInventory()) {
                        String itemKey = getItemKey(item);
                        if (itemKey.equals(key)) {
                            selectedItem = item;
                            selectedItemQuantity = quantity;
                            placeItemButton.setEnabled(true);
                            return;
                        }
                    }
                } else {
                    selectedItem = null;
                    placeItemButton.setEnabled(false);
                }
            }
        });
    }
    
    /**
     * Gets a unique key for an item based on its class and description.
     * <p>
     * Creates a composite key that uniquely identifies an item type, combining
     * the class name and description to group identical items together.
     * </p>
     *
     * @param item The item to get a key for
     * @return The unique key string for the item
     */
    private String getItemKey(TradeableItem item) {
        return item.getClass().getSimpleName() + ":" + item.getDescription();
    }
}
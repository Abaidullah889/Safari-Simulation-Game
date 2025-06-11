package my.company.my.safarigame.view;

import my.company.my.safarigame.model.Player;
import my.company.my.safarigame.controller.GameController;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;

/**
 * DashboardView displays game status information and controls in the Safari Game.
 * <p>
 * This class provides a graphical dashboard interface that shows player information,
 * game status, and control buttons. It includes a mini-map view, player statistics
 * (name, capital, game speed), day/night indicators, and buttons for accessing
 * inventory, market, and other game functions.
 * </p>
 * <p>
 * The dashboard is typically positioned at the bottom of the game screen and serves
 * as the main control hub for the player. It communicates with the GameController to
 * update game state and with the MapView to coordinate visual elements.
 * </p>
 */
public class DashboardView {

    /** Main panel containing all dashboard components. */
    private JPanel dashboardPanel;

    /** Label indicating whether it's currently day or night in the game. */
    private JLabel dayNightIndicator;
    
    /** Button for toggling between day and night modes. */
    private JButton nightModeToggleButton;
    
    /** Button for advancing time more quickly. */
    private JButton fastForwardButton;

    /** Button for saving the game. */
    private JButton saveGameButton;

    /** Label displaying the current game time. */
    private JLabel timeLabel;
    
    /** Mini-map view showing a scaled-down view of the game world. */
    private MiniMapView miniMapView;

    /** The player whose information is displayed in the dashboard. */
    private Player player;
    
    /** The current game speed setting. */
    final private int gameSpeed;

    /** Custom font used for dashboard text. */
    private Font customFont = new Font("Comic Sans MS", Font.PLAIN, 24);
    
    /** Font used for headings in the dashboard. */
    private Font headingFont = new Font("Comic Sans MS", Font.BOLD, 18);
    
    /** Orange color used in the safari theme. */
    private Color safariOrange = new Color(255, 165, 0);
    
    /** Dark brown color used in the safari theme. */
    private Color darkBrown = new Color(139, 69, 19);

    /** Label showing the current location in the game world. */
    private JLabel locationLabel;

    /** Label showing the player's current capital. */
    private JLabel capitalLabel;

    /** Progress bar showing progress toward capital goal. */
    private JProgressBar capitalProgressBar;
    
    /** Constant representing the capital goal to win the game. */
    private static final double CAPITAL_GOAL = 5000.0;

    /** Reference to the map view for coordination. */
    private MapView mapView;

    /** Reference to the game controller for communication. */
    private GameController controller;

    /** Parent frame reference for dialogs. */
    private JFrame parentFrame;

    /** Handler for item placement functionality. */
    private ItemPlacementHandler itemPlacementHandler;

    /** Label showing the number of tourists in the game. */
    private JLabel touristLabel;

    /**
     * Sets the item placement handler for inventory operations.
     * <p>
     * This handler is used to place items from inventory onto the map.
     * </p>
     *
     * @param handler The item placement handler to use
     */
    public void setItemPlacementHandler(ItemPlacementHandler handler) {
        this.itemPlacementHandler = handler;
    }

    /**
     * Opens the inventory view dialog.
     * <p>
     * Displays a dialog showing the player's inventory of items and allows interaction
     * with those items including placing them on the map.
     * </p>
     */
    private void openInventoryView() {
        // Ensure we have necessary references
        if (player == null) {
            JOptionPane.showMessageDialog(
                    dashboardPanel,
                    "Player not initialized",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Determine parent frame
        JFrame frame = parentFrame;
        if (frame == null) {
            // Try to find the parent frame
            Container parent = dashboardPanel.getTopLevelAncestor();
            if (parent instanceof JFrame) {
                frame = (JFrame) parent;
            } else {
                // Create a new frame as a last resort
                frame = new JFrame("Safari Inventory");
            }
        }

        // Create and show the inventory view
        InventoryView inventoryView = new InventoryView(frame, player);

        // Set the placement handler if available
        if (itemPlacementHandler != null) {
            inventoryView.setItemPlacementHandler(itemPlacementHandler);
        }

        inventoryView.setVisible(true);
    }

    /**
     * Constructs a new DashboardView with the specified player and game speed.
     *
     * @param player The player whose information will be displayed
     * @param gameSpeed The current game speed setting
     */
    public DashboardView(Player player, int gameSpeed) {
        this.player = player;
        this.gameSpeed = gameSpeed;

        // Create the mini map
        this.miniMapView = new MiniMapView();

        // Create the dashboard panel
        this.dashboardPanel = createDashboardPanel();
    }

    /**
     * Constructs a new DashboardView with the specified player, game speed, and controller.
     *
     * @param player The player whose information will be displayed
     * @param gameSpeed The current game speed setting
     * @param controller The game controller for communication
     */
    public DashboardView(Player player, int gameSpeed, GameController controller) {
        this.player = player;
        this.gameSpeed = gameSpeed;
        this.controller = controller;

        // Create the mini map
        this.miniMapView = new MiniMapView();

        // Create the dashboard panel
        this.dashboardPanel = createDashboardPanel();
    }

    /**
     * Sets the parent frame for opening dialogs.
     * <p>
     * This frame is used as the parent for dialog windows launched from the dashboard.
     * </p>
     *
     * @param parentFrame The parent JFrame
     */
    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Sets the reference to the main map view.
     * <p>
     * Establishes the connection between the dashboard and the map view,
     * and updates the mini-map with the reference to the main map.
     * </p>
     *
     * @param mapView The main map view
     */
    public void setMapView(MapView mapView) {
        this.mapView = mapView;

        // Ensure we update the mini map to point to this map view
        if (this.miniMapView != null && mapView != null) {
            mapView.setMiniMapView(miniMapView);
        }

        // Update the button panel with the removal button
        updateButtonPanel();
    }

    /**
     * Sets the reference to the game controller.
     *
     * @param controller The game controller
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Gets the dashboard panel for display.
     *
     * @return The main dashboard JPanel
     */
    public JPanel getDashboardPanel() {
        return dashboardPanel;
    }

    /**
     * Gets the mini map view for connection to map view.
     *
     * @return The MiniMapView instance
     */
    public MiniMapView getMiniMapView() {
        return miniMapView;
    }

    /**
     * Creates the main dashboard panel with all components.
     * <p>
     * Sets up the layout with player information panel, control buttons,
     * and the mini-map view arranged in a layout.
     * </p>
     *
     * @return The complete dashboard panel
     */
    private JPanel createDashboardPanel() {
        // Create main dashboard panel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 180)); // Increased height for dashboard
        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add some padding

        // Create a panel for the left and center content
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.DARK_GRAY);

        // Create player info panel for the center-left
        JPanel infoPanel = createPlayerInfoPanel();
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        // Create button panel for the bottom of left panel
        JPanel buttonPanel = createButtonPanel();
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create mini map panel (right side)
        JPanel miniMapPanel = createMiniMapPanel();

        // Add panels to dashboard
        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(miniMapPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the player information panel with stats and indicators.
     * <p>
     * Displays the player's name, capital, game speed, tourist count,
     * current time, and day/night indicator.
     * </p>
     *
     * @return The player information panel
     */
    private JPanel createPlayerInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 6, 15, 0));  // ✅ Matches actual number of elements

        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = createInfoLabel("Name: " + player.getName());
        capitalLabel = createInfoLabel("Capital: $" + player.getCapital());
        JLabel speedLabel = createInfoLabel("Speed: " + getSpeedLabel(gameSpeed));

        // Add new tourist count label
        touristLabel = createInfoLabel("Tourists: 0");

        panel.add(nameLabel);
        panel.add(capitalLabel);
        panel.add(speedLabel);
        panel.add(touristLabel);

        timeLabel = createInfoLabel("Day 1, Month 1, 8:00 AM");
        panel.add(timeLabel);

        dayNightIndicator = new JLabel("\u2600\uFE0F Day", SwingConstants.CENTER);
        dayNightIndicator.setFont(headingFont);
        dayNightIndicator.setForeground(Color.WHITE);
        panel.add(dayNightIndicator);

        return panel;
    }

    /**
     * Updates the tourist count display.
     *
     * @param touristCount The number of tourists to display
     */
    public void updateTouristDisplay(int touristCount) {
        if (touristLabel != null) {
            SwingUtilities.invokeLater(() -> touristLabel.setText("Tourists: " + touristCount));
        }
    }

    /**
     * Updates the time display with the provided time string.
     *
     * @param timeStr The time string to display
     */
    public void updateTimeDisplay(String timeStr) {
        if (timeLabel != null) {
            timeLabel.setText(timeStr);
        }
    }

    /**
     * Creates a styled information label.
     * <p>
     * Creates a JLabel with standardized styling for information display,
     * including font, colors, and borders.
     * </p>
     *
     * @param text The text to display in the label
     * @return The styled label
     */
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(customFont);

        // Create panel for the label with a border
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(darkBrown, 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(label, BorderLayout.CENTER);

        return label;
    }

    /**
     * Creates the mini-map panel with border and container.
     *
     * @return The mini-map panel
     */
    private JPanel createMiniMapPanel() {
        // Create panel for mini map with border layout
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.DARK_GRAY);

        // Create panel for mini map with border
        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.setBackground(Color.DARK_GRAY);
        mapContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Make sure mini map is perfectly centered
        mapContainer.add(miniMapView.getMiniMapPanel(), BorderLayout.CENTER);

        // Add the map container to the main panel
        panel.add(mapContainer, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Updates the location label to show the current area.
     *
     * @param locationName The name of the current location
     */
    public void updateLocationLabel(String locationName) {
        if (locationLabel != null) {
            locationLabel.setText("Current Area: " + locationName);
        }
    }

    /**
     * Creates the button panel with game control buttons.
     * <p>
     * Sets up buttons for inventory, market, exit game, save game,
     * night mode toggle, and fast forward.
     * </p>
     *
     * @return The button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));

        // Create buttons
        JButton inventoryButton = createButton("Inventory");
        JButton marketButton = createButton("Market");
        JButton exitButton = createButton("Exit Game");
        saveGameButton = createButton("Save Game");

        // Add action to save game button
        saveGameButton.addActionListener(e -> {
            if (controller != null) {
                // Prompt for filename
                String fileName = promptForFileName();
                if (fileName != null && !fileName.trim().isEmpty()) {
                    // Call the save function with provided filename
                    controller.saveToGrid(fileName);
                }
            }
        });
        // Add action to inventory button
        inventoryButton.addActionListener(e -> {
            openInventoryView();
        });

        // Add action to market button
        marketButton.addActionListener(e -> {
            openMarketView();
        });

        // Add action to exit button
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to panel - do NOT add removalButton here as it's null
        panel.add(inventoryButton);
        panel.add(marketButton);
        panel.add(exitButton);
        panel.add(saveGameButton);

        nightModeToggleButton = createButton("Toggle Night Mode");
        nightModeToggleButton.addActionListener(e -> {
            if (controller != null && mapView != null) {
                boolean currentNightMode = !controller.getModel().getCurrentTime().isDaytime();
                controller.toggleNightMode(!currentNightMode);
            }
        });

        panel.add(nightModeToggleButton);
        fastForwardButton = createButton("⏩ Fast Forward");
        fastForwardButton.addActionListener(e -> {
            if (controller != null) {
                controller.advanceTimeQuickly();
            }
        });

        panel.add(fastForwardButton);

        return panel;
    }

    /**
     * Opens the market view dialog.
     * <p>
     * Displays a dialog showing the market interface where the player can
     * buy and sell items.
     * </p>
     */
    private void openMarketView() {
        // Ensure we have necessary references
        if (controller == null) {
            JOptionPane.showMessageDialog(
                    dashboardPanel,
                    "Game controller not initialized",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Determine parent frame
        JFrame frame = parentFrame;
        if (frame == null) {
            // Try to find the parent frame
            Container parent = dashboardPanel.getTopLevelAncestor();
            if (parent instanceof JFrame) {
                frame = (JFrame) parent;
            } else {
                // Create a new frame as a last resort
                frame = new JFrame("Safari Market");
            }
        }

        // Create the market view
        MarketView marketView = new MarketView(
                frame,
                controller.getModel().getMarket(),
                controller.getModel().getPlayer(),
                controller
        );

        // Add a window listener to update the dashboard when market closes
        marketView.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                // Update the capital display when market is closed
                updateCapitalDisplay();
            }
        });

        // Show the market view
        marketView.setVisible(true);

        // Update capital display after market interaction
        updateCapitalDisplay();
    }

    /**
     * Creates a styled button with consistent appearance.
     *
     * @param text The text to display on the button
     * @return The styled button
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(customFont);
        button.setBackground(safariOrange);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(180, 50));
        button.setFocusPainted(false);

        // Add a border
        button.setBorder(BorderFactory.createLineBorder(darkBrown, 3));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 200, 100)); // Lighter orange
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(safariOrange);
                button.setBorder(BorderFactory.createLineBorder(darkBrown, 3));
            }
        });

        return button;
    }

    /**
     * Converts game speed value to a user-friendly label.
     *
     * @param speed The game speed value (1=Hour, 2=Day, 3=Week)
     * @return A user-friendly string representing the speed
     */
    private String getSpeedLabel(int speed) {
        switch (speed) {
            case 1:
                return "Hour";
            case 2:
                return "Day";
            case 3:
                return "Week";
            default:
                return "Unknown";
        }
    }

    /**
     * Updates the button panel with removal button after mapView is initialized.
     * <p>
     * This method adds the item removal toggle button from the MapView to the button panel.
     * </p>
     */
    public void updateButtonPanel() {
        if (mapView != null) {
            JToggleButton removalButton = mapView.getRemovalButton();
            if (removalButton != null) {
                // Get the button panel which is the SOUTH component of leftPanel
                JPanel leftPanel = (JPanel) dashboardPanel.getComponent(0);
                JPanel buttonPanel = (JPanel) leftPanel.getComponent(1);

                // Add the removal button between inventory and market buttons
                buttonPanel.add(removalButton, 1); // Add at position 1 (after inventory)
                buttonPanel.revalidate();
                buttonPanel.repaint();
            }
        }
    }

    /**
     * Updates the day/night indicator based on the current time.
     *
     * @param isDaytime true if it's daytime in the game, false if it's nighttime
     */
    public void updateDayNightIndicator(boolean isDaytime) {
        if (dayNightIndicator != null) {
            dayNightIndicator.setText(isDaytime ? "☀️ Day" : "🌙 Night");
            dayNightIndicator.setForeground(isDaytime ? Color.WHITE : new Color(180, 180, 255));
        }
    }

    /**
     * Shows a popup notification when capital changes due to tourist activity.
     * <p>
     * Displays a temporary notification indicating how much capital was gained
     * or lost from tourists viewing the safari.
     * </p>
     *
     * @param amount The amount of capital gained or lost
     */
    public void showCapitalIncreasePopup(int amount) {
        String message;
        if (amount > 0) {
            message = "Tourists loved the view! +$" + amount;
        } else {
            message = "Tourists didn't enjoy the view.";
        }

        JLabel popupLabel = new JLabel(message);
        popupLabel.setOpaque(true);
        popupLabel.setBackground(new Color(0, 0, 0, 180));
        popupLabel.setForeground(Color.WHITE);
        popupLabel.setFont(new Font("Arial", Font.BOLD, 14));
        popupLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        popupLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Size and position
        int width = 250;
        int height = 40;
        popupLabel.setSize(width, height);

        // Position it at the top-right corner (relative to parent frame)
        int x = parentFrame.getWidth() - width - 30;
        int y = 30;
        popupLabel.setLocation(x, y);

        // Add to the layered pane to appear above other components
        JLayeredPane layeredPane = parentFrame.getLayeredPane();
        layeredPane.add(popupLabel, JLayeredPane.POPUP_LAYER);
        layeredPane.repaint();

        // Remove after 3 seconds
        Timer fadeTimer = new Timer(3000, e -> {
            layeredPane.remove(popupLabel);
            layeredPane.repaint();
        });
        fadeTimer.setRepeats(false);
        fadeTimer.start();

        updateCapitalDisplay(); // Still update dashboard
    }

    /**
     * Prompts the user for a filename to save the game.
     * <p>
     * Displays a dialog allowing the user to enter a name for their saved game.
     * </p>
     *
     * @return The filename entered by the user or null if canceled
     */
    private String promptForFileName() {
        // Create a styled input dialog with our custom colors
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add instructions
        JLabel label = new JLabel("Enter a name for your saved game:");
        label.setFont(headingFont);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);

        // Text field for filename
        JTextField textField = new JTextField();
        textField.setFont(customFont);
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkBrown, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Generate a default filename with player name and current date/time
        String playerName = player != null ? player.getName() : "safari";
        String defaultName = playerName + "_" + System.currentTimeMillis();
        textField.setText(defaultName);

        // Select all text so user can easily replace it
        textField.selectAll();

        panel.add(textField, BorderLayout.CENTER);

        // Show the dialog
        int result = JOptionPane.showConfirmDialog(
                parentFrame,
                panel,
                "Save Game",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Process result
        if (result == JOptionPane.OK_OPTION) {
            return textField.getText().trim();
        } else {
            return null; // User canceled
        }
    }

    /**
     * Creates a panel displaying the progress toward the capital goal.
     * <p>
     * This panel includes a progress bar showing how close the player is to
     * reaching the capital goal needed to win the game.
     * </p>
     *
     * @return The capital progress panel
     */
    private JPanel createCapitalProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);

        JLabel goalLabel = new JLabel("Goal: $" + CAPITAL_GOAL);
        goalLabel.setFont(customFont);
        goalLabel.setForeground(Color.WHITE);

        capitalProgressBar = new JProgressBar(0, (int) CAPITAL_GOAL);
        capitalProgressBar.setValue((int) player.getCapital());
        capitalProgressBar.setStringPainted(true);
        capitalProgressBar.setString("$" + player.getCapital() + " / $" + CAPITAL_GOAL);
        capitalProgressBar.setForeground(new Color(255, 165, 0)); // Safari orange
        capitalProgressBar.setBackground(new Color(60, 60, 60));

        panel.add(goalLabel, BorderLayout.NORTH);
        panel.add(capitalProgressBar, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Updates the capital display with the current player capital.
     * <p>
     * Updates both the capital label and the progress bar (if present) to
     * reflect the player's current capital amount and progress toward the goal.
     * </p>
     */
    public void updateCapitalDisplay() {
        if (capitalLabel != null && player != null) {
            SwingUtilities.invokeLater(() -> {
                double currentCapital = player.getCapital();

                // Update capital label
                capitalLabel.setText("Capital: $" + currentCapital);
                capitalLabel.revalidate();
                capitalLabel.repaint();

                // Update progress bar if it exists
                if (capitalProgressBar != null) {
                    capitalProgressBar.setValue((int) currentCapital);
                    capitalProgressBar.setString("$" + currentCapital + " / $" + CAPITAL_GOAL);

                    // Change color as player gets closer to goal
                    if (currentCapital >= CAPITAL_GOAL * 0.75) {
                        capitalProgressBar.setForeground(new Color(0, 200, 0)); // Green when close
                    } else if (currentCapital >= CAPITAL_GOAL * 0.5) {
                        capitalProgressBar.setForeground(new Color(200, 200, 0)); // Yellow at halfway
                    }

                    capitalProgressBar.revalidate();
                    capitalProgressBar.repaint();
                }
            });
        }
    }
}
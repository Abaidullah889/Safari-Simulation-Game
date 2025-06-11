package my.company.my.safarigame.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.io.InputStream;
import my.company.my.safarigame.audio.SoundManager;
import my.company.my.safarigame.model.SafariGameModel;
import my.company.my.safarigame.controller.GameController;

/**
 * Main GameView class responsible for displaying the game interface.
 * <p>
 * This class follows the MVC (Model-View-Controller) pattern by communicating with
 * the model through the controller. It manages the main application window and all
 * user interface screens including the main menu, start game options, load game,
 * settings, and the main game screen with the map and dashboard.
 * </p>
 * <p>
 * The GameView handles user interface creation, screen transitions, button styling,
 * and dispatches user actions to the controller for processing.
 * </p>
 */
public class GameView {

    /** Main application window. */
    private JFrame frame;
    
    /** Current panel being displayed. */
    private JPanel currentPanel;

    /** Button to start a new game. */
    private JButton startButton;
    
    /** Button to load a saved game. */
    private JButton loadButton;
    
    /** Button to open the settings screen. */
    private JButton settingsButton;
    
    /** Button to exit the game. */
    private JButton exitButton;
    
    /** Button in the start game panel to actually start the game. */
    private JButton actualStartGameButton;

    /** View component displaying the game map. */
    private MapView mapView;
    
    /** View component displaying game status and controls. */
    private DashboardView dashboardView;

    /** Reference to the game model. */
    private SafariGameModel model;
    
    /** Reference to the game controller. */
    private GameController controller;

    /** Custom font used throughout the interface. */
    private Font customFont;

    /** Slider for adjusting sound volume in settings. */
    private JSlider soundSlider;
    
    /** Slider for adjusting game speed in settings. */
    private JSlider speedSlider;

    /** List of saved games for the load game screen. */
    private List<String> savedGames;

    /**
     * Constructs a new GameView with the specified model and controller.
     * <p>
     * Initializes the main application window and sets up the initial
     * user interface, starting with the main menu screen.
     * </p>
     *
     * @param model The game model containing game state
     * @param controller The game controller for processing user actions
     */
    public GameView(SafariGameModel model, GameController controller) {
        // Initialize the references
        this.model = model;
        this.controller = controller;

        // Set up UI elements
        customFont = new Font("Comic Sans MS", Font.PLAIN, 24);

        // Create frame
        frame = new JFrame("Safari Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        frame.setUndecorated(true); // Remove title bar
        frame.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Set the first screen as the main game screen
        currentPanel = createMainGamePanel();
        frame.getContentPane().add(currentPanel);

        // Make the frame visible
        frame.setLocationRelativeTo(null);  // Center on the screen
        frame.setVisible(true);
    }

    /**
     * Creates the main menu panel.
     * <p>
     * This panel displays the game title and main navigation buttons
     * (Start Game, Load, Settings, Exit).
     * </p>
     *
     * @return The main menu panel
     */
    private JPanel createMainGamePanel() {
        // Create a main panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout(30, 30));

        // Title label at the top
        JLabel titleLabel = new JLabel("Safari Game", SwingConstants.CENTER);
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 80f));
        titleLabel.setForeground(new Color(255, 165, 0));  // Safari Orange color

        // Create a panel for the title with more padding at the top
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setPreferredSize(new Dimension(frame.getWidth(), 200));

        // Add title panel to the top of the main panel
        panel.add(titlePanel, BorderLayout.NORTH);

        // Create buttons with consistent style and size
        Dimension buttonSize = new Dimension(300, 90);
        startButton = createButton("Start Game");
        loadButton = createButton("Load");
        settingsButton = createButton("Settings");
        exitButton = createButton("Exit");

        // Set all buttons to the same size
        startButton.setPreferredSize(buttonSize);
        loadButton.setPreferredSize(buttonSize);
        settingsButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);

        // Add action listeners for navigation
        startButton.addActionListener(e -> switchToStartGamePanel());
        loadButton.addActionListener(e -> switchToLoadGamePanel());
        settingsButton.addActionListener(e -> switchToSettingsPanel());
        exitButton.addActionListener(e -> exitGame());

        // Create a panel to hold the buttons and use GridBagLayout for alignment
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Align buttons in one column
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons expand horizontally
        gbc.insets = new Insets(15, 0, 15, 0); // Top, Left, Bottom, Right - 15px gap between buttons

        gbc.gridy = 0;
        buttonPanel.add(startButton, gbc);

        gbc.gridy++;
        buttonPanel.add(loadButton, gbc);

        gbc.gridy++;
        buttonPanel.add(settingsButton, gbc);

        gbc.gridy++;
        buttonPanel.add(exitButton, gbc);

        // Add button panel to the center
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the load game panel.
     * <p>
     * This panel displays a list of saved games that the player can load,
     * or a message indicating that no saved games were found.
     * </p>
     *
     * @return The load game panel
     */
    private JPanel createLoadGamePanel() {
        // Create the panel with a custom background
        JPanel panel = new JPanel(new BorderLayout(30, 30));

        // Title label at the top
        JLabel titleLabel = new JLabel("Load Game", SwingConstants.CENTER);
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 80f));
        titleLabel.setForeground(new Color(255, 165, 0));

        // Create a panel for the title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setPreferredSize(new Dimension(frame.getWidth(), 200));
        panel.add(titlePanel, BorderLayout.NORTH);

        // Create a panel for the list of saved games
        JPanel gameListPanel = new JPanel();
        gameListPanel.setLayout(new BoxLayout(gameListPanel, BoxLayout.Y_AXIS));
        gameListPanel.setOpaque(false);
        gameListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Get saved games from the controller
        List<String> availableSaves = controller.getAvailableSaves();

        if (availableSaves.isEmpty()) {
            // Display a message if no saved games are found
            JLabel noSavesLabel = new JLabel("No saved games found", SwingConstants.CENTER);
            noSavesLabel.setFont(customFont.deriveFont(Font.PLAIN, 24f));
            noSavesLabel.setForeground(new Color(100, 100, 100));
            noSavesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel labelWrapper = new JPanel();
            labelWrapper.setOpaque(false);
            labelWrapper.add(noSavesLabel);

            gameListPanel.add(labelWrapper);
        } else {
            // Add saved game buttons
            for (String savedGame : availableSaves) {
                JButton gameButton = createButton(savedGame);
                gameButton.setPreferredSize(new Dimension(300, 80));
                gameButton.setMaximumSize(new Dimension(300, 80));
                gameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                gameButton.addActionListener(e -> controller.loadGame(savedGame));

                // Create a wrapper panel for spacing
                JPanel buttonWrapper = new JPanel();
                buttonWrapper.setOpaque(false);
                buttonWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                buttonWrapper.add(gameButton);

                gameListPanel.add(buttonWrapper);
            }
        }

        // Add the game list panel to the center
        JPanel listContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        listContainer.setOpaque(false);
        listContainer.add(gameListPanel);
        panel.add(listContainer, BorderLayout.CENTER);

        // Create the Back button at the bottom
        JButton backButton = createButton("Back");
        backButton.setPreferredSize(new Dimension(250, 70));
        backButton.setMaximumSize(new Dimension(250, 70));
        backButton.addActionListener(e -> switchToMainGamePanel());

        // Center the Back button with spacing
        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        backPanel.setOpaque(false);
        backPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        backPanel.add(backButton);

        // Add the back panel to the bottom
        panel.add(backPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the start game panel with player options.
     * <p>
     * This panel allows the player to enter their name, select a difficulty level,
     * and adjust the game speed before starting a new game.
     * </p>
     *
     * @return The start game panel
     */
    private JPanel createStartGamePanel() {
        // Create a panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout(20, 20));

        // Title label at the top
        JLabel titleLabel = new JLabel("Start Game", SwingConstants.CENTER);
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 80f));
        titleLabel.setForeground(new Color(255, 165, 0));

        // Create a panel for the title label
        JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Create a panel to hold the input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Player name label and input field
        JLabel nameLabel = new JLabel("Enter Your Name", SwingConstants.CENTER);
        nameLabel.setFont(customFont);
        JTextField nameField = new JTextField(15);
        nameField.setFont(customFont);
        nameField.setMaximumSize(new Dimension(300, 100));

        // Speed slider
        JSlider speedSlider = new JSlider(1, 3, 2);
        speedSlider.setFont(customFont);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        // Custom labels for the speedSlider
        Hashtable<Integer, JLabel> speedLabelTable = new Hashtable<>();
        speedLabelTable.put(1, new JLabel("Hour"));
        speedLabelTable.put(2, new JLabel("Day"));
        speedLabelTable.put(3, new JLabel("Week"));
        for (Integer key : speedLabelTable.keySet()) {
            speedLabelTable.get(key).setFont(customFont);
        }
        speedSlider.setLabelTable(speedLabelTable);

        // Game Difficulty label and combo box
        JLabel difficultyLabel = new JLabel("Select Difficulty", SwingConstants.CENTER);
        difficultyLabel.setFont(customFont);
        String[] difficulties = {"Easy", "Medium", "Hard"};
        JComboBox<String> difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setFont(customFont);
        difficultyComboBox.setPreferredSize(new Dimension(300, 40));

        // Add spacing between components
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        speedSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add all input components to the inputPanel with gaps
        inputPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        inputPanel.add(nameLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(nameField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 70)));
        inputPanel.add(difficultyLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(difficultyComboBox);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 120)));
        inputPanel.add(speedSlider);

        // Create a wrapper panel with padding
        JPanel inputWrapperPanel = new JPanel();
        inputWrapperPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
        inputWrapperPanel.add(inputPanel);

        // Create Start Game button
        actualStartGameButton = createButton("Start Game");
        actualStartGameButton.addActionListener(e -> {
            // Validate player name
            String playerName = nameField.getText();
            if (!"".equals(playerName)) {
                // Use controller to start the game
                int selectedSpeed = speedSlider.getValue();
                String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
                controller.startGame(playerName, selectedSpeed, selectedDifficulty);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter your name.", "Name Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Create a back button
        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> switchToMainGamePanel());

        // Center the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        buttonPanel.add(actualStartGameButton);

        // Add everything to the panel
        panel.add(inputWrapperPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the settings panel.
     * <p>
     * This panel allows the player to adjust game settings such as sound volume
     * and game speed.
     * </p>
     *
     * @return The settings panel
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(30, 30));

        // Title label at the top
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 80f));
        titleLabel.setForeground(new Color(255, 165, 0));

        // Create a panel for the title label
        JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Create a panel for settings options
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create the sound slider
        soundSlider = new JSlider(0, 100, 100);
        soundSlider.setFont(customFont);
        soundSlider.setMajorTickSpacing(10);
        soundSlider.setMinorTickSpacing(5);
        soundSlider.setPaintTicks(true);
        soundSlider.setPaintLabels(true);

        // Create a label for sound slider
        JLabel soundLabel = new JLabel("Sound Volume", SwingConstants.CENTER);
        soundLabel.setFont(customFont);

        // Add a ChangeListener to update volume
        soundSlider.addChangeListener(e -> {
            int sliderValue = soundSlider.getValue();
            float volume = sliderValue / 100f;
            SoundManager.getInstance().setVolume(volume);
        });

        // Create the game speed slider
        speedSlider = new JSlider(1, 3, 2);
        speedSlider.setFont(customFont);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        // Create custom labels for the speedSlider
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(1, new JLabel("Hour"));
        labelTable.put(2, new JLabel("Day"));
        labelTable.put(3, new JLabel("Week"));
        for (Integer key : labelTable.keySet()) {
            labelTable.get(key).setFont(customFont);
        }
        speedSlider.setLabelTable(labelTable);

        // Create a label for game speed slider
        JLabel speedLabel = new JLabel("Game Speed", SwingConstants.CENTER);
        speedLabel.setFont(customFont);

        // Add spacing between components
        soundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        speedSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to the settings panel with gaps
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        settingsPanel.add(soundLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(soundSlider);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        settingsPanel.add(speedLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(speedSlider);

        // Create a wrapper panel with padding
        JPanel settingsWrapperPanel = new JPanel();
        settingsWrapperPanel.setLayout(new BorderLayout());
        settingsWrapperPanel.add(settingsPanel, BorderLayout.CENTER);
        settingsWrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        // Create a save button to apply settings
        JButton saveButton = createButton("Save Settings");
        saveButton.addActionListener(e -> {
            // Use controller to update settings
            controller.updateSettings(soundSlider.getValue(), speedSlider.getValue());
            JOptionPane.showMessageDialog(frame, "Settings saved successfully!", "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
        });

        // Create a back button
        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> switchToMainGamePanel());

        // Center the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);

        // Add all components to the panel
        panel.add(settingsWrapperPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Shows the main game panel with map and dashboard.
     * <p>
     * This method is called by the controller after initializing MapView and DashboardView
     * to display the actual game interface.
     * </p>
     *
     * @param mapView The initialized map view
     * @param dashboardView The initialized dashboard view
     */
    public void showGamePanel(MapView mapView, DashboardView dashboardView) {
        // Store the references
        this.mapView = mapView;
        this.dashboardView = dashboardView;
        dashboardView.setMapView(mapView);

        // Clear the frame
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Add map in the center
        frame.add(mapView.getScrollPane(), BorderLayout.CENTER);

        // Add dashboard at the bottom
        frame.add(dashboardView.getDashboardPanel(), BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();

        // Request focus on the map for keyboard navigation
        SwingUtilities.invokeLater(() -> {
            mapView.getScrollPane().requestFocusInWindow();
        });
    }

    /**
     * Switches to the Start Game screen.
     * <p>
     * Replaces the current panel with the panel for starting a new game.
     * </p>
     */
    private void switchToStartGamePanel() {
        // Switch the current panel to the Start Game screen
        frame.getContentPane().removeAll();
        frame.add(createStartGamePanel());
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Switches to the Load Game screen.
     * <p>
     * Replaces the current panel with the panel for loading a saved game.
     * </p>
     */
    private void switchToLoadGamePanel() {
        // Switch the current panel to the Load Game screen
        frame.getContentPane().removeAll();
        frame.add(createLoadGamePanel());
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Switches to the Settings screen.
     * <p>
     * Replaces the current panel with the panel for adjusting game settings.
     * </p>
     */
    private void switchToSettingsPanel() {
        // Switch the current panel to the Settings screen
        frame.getContentPane().removeAll();
        frame.add(createSettingsPanel());
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Exits the game with a confirmation popup.
     * <p>
     * Displays a dialog asking for confirmation before exiting the game.
     * </p>
     */
    private void exitGame() {
        // Create a custom confirmation panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add confirmation message
        JLabel messageLabel = new JLabel("Are you sure you want to exit?");
        messageLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        messageLabel.setForeground(new Color(255, 165, 0));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(messageLabel);

        // Show confirmation dialog
        Object[] options = {"Yes", "No"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                panel,
                "Exit Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1] // Default is "No"
        );

        // Handle user's choice
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else {
            switchToMainGamePanel();
        }
    }

    /**
     * Creates a consistently styled button with hover effects.
     * <p>
     * All buttons in the game interface use this styling for consistency.
     * </p>
     *
     * @param text The text to display on the button
     * @return The styled JButton
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(customFont);
        button.setBackground(new Color(255, 165, 0)); // Safari Orange
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(300, 70));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add a strong border outline
        Color defaultBorderColor = new Color(139, 69, 19); // Brown border
        button.setBorder(BorderFactory.createLineBorder(defaultBorderColor, 5));

        // Default and hover colors
        Color defaultColor = new Color(255, 165, 0); // Safari Orange
        Color hoverColor = new Color(255, 200, 100); // Lighter orange when hovered

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(defaultColor);
                button.setBorder(BorderFactory.createLineBorder(defaultBorderColor, 5));
            }
        });

        return button;
    }

    /**
     * Gets the Start Game button.
     *
     * @return The Start Game button
     */
    public JButton getStartButton() {
        return startButton;
    }

    /**
     * Gets the Load Game button.
     *
     * @return The Load Game button
     */
    public JButton getLoadButton() {
        return loadButton;
    }

    /**
     * Gets the Settings button.
     *
     * @return The Settings button
     */
    public JButton getSettingsButton() {
        return settingsButton;
    }

    /**
     * Gets the Exit button.
     *
     * @return The Exit button
     */
    public JButton getExitButton() {
        return exitButton;
    }

    /**
     * Gets the main application frame.
     *
     * @return The main JFrame
     */
    public JFrame getMainFrame() {
        return frame;
    }

    /**
     * Switches to the Main Game screen (main menu).
     * <p>
     * Replaces the current panel with the main menu panel.
     * This method is public to allow external components to
     * return to the main menu.
     * </p>
     */
    public void switchToMainGamePanel() {
        // Switch the current panel to the Main Game screen
        frame.getContentPane().removeAll();
        frame.add(createMainGamePanel());
        frame.revalidate();
        frame.repaint();
    }
}
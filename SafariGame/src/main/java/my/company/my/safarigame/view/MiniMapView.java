package my.company.my.safarigame.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.RadialGradientPaint;
import javax.swing.border.LineBorder;
import javax.swing.Timer;

/**
 * Displays a scaled-down overview of the safari game map with navigation capabilities.
 * <p>
 * The MiniMapView provides a compact representation of the entire game world, showing
 * the current viewport position and allowing players to quickly navigate to different
 * areas of the map. It automatically updates to reflect changes in the main map view
 * and provides visual feedback when interacted with.
 * </p>
 * <p>
 * Features include:
 * </p>
 * <ul>
 *   <li>Scaled representation of the full game map</li>
 *   <li>Viewport rectangle showing the current visible area</li>
 *   <li>Click-to-navigate functionality</li>
 *   <li>Visual feedback with hover and click effects</li>
 *   <li>Pulsating viewport indicator for better visibility</li>
 *   <li>Optional debug information display</li>
 * </ul>
 */
public class MiniMapView {
    /** Size of the mini map in pixels. */
    private final int MINI_MAP_SIZE = 160;
    
    /** Panel containing the mini map display. */
    private JPanel miniMapPanel;
    
    /** Reference to the main map panel for context. */
    private JPanel mapPanelReference;
    
    /** Current viewport's left position in map coordinates. */
    private int viewportX = 0;
    
    /** Current viewport's top position in map coordinates. */
    private int viewportY = 0;
    
    /** Current viewport's width in map coordinates. */
    private int viewportWidth = 0;
    
    /** Current viewport's height in map coordinates. */
    private int viewportHeight = 0;
    
    /** Total width of the game map in pixels. */
    private int totalMapWidth = 2400; // 50 * 48
    
    /** Total height of the game map in pixels. */
    private int totalMapHeight = 2400; // 50 * 48
    
    /** Color for the semi-transparent viewport rectangle. */
    private final Color VIEWPORT_COLOR = new Color(255, 255, 255, 100);
    
    /** Color for the viewport rectangle border. */
    private final Color VIEWPORT_BORDER = new Color(255, 255, 255);
    
    /** Color for the hover point indicator. */
    private final Color HOVER_POINT_COLOR = new Color(255, 215, 0, 180); // Gold color for hover point
    
    /** Timer for controlling the pulsating effect animation. */
    private Timer pulseTimer;
    
    /** Current intensity of the pulsating effect (0.0 to 1.0). */
    private float pulseIntensity = 0.0f;
    
    /** Flag indicating if pulse intensity is increasing or decreasing. */
    private boolean pulseIncreasing = true;
    
    /** Flag to enable/disable display of debug information. */
    private boolean showDebugInfo = false;
    
    /** 2D array of map cell labels from the main map view. */
    private JLabel[][] mapCells;
    
    /** Size of each cell in pixels. */
    private int cellSize;
    
    /** Number of rows in the grid. */
    private int gridRows;
    
    /** Number of columns in the grid. */
    private int gridCols;
    
    /** Current hover point location on the mini map. */
    private Point hoverPoint = null;
    
    /** Navigation callback for handling clicks on the mini map. */
    private NavigationCallback navigationCallback;

    /**
     * Interface for handling navigation events triggered by clicking on the mini map.
     * <p>
     * Classes implementing this interface can receive notifications when a player
     * clicks on the mini map to navigate to a specific location.
     * </p>
     */
    public interface NavigationCallback {
        /**
         * Called when the mini map is clicked to navigate to a specific point.
         *
         * @param mapX The x-coordinate in map coordinates to navigate to
         * @param mapY The y-coordinate in map coordinates to navigate to
         */
        void navigateToPoint(int mapX, int mapY);
    }
    
    /**
     * Constructs a new MiniMapView.
     * <p>
     * Initializes the mini map panel and starts the pulsating animation
     * for the viewport indicator.
     * </p>
     */
    public MiniMapView() {
        createMiniMapPanel();
        startPulseAnimation();
    }
    
    /**
     * Sets the map cells and cell size information from MapView.
     * <p>
     * This method establishes the connection with the main map view by
     * storing references to its grid cells, allowing the mini map to
     * accurately represent the current state of the map.
     * </p>
     *
     * @param cells The 2D array of JLabel cells from the main map
     * @param size The size of each cell in pixels
     * @param rows The number of rows in the grid
     * @param cols The number of columns in the grid
     */
    public void setMapCells(JLabel[][] cells, int size, int rows, int cols) {
        this.mapCells = cells;
        this.cellSize = size;
        this.gridRows = rows;
        this.gridCols = cols;
        
        // Force a redraw
        if (miniMapPanel != null) {
            miniMapPanel.repaint();
        }
    }

    /**
     * Updates the map cells reference without changing dimensions.
     * <p>
     * This method is used when the content of the map cells changes but
     * their structure remains the same, forcing a redraw of the mini map.
     * </p>
     *
     * @param cells The updated 2D array of JLabel cells from the main map
     */
    public void updateMapCells(JLabel[][] cells) {
        this.mapCells = cells;

        // Clear the cached map image to force redraw
        if (miniMapPanel != null) {
            try {
                java.lang.reflect.Field field = miniMapPanel.getClass().getDeclaredField("cachedMapImage");
                field.setAccessible(true);
                field.set(miniMapPanel, null);
                field.setAccessible(false);
            } catch (Exception e) {
                // If reflection fails, just force a repaint
            }

            miniMapPanel.repaint();
        }
    }
    
    /**
     * Starts the pulsating animation for the viewport rectangle.
     * <p>
     * This animation creates a pulsating effect for the viewport indicator,
     * making it more visually prominent and easier to locate.
     * </p>
     */
    private void startPulseAnimation() {
        pulseTimer = new Timer(50, e -> {
            // Update pulse intensity (0.0 to 1.0)
            if (pulseIncreasing) {
                pulseIntensity += 0.05f;
                if (pulseIntensity >= 1.0f) {
                    pulseIntensity = 1.0f;
                    pulseIncreasing = false;
                }
            } else {
                pulseIntensity -= 0.05f;
                if (pulseIntensity <= 0.0f) {
                    pulseIntensity = 0.0f;
                    pulseIncreasing = true;
                }
            }
            
            // Repaint minimap with new pulse value
            if (miniMapPanel != null) {
                miniMapPanel.repaint();
            }
        });
        pulseTimer.start();
    }
    
    /**
     * Gets the mini map panel for display.
     * <p>
     * Returns the JPanel containing the mini map, which can be added
     * to a container in the UI.
     * </p>
     *
     * @return The mini map JPanel
     */
    public JPanel getMiniMapPanel() {
        return miniMapPanel;
    }
    
    /**
     * Sets the navigation callback that will be called when mini map is clicked.
     * <p>
     * The callback allows the mini map to communicate with the main map view
     * when the player clicks to navigate to a specific location.
     * </p>
     *
     * @param callback The navigation callback to use
     */
    public void setNavigationCallback(NavigationCallback callback) {
        this.navigationCallback = callback;
    }
    
    /**
     * Gets the current navigation callback.
     *
     * @return The navigation callback
     */
    public NavigationCallback getNavigationCallback() {
        return navigationCallback;
    }
    
    /**
     * Sets reference to the actual map panel.
     * <p>
     * This reference is used as a fallback for rendering the mini map
     * when the cell information is not available.
     * </p>
     *
     * @param mapPanel The main map panel
     */
    public void setMapPanelReference(JPanel mapPanel) {
        this.mapPanelReference = mapPanel;
        miniMapPanel.repaint();
    }
    
    /**
     * Gets the map panel reference.
     *
     * @return The reference to the main map panel
     */
    public JPanel getMapPanelReference() {
        return mapPanelReference;
    }
    
    /**
     * Updates the viewport position and size in map coordinates.
     * <p>
     * This method is called by the main map view to update the mini map
     * when the visible portion of the map changes due to scrolling.
     * </p>
     *
     * @param x Viewport left position in map coordinates
     * @param y Viewport top position in map coordinates
     * @param width Viewport width in map coordinates
     * @param height Viewport height in map coordinates
     */
    public void updateViewport(int x, int y, int width, int height) {
        // Validate and store viewport values
        this.viewportX = Math.max(0, x);
        this.viewportY = Math.max(0, y);
        this.viewportWidth = Math.min(width, totalMapWidth);
        this.viewportHeight = Math.min(height, totalMapHeight);
        
        // Ensure the viewport doesn't extend beyond the map edges
        if (this.viewportX + this.viewportWidth > totalMapWidth) {
            this.viewportX = totalMapWidth - this.viewportWidth;
        }
        
        if (this.viewportY + this.viewportHeight > totalMapHeight) {
            this.viewportY = totalMapHeight - this.viewportHeight;
        }
        
        // Debug info
        if (showDebugInfo) {
            System.out.println("Viewport updated: " + viewportX + "," + viewportY + 
                              " size: " + viewportWidth + "x" + viewportHeight);
        }
        
        // Repaint the mini map
        if (miniMapPanel != null) {
            miniMapPanel.repaint();
        }
    }
    
    /**
     * Sets the total map dimensions.
     * <p>
     * This method is used to update the mini map when the size of the
     * main map changes.
     * </p>
     *
     * @param width Total width of the map in pixels
     * @param height Total height of the map in pixels
     */
    public void setMapDimensions(int width, int height) {
        this.totalMapWidth = width;
        this.totalMapHeight = height;
    }
    
    /**
     * Creates the mini map panel with click handling.
     * <p>
     * Sets up the visual appearance of the mini map and adds mouse listeners
     * for click, hover, and debug toggle functionality.
     * </p>
     */
    private void createMiniMapPanel() {
        miniMapPanel = new JPanel() {
            private BufferedImage cachedMapImage = null;
            private long lastRenderTime = 0;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Draw the actual map but at mini-map size
                drawScaledMap(g);
                
                // Draw viewport rectangle
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Use exact floating-point scaling to maintain precision
                double scaleX = (double)MINI_MAP_SIZE / totalMapWidth;
                double scaleY = (double)MINI_MAP_SIZE / totalMapHeight;
                
                // Calculate precise viewport rectangle dimensions
                int viewX = (int)Math.round(viewportX * scaleX);
                int viewY = (int)Math.round(viewportY * scaleY);
                int viewW = (int)Math.round(viewportWidth * scaleX);
                int viewH = (int)Math.round(viewportHeight * scaleY);
                
                // Ensure minimum size for visibility
                viewW = Math.max(4, viewW);
                viewH = Math.max(4, viewH);
                
                // Ensure the viewport rectangle stays within mini-map bounds
                if (viewX + viewW > MINI_MAP_SIZE) {
                    viewX = MINI_MAP_SIZE - viewW;
                }
                
                if (viewY + viewH > MINI_MAP_SIZE) {
                    viewY = MINI_MAP_SIZE - viewH;
                }
                
                // Pulsating alpha for viewport
                int alpha = 100 + (int)(155 * pulseIntensity);
                
                // Draw semi-transparent fill
                g2d.setColor(new Color(255, 255, 255, alpha / 3));
                g2d.fillRect(viewX, viewY, viewW, viewH);
                
                // Draw border with pulsating intensity
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.setColor(new Color(255, 255, 255, alpha));
                g2d.drawRect(viewX, viewY, viewW, viewH);
                
                // Draw hover point if present
                if (hoverPoint != null) {
                    int hoverSize = 6;
                    g2d.setColor(HOVER_POINT_COLOR);
                    g2d.fillOval(hoverPoint.x - hoverSize/2, hoverPoint.y - hoverSize/2, hoverSize, hoverSize);
                    g2d.setColor(Color.WHITE);
                    g2d.drawOval(hoverPoint.x - hoverSize/2, hoverPoint.y - hoverSize/2, hoverSize, hoverSize);
                }
                
                // Draw debug info if enabled
                if (showDebugInfo) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));
                    g2d.drawString("Map: " + totalMapWidth + "x" + totalMapHeight, 5, 10);
                    g2d.drawString("View: " + viewportX + "," + viewportY, 5, 20);
                    g2d.drawString("Size: " + viewportWidth + "x" + viewportHeight, 5, 30);
                    g2d.drawString("Mini: " + viewX + "," + viewY + "," + viewW + "x" + viewH, 5, 40);
                    g2d.drawString("Scale: " + String.format("%.3f", scaleX) + "," + String.format("%.3f", scaleY), 5, 50);
                }
            }
            
            /**
             * Draws a scaled-down version of the actual map.
             * <p>
             * Creates and caches a scaled version of the main map for efficiency,
             * updating it periodically to reflect changes in the game world.
             * </p>
             *
             * @param g The Graphics context to draw on
             */
            private void drawScaledMap(Graphics g) {
                // Create or use cached image of the map
                long currentTime = System.currentTimeMillis();
                
                // Regenerate cached image if needed (every 100ms instead of 200ms)
                if (cachedMapImage == null || currentTime - lastRenderTime > 100) {
                    // Create a new image for the scaled map
                    BufferedImage newImage = new BufferedImage(
                            MINI_MAP_SIZE, MINI_MAP_SIZE, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D imageG = newImage.createGraphics();
                    
                    try {
                        // Enable antialiasing
                        imageG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                            RenderingHints.VALUE_ANTIALIAS_ON);
                        imageG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        
                        // Draw a default green background
                        imageG.setColor(new Color(100, 200, 100));
                        imageG.fillRect(0, 0, MINI_MAP_SIZE, MINI_MAP_SIZE);
                        
                        // If we have the map cells, draw them directly
                        if (mapCells != null && cellSize > 0) {
                            // Calculate scaling factors
                            double scaleX = (double)MINI_MAP_SIZE / (gridCols * cellSize);
                            double scaleY = (double)MINI_MAP_SIZE / (gridRows * cellSize);
                            
                            // Draw each cell
                            for (int row = 0; row < gridRows; row++) {
                                for (int col = 0; col < gridCols; col++) {
                                    // Calculate position in mini-map
                                    int x = (int)(col * cellSize * scaleX);
                                    int y = (int)(row * cellSize * scaleY);
                                    int width = (int)Math.ceil(cellSize * scaleX);
                                    int height = (int)Math.ceil(cellSize * scaleY);
                                    
                                    // Get the icon for this cell
                                    JLabel cell = mapCells[row][col];
                                    if (cell != null && cell.getIcon() instanceof ImageIcon) {
                                        ImageIcon icon = (ImageIcon)cell.getIcon();
                                        Image img = icon.getImage();
                                        imageG.drawImage(img, x, y, width, height, null);
                                    }
                                }
                            }
                            
                            // Draw grid lines
                            imageG.setColor(new Color(0, 0, 0, 10));
                            imageG.setStroke(new BasicStroke(0.5f));
                            
                            // Draw only major grid lines
                            for (int i = 0; i <= gridCols; i += 10) {
                                int x = (int)(i * cellSize * scaleX);
                                imageG.drawLine(x, 0, x, MINI_MAP_SIZE);
                            }
                            
                            for (int i = 0; i <= gridRows; i += 10) {
                                int y = (int)(i * cellSize * scaleY);
                                imageG.drawLine(0, y, MINI_MAP_SIZE, y);
                            }
                        }
                        // Fallback to old method if we don't have map cells
                        else if (mapPanelReference != null) {
                            // Original drawing code (keep this as a fallback)
                            // [existing code for drawing from mapPanelReference]
                            // ...
                        }
                        
                    } catch (Exception e) {
                        System.err.println("Error rendering mini-map: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        imageG.dispose();
                    }
                    
                    cachedMapImage = newImage;
                    lastRenderTime = currentTime;
                }
                
                // Draw the cached mini map image
                if (cachedMapImage != null) {
                    g.drawImage(cachedMapImage, 0, 0, this);
                }
            }
        };
        
        // Set panel properties
        miniMapPanel.setPreferredSize(new Dimension(MINI_MAP_SIZE, MINI_MAP_SIZE));
        miniMapPanel.setBorder(new LineBorder(new Color(139, 69, 19), 3));
        
        // Add mouse listeners for navigation and hover effects
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMapClick(e);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMapClick(e);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                // Update hover point
                hoverPoint = e.getPoint();
                miniMapPanel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Clear hover point
                hoverPoint = null;
                miniMapPanel.repaint();
            }
        };
        
        miniMapPanel.addMouseListener(mouseHandler);
        miniMapPanel.addMouseMotionListener(mouseHandler);
        
        // Add ability to toggle debug info with right-click
        miniMapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showDebugInfo = !showDebugInfo;
                    miniMapPanel.repaint();
                }
            }
        });
        
        // Add tooltip
        miniMapPanel.setToolTipText("Click to navigate to location");
    }
    
    /**
     * Gets the current viewport's left position.
     *
     * @return The viewport's left position in map coordinates
     */
    public int getViewportX() {
        return viewportX;
    }
    
    /**
     * Gets the current viewport's top position.
     *
     * @return The viewport's top position in map coordinates
     */
    public int getViewportY() {
        return viewportY;
    }
    
    /**
     * Gets the current viewport's width.
     *
     * @return The viewport's width in map coordinates
     */
    public int getViewportWidth() {
        return viewportWidth;
    }
    
    /**
     * Gets the current viewport's height.
     *
     * @return The viewport's height in map coordinates
     */
    public int getViewportHeight() {
        return viewportHeight;
    }
    
    /**
     * Gets the total width of the map.
     *
     * @return The total map width in pixels
     */
    public int getTotalMapWidth() {
        return totalMapWidth;
    }
    
    /**
     * Gets the total height of the map.
     *
     * @return The total map height in pixels
     */
    public int getTotalMapHeight() {
        return totalMapHeight;
    }
    
    /**
     * Handles a click on the mini map.
     * <p>
     * Converts the click coordinates to main map coordinates and calls
     * the navigation callback to move the viewport to that location.
     * </p>
     *
     * @param e The mouse event containing click information
     */
    private void handleMapClick(MouseEvent e) {
        if (navigationCallback != null) {
            try {
                // Convert mini map coordinates to main map coordinates with precise scaling
                double scaleX = (double)totalMapWidth / MINI_MAP_SIZE;
                double scaleY = (double)totalMapHeight / MINI_MAP_SIZE;
                
                // Use exact math for precision
                int mapX = (int)Math.round(e.getX() * scaleX);
                int mapY = (int)Math.round(e.getY() * scaleY);
                
                // Ensure within bounds
                mapX = Math.max(0, Math.min(totalMapWidth - 1, mapX));
                mapY = Math.max(0, Math.min(totalMapHeight - 1, mapY));
                
                // Debug output
                if (showDebugInfo) {
                    System.out.println("Mini map click at: " + e.getX() + "," + e.getY() + 
                                      " -> map coords: " + mapX + "," + mapY);
                }
                
                // Highlight the clicked area temporarily
                highlightClickLocation(e.getX(), e.getY());
                
                // Notify the map view to navigate to this point
                navigationCallback.navigateToPoint(mapX, mapY);
                
            } catch (Exception ex) {
                System.err.println("Error handling mini map click: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Shows a temporary highlight effect at the clicked location.
     * <p>
     * Creates a radial gradient highlight that briefly appears to
     * provide visual feedback for the click.
     * </p>
     *
     * @param x The x-coordinate of the click on the mini map
     * @param y The y-coordinate of the click on the mini map
     */
    private void highlightClickLocation(int x, int y) {
        // Create a highlight effect
        JPanel highlight = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create a radial gradient for the highlight
                RadialGradientPaint paint = new RadialGradientPaint(
                    new Point(10, 10), 
                    10, 
                    new float[] {0.0f, 1.0f},
                    new Color[] {new Color(255, 255, 255, 200), new Color(255, 255, 255, 0)}
                );
                
                g2d.setPaint(paint);
                g2d.fillOval(0, 0, 20, 20);
            }
        };
        
        // Set up the highlight panel
        highlight.setOpaque(false);
        highlight.setBounds(x-10, y-10, 20, 20);
        
        // Add to the minimap panel
        miniMapPanel.setLayout(null); // Allow absolute positioning
        miniMapPanel.add(highlight);
        miniMapPanel.revalidate();
        miniMapPanel.repaint();
        
        // Remove after a short delay
        Timer timer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miniMapPanel.remove(highlight);
                miniMapPanel.revalidate();
                miniMapPanel.repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Forces the mini-map to update immediately.
     * <p>
     * Called whenever the main map changes significantly to ensure
     * the mini map accurately represents the current state of the game world.
     * </p>
     */
    public void forceUpdate() {
        // Clear the cached image to force a redraw on the next paint
       if (miniMapPanel != null) {
           // Use reflection to access the cached map image
           try {
               java.lang.reflect.Field field = miniMapPanel.getClass().getDeclaredField("cachedMapImage");
               field.setAccessible(true);
               field.set(miniMapPanel, null);
               field.setAccessible(false);
           } catch (Exception e) {
               System.err.println("Error clearing cached map image: " + e.getMessage());
           }

           // Request a repaint
           miniMapPanel.repaint();
       }
   }
}
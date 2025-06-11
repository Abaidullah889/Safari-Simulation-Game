package my.company.my.safarigame.view;

import my.company.my.safarigame.model.Animal;
import my.company.my.safarigame.model.Herbivore;
import my.company.my.safarigame.model.Carnivore;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

/**
 * Custom JLabel for displaying animals with health bars and visual effects.
 * <p>
 * This component extends JLabel to create a specialized display for animals in the safari game.
 * It visualizes the animal's state with health bars, thirst indicators, and various
 * animations for activities like drinking, eating, hunting, and death. The component
 * updates dynamically to reflect changes in the animal's state and provides visual
 * feedback for game events.
 * </p>
 */
public class AnimalLabel extends JLabel {

    /** The animal represented by this label. */
    private Animal animal;
    
    /** Size of the cell in pixels. */
    private int cellSize;
    
    /** Previous health value for detecting changes. */
    private int previousHealth = 0;
    
    /** Flag indicating if the maximum health indicator should be shown. */
    private boolean showMaxHealthIndicator = false;
    
    /** Timer for controlling the duration of the maximum health indicator. */
    private int maxHealthIndicatorTimer = 0;
    
    /** Flag indicating if the death animation should be shown. */
    private boolean showDeathAnimation = false;
    
    /** Timer for controlling the duration of the death animation. */
    private int deathAnimationTimer = 0;
    
    /** Opacity value for the death animation fade effect. */
    private float deathAnimationOpacity = 1.0f;

    /** Flag indicating if the animal is currently drinking. */
    private boolean isDrinking = false;
    
    /** Timer for controlling the duration of the drinking animation. */
    private int drinkingAnimationTimer = 0;

    /** Label component for displaying the animal's thirst level. */
    private JLabel thirstIndicator;

    /** Background color for health bars. */
    private final Color HEALTH_BAR_BG = new Color(60, 60, 60, 200);
    
    /** Color for high health levels. */
    private final Color HEALTH_GREEN = new Color(0, 200, 0, 200);
    
    /** Color for medium health levels. */
    private final Color HEALTH_YELLOW = new Color(200, 200, 0, 200);
    
    /** Color for low health levels. */
    private final Color HEALTH_RED = new Color(200, 0, 0, 200);
    
    /** Color for health increases. */
    private final Color HEALTH_INCREASE = new Color(255, 0, 0, 200);
    
    /** Color for maximum health indicator. */
    private final Color MAX_HEALTH = new Color(255, 180, 0, 200);
    
    /** Color for attack animations. */
    private final Color ATTACK_COLOR = new Color(255, 0, 0, 150);
    
    /** Color for injured animations. */
    private final Color INJURED_COLOR = new Color(255, 0, 0, 100);

    /**
     * Constructs a new AnimalLabel for the specified animal.
     * <p>
     * Initializes the label with the animal reference and sets up the thirst indicator
     * and other visual components.
     * </p>
     *
     * @param animal The animal to be displayed
     * @param cellSize The size of the cell in pixels
     */
    public AnimalLabel(Animal animal, int cellSize) {
        this.animal = animal;
        this.cellSize = cellSize;

        setLayout(new OverlayLayout(this));

        thirstIndicator = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int thirstLevel = animal.getThirst();
                int maxThirst = 100;

                // Calculate color based on thirst level
                Color thirstColor;
                if (thirstLevel < 30) {
                    thirstColor = new Color(0, 0, 255, 100); // Light blue
                } else if (thirstLevel < 70) {
                    thirstColor = new Color(0, 0, 255, 150); // Medium blue
                } else {
                    thirstColor = new Color(0, 0, 255, 230); // Dark blue
                }

                // Draw thirst indicator with gradient
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, getHeight(), Color.WHITE.brighter(),
                        0, 0, thirstColor
                );
                g2d.setPaint(gradient);

                // Calculate the height of the thirst bar based on current thirst level
                int indicatorHeight = (int) ((thirstLevel / (double) maxThirst) * getHeight());
                g2d.fillRect(0, getHeight() - indicatorHeight, getWidth(), indicatorHeight);

                // Add border
                g2d.setColor(Color.BLUE.darker());
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };

        // Customize thirst indicator appearance
        thirstIndicator.setOpaque(false);
        thirstIndicator.setPreferredSize(new Dimension(cellSize, 10)); // Thin bar
        thirstIndicator.setMaximumSize(new Dimension(cellSize, 10));
        thirstIndicator.setMinimumSize(new Dimension(cellSize, 10));
        thirstIndicator.setAlignmentX(CENTER_ALIGNMENT);

        // Add components
        add(thirstIndicator, BorderLayout.NORTH);
        add(new JLabel(new ImageIcon())); // Placeholder for animal icon
    }

    /**
     * Gets the animal associated with this label.
     *
     * @return The animal represented by this label
     */
    public Animal getAnimal() {
        return animal;
    }

    /**
     * Custom painting for the animal label.
     * <p>
     * Paints the animal's health bar, thirst indicator, and various animations
     * based on the animal's current state.
     * </p>
     *
     * @param g The Graphics context to paint on
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (animal == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Check if animal is in drinking animation (either actively drinking or shortly after)
        if (animal.isInDrinkingAnimation() || isDrinking) {
            // Draw water droplets animation
            drawDrinkingAnimation(g2d);
        }
        
        thirstIndicator.repaint();

        // Paint the health bar if this is an animal
        if (animal != null) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // If the animal is dead, show death animation
            if (animal instanceof Herbivore && ((Herbivore) animal).isDead()) {
                paintDeadAnimal(g2d);
                return;
            }

            // Draw health bar background
            int barWidth = cellSize - 10;
            int barHeight = 5;
            int barX = 5;
            int barY = 2;

            // Background is always dark gray
            g2d.setColor(HEALTH_BAR_BG);
            g2d.fillRect(barX, barY, barWidth, barHeight);

            // Get current health percentage
            int healthPercentage = animal.getHealthPercentage();
            int fillWidth = (barWidth * healthPercentage) / 100;

            // Check if health increased since last paint
            int currentHealth = animal.getHealth();
            boolean healthIncreased = currentHealth > previousHealth;
            boolean healthDecreased = currentHealth < previousHealth;

            // Always log health changes to console
            if (currentHealth != previousHealth) {
                System.out.println("Animal health changed: " + previousHealth + " -> " + currentHealth
                        + " (" + (currentHealth > previousHealth ? "+" : "")
                        + (currentHealth - previousHealth) + ")");
                previousHealth = currentHealth; // Update previous health
            }

            // Check if animal is at max health
            boolean isMaxHealth = false;
            if (animal instanceof Herbivore) {
                Herbivore herbivore = (Herbivore) animal;
                isMaxHealth = (herbivore.getHealth() >= herbivore.getMaxHealth());

                // If animal tries to eat while at max health, show max health indicator
                if (isMaxHealth && herbivore.isEating()) {
                    showMaxHealthIndicator = true;
                    maxHealthIndicatorTimer = 10;
                }
            } else if (animal instanceof Carnivore) {
                Carnivore carnivore = (Carnivore) animal;
                isMaxHealth = (carnivore.getHealth() >= carnivore.getMaxHealth());

                // If animal is hunting while at max health, show max health indicator
                if (isMaxHealth && carnivore.isHunting()) {
                    showMaxHealthIndicator = true;
                    maxHealthIndicatorTimer = 10;
                }
            }

            // Fill health bar with appropriate color
            if (isMaxHealth && showMaxHealthIndicator) {
                // Use a special pulsing color for max health
                int pulse = (int) (128 + 127 * Math.sin(System.currentTimeMillis() / 100.0));
                g2d.setColor(new Color(255, pulse, 0, 200)); // Pulsing orange-red for max health
            } else if (healthIncreased) {
                // Use RED for health increase as requested
                g2d.setColor(HEALTH_INCREASE); // Bright Red
            } else if (healthDecreased) {
                // Use darker red for health decrease
                g2d.setColor(HEALTH_RED.darker()); // Dark Red
            } else {
                // Normal health bar colors based on health percentage
                if (healthPercentage > 70) {
                    g2d.setColor(HEALTH_GREEN); // Green
                } else if (healthPercentage > 30) {
                    g2d.setColor(HEALTH_YELLOW); // Yellow
                } else {
                    g2d.setColor(HEALTH_RED); // Red
                }
            }

            // Draw the health bar fill
            g2d.fillRect(barX, barY, fillWidth, barHeight);

            // Add a border to the health bar
            g2d.setColor(new Color(200, 200, 200, 200));
            g2d.drawRect(barX, barY, barWidth, barHeight);

            // If it's a herbivore, handle special visualizations
            if (animal instanceof Herbivore) {
                Herbivore herbivore = (Herbivore) animal;

                // Check if herbivore is injured (recently attacked)
                if (herbivore.isInjured()) {
                    // Show blood splatter effect
                    paintInjuryEffect(g2d);
                }

                if (herbivore.isEating()) {
                    // Show amount of health gained or max health indicator
                    if (isMaxHealth) {
                        // Show MAX indicator instead of health gain
                        g2d.setColor(new Color(255, 215, 0, 200)); // Gold color
                        g2d.setFont(new Font("Arial", Font.BOLD, 12));
                        g2d.drawString("MAX", barX + barWidth + 2, barY + barHeight + 10);
                    } else {
                        // Show regular health gain
                        g2d.setColor(new Color(255, 0, 0, 200)); // Red text
                        g2d.setFont(new Font("Arial", Font.BOLD, 12));
                        int healthGain = herbivore.getHealthIncreaseAmount();
                        g2d.drawString("+" + healthGain, barX + barWidth + 2, barY + barHeight + 10);
                    }

                    // Only show green dot for plant eating, not grass
                    if (herbivore.isEatingPlant()) {
                        // Show green "eating" indicator
                        g2d.setColor(new Color(0, 255, 0, 150));
                        int animSize = 10;
                        g2d.fillOval(getWidth() / 2 - animSize / 2, getHeight() / 2 - animSize / 2, animSize, animSize);
                    }
                }
            } // If it's a carnivore, handle hunting visualization
            else if (animal instanceof Carnivore) {
                Carnivore carnivore = (Carnivore) animal;
                if (carnivore.isHunting()) {
                    // Show hunting animation - red fangs
                    paintHuntingEffect(g2d);

                    // Show amount of health gained or max health indicator
                    if (isMaxHealth) {
                        // Show MAX indicator
                        g2d.setColor(new Color(255, 215, 0, 200)); // Gold color
                        g2d.setFont(new Font("Arial", Font.BOLD, 12));
                        g2d.drawString("MAX", barX + barWidth + 2, barY + barHeight + 10);
                    } else {
                        // Show health gain
                        g2d.setColor(new Color(255, 0, 0, 200)); // Red text
                        g2d.setFont(new Font("Arial", Font.BOLD, 12));
                        int healthGain = carnivore.getHealthIncreaseAmount();
                        g2d.drawString("+" + healthGain, barX + barWidth + 2, barY + barHeight + 10);
                    }
                }
            }

            // Update max health indicator timer
            if (showMaxHealthIndicator) {
                maxHealthIndicatorTimer--;
                if (maxHealthIndicatorTimer <= 0) {
                    showMaxHealthIndicator = false;
                }
            }
        }
    }

    /**
     * Paints visualization for a dead animal.
     * <p>
     * Draws a fading X and "DEAD" text over the animal when it dies.
     * </p>
     *
     * @param g2d The Graphics2D context to paint on
     */
    private void paintDeadAnimal(Graphics2D g2d) {
        // Create fading effect for dead animal
        if (deathAnimationTimer > 0) {
            // Calculate opacity based on timer
            deathAnimationOpacity = (float) deathAnimationTimer / 50.0f;

            // Set composite for transparency
            AlphaComposite alphaComposite = AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, deathAnimationOpacity);
            g2d.setComposite(alphaComposite);

            // Draw skull and crossbones or X over the animal
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3));

            // Draw X
            g2d.drawLine(5, 5, cellSize - 5, cellSize - 5);
            g2d.drawLine(cellSize - 5, 5, 5, cellSize - 5);

            // Draw "DEAD" text
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("DEAD", cellSize / 2 - 15, cellSize / 2 + 5);

            // Update timer
            deathAnimationTimer--;

            // Request repaint to continue animation
            if (deathAnimationTimer > 0) {
                repaint(50); // Repaint every 50ms
            }
        }
    }

    /**
     * Paints hunting effect for carnivores.
     * <p>
     * Draws red fangs and blood drops to indicate a carnivore is hunting.
     * </p>
     *
     * @param g2d The Graphics2D context to paint on
     */
    private void paintHuntingEffect(Graphics2D g2d) {
        // Draw red fangs or attack indicator
        g2d.setColor(ATTACK_COLOR);

        // Draw fangs
        int fangWidth = 4;
        int fangHeight = 10;
        int centerX = cellSize / 2;
        int centerY = cellSize / 2;

        // Left fang
        g2d.fillRect(centerX - 8, centerY - 5, fangWidth, fangHeight);
        // Right fang
        g2d.fillRect(centerX + 4, centerY - 5, fangWidth, fangHeight);

        // Draw blood drops
        g2d.setColor(new Color(255, 0, 0, 200));
        g2d.fillOval(centerX - 6, centerY + 8, 4, 4); // Left drop
        g2d.fillOval(centerX + 6, centerY + 10, 3, 3); // Right drop
    }

    /**
     * Paints injury effect for herbivores.
     * <p>
     * Draws blood splatters and a wound to indicate a herbivore is injured.
     * </p>
     *
     * @param g2d The Graphics2D context to paint on
     */
    private void paintInjuryEffect(Graphics2D g2d) {
        // Draw blood splatter
        g2d.setColor(INJURED_COLOR);

        // Random blood splatters
        int centerX = cellSize / 2;
        int centerY = cellSize / 2;

        // Create several small blood droplets
        for (int i = 0; i < 5; i++) {
            int offsetX = (int) (Math.random() * cellSize - cellSize / 2);
            int offsetY = (int) (Math.random() * cellSize - cellSize / 2);
            int size = (int) (3 + Math.random() * 4);

            g2d.fillOval(centerX + offsetX, centerY + offsetY, size, size);
        }

        // Draw a small wound
        g2d.setColor(new Color(180, 0, 0, 150));
        g2d.fillOval(centerX - 2, centerY - 2, 8, 5);
    }

    /**
     * Sets a new animal for this label.
     * <p>
     * Updates the label to represent a different animal, initializing
     * appropriate animations based on the animal's state.
     * </p>
     *
     * @param animal The new animal to represent
     */
    public void setAnimal(Animal animal) {
        this.animal = animal;
        if (animal != null) {
            this.previousHealth = animal.getHealth(); // Update previous health

            // Check for herbivore death
            if (animal instanceof Herbivore && ((Herbivore) animal).isDead()) {
                showDeathAnimation = true;
                deathAnimationTimer = 50; // Longer death animation
            }
        }
        repaint();
    }

    /**
     * Forces the label to show the max health indicator.
     * <p>
     * This method is called when an animal reaches maximum health,
     * displaying a visual indicator for a short duration.
     * </p>
     */
    public void showMaxHealthReached() {
        showMaxHealthIndicator = true;
        maxHealthIndicatorTimer = 20; // Show for longer
        repaint();
    }

    /**
     * Triggers the death animation.
     * <p>
     * Initiates the fading death animation when an animal dies.
     * </p>
     */
    public void showDeathAnimation() {
        showDeathAnimation = true;
        deathAnimationTimer = 50; // Show for 50 cycles
        repaint();
    }

    /**
     * Shows attack animation on this animal.
     * <p>
     * Displays visual effects when the animal is being attacked.
     * </p>
     */
    public void showAttackAnimation() {
        // For herbivores, we'll show the injured state
        if (animal instanceof Herbivore) {
            Herbivore herbivore = (Herbivore) animal;
            // The isInjured flag should already be set by the takeDamage method
            repaint();
        }
    }

    /**
     * Checks if this animal is currently showing a death animation.
     *
     * @return true if death animation is active, false otherwise
     */
    public boolean isShowingDeathAnimation() {
        return showDeathAnimation && deathAnimationTimer > 0;
    }

    /**
     * Shows drinking animation for the animal.
     * <p>
     * Initiates a water droplet animation to indicate the animal is drinking.
     * </p>
     */
    public void showDrinkingAnimation() {
        isDrinking = true;
        drinkingAnimationTimer = 20; // Increased duration of drinking animation for better visibility

        // Repaint this label immediately
        repaint();
    }

    /**
     * Draws the drinking animation.
     * <p>
     * Creates water droplets and ripple effects to visualize the animal drinking.
     * </p>
     *
     * @param g2d The Graphics2D context to paint on
     */
    private void drawDrinkingAnimation(Graphics2D g2d) {
        // Save original composite
        Composite originalComposite = g2d.getComposite();

        // Draw water droplets
        g2d.setColor(new Color(0, 150, 255));

        // Calculate number of droplets based on animation progress
        int numDroplets = Math.min(5, Math.max(1, drinkingAnimationTimer / 4));

        // Draw water ripples
        int centerX = cellSize / 2;
        int bottom = cellSize - 5;

        // Draw ripple effect around animal's mouth
        for (int i = 0; i < 3; i++) {
            // Ripple transparency decreases with size
            float alpha = 0.8f - (i * 0.2f);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            int size = 5 + (i * 4);
            g2d.fillOval(centerX - size / 2, bottom - size / 2, size, size);
        }

        // Draw droplets falling
        for (int i = 0; i < numDroplets; i++) {
            // Randomize droplet position
            int dropX = centerX - 10 + (i * 5);
            int dropY = bottom - (drinkingAnimationTimer % 10) - (i * 3);

            // Droplet size
            int dropSize = 3 + (i % 2);

            // Draw droplet
            g2d.fillOval(dropX, dropY, dropSize, dropSize);
        }

        // Restore original composite
        g2d.setComposite(originalComposite);
    }

    /**
     * Updates the thirst indicator display.
     * <p>
     * Refreshes the thirst indicator and applies pulsing effects for
     * animals with high thirst levels.
     * </p>
     */
    public void updateThirstIndicator() {
        if (thirstIndicator != null) {
            // Update the bar's appearance based on animal's current thirst
            thirstIndicator.repaint();

            // If the animal is very thirsty, make the indicator pulse
            if (animal != null && animal.getThirst() > 70) {
                // Apply pulsing effect to make it more noticeable
                Timer pulseTimer = new Timer(500, e -> thirstIndicator.repaint());
                pulseTimer.setRepeats(false);
                pulseTimer.start();
            }
        }
    }
}
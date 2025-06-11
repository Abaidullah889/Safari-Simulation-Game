package my.company.my.safarigame.model;

/**
 * Represents a herbivorous animal in the safari game.
 * <p>
 * Herbivores are specialized animals that graze on vegetation for food and health benefits.
 * They can be hunted by carnivores and have unique grazing mechanics with different health
 * gains based on the type of vegetation consumed. Herbivores can track injury status
 * from predator attacks and will die if their health reaches zero.
 * </p>
 */
public class Herbivore extends Animal {

    /** Flag to track if the herbivore is currently in an eating animation. */
    protected boolean isEating = false;
    
    /** Flag to track if the herbivore is eating a plant/bush/shrub (not just grass). */
    protected boolean isEatingPlant = false;
    
    /** Counter for the duration of the eating animation. */
    protected int eatingDuration = 0;
    
    /** Amount of health to increase when eating. */
    protected int healthIncreaseAmount = 0;
    
    /** Flag to track if the herbivore is dead. */
    public boolean isDead = false;
    
    /** Flag to track if the herbivore has been recently attacked. */
    protected boolean isInjured = false;
    
    /** Timer for showing the injury animation. */
    protected int injuryTimer = 0;

    /**
     * Constructs a new Herbivore at the specified position with the given type.
     *
     * @param position The initial position of the herbivore
     * @param type The type identifier for the herbivore (e.g., "cow", "deer")
     */
    public Herbivore(Coordinate position, String type) {
        super(position, type);
        this.price = 50.0; // You could make price depend on species if you want
    }

    /**
     * Processes damage from a predator attack.
     * <p>
     * Reduces the herbivore's health by the specified amount and checks if the
     * herbivore has died as a result. Also sets the injured status for visual feedback.
     * </p>
     *
     * @param damage Amount of damage to take
     * @return true if the damage killed this herbivore, false otherwise
     */
    public boolean takeDamage(int damage) {
        // Record health before change
        int oldHealth = this.health;

        // Apply the damage
        health -= damage;

        // Check if we're dead
        if (health <= 0) {
            health = 0;
            isDead = true;
            System.out.println(getDescription() + " has died from predator attack!");
        } else {
            // Show injured status
            isInjured = true;
            injuryTimer = 15; // Show injury animation for 15 cycles
        }

        // Log the damage
        System.out.println("DAMAGE: " + getDescription() + " took " + damage
                + " damage. Health: " + oldHealth + " -> " + health);

        return isDead;
    }

    /**
     * Checks if this herbivore is dead and should be removed.
     *
     * @return true if the herbivore is dead, false otherwise
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Checks if this herbivore is injured (recently attacked).
     *
     * @return true if the herbivore is injured, false otherwise
     */
    public boolean isInjured() {
        return isInjured;
    }

    /**
     * Handles the herbivore drinking from a water area.
     * <p>
     * Extends the base Animal class method to provide herbivore-specific behavior
     * when drinking water. Herbivores get more health benefit from water compared
     * to carnivores.
     * </p>
     *
     * @param waterArea The water area the herbivore is drinking from
     */
    @Override
    public void drinkFromWaterArea(WaterArea waterArea) {
        super.drinkFromWaterArea(waterArea);

        // Add any herbivore-specific drinking behavior here
        // For example, herbivores might recover more health from drinking
        if (waterArea != null && !isDead) {
            // Slightly increase health
            health = Math.min(maxHealth, health + 5);
        }
    }

    /**
     * Updates the herbivore's lifecycle status.
     * <p>
     * Extends the base Animal updateLifecycle method with herbivore-specific behavior,
     * particularly handling the injury timer during drinking.
     * </p>
     */
    @Override
    public void updateLifecycle() {
        // Don't update if dead
        if (isDead) {
            return;
        }

        super.updateLifecycle();

        // Any additional herbivore-specific update logic
        if (isDrinking) {
            // Update the injury timer while drinking
            if (isInjured && injuryTimer > 0) {
                injuryTimer--;
                if (injuryTimer <= 0) {
                    isInjured = false;
                }
            }
        }
    }

    /**
     * Makes the herbivore graze on grass.
     * <p>
     * Initiates the eating animation and provides a small health benefit.
     * This method is used for grazing on regular grass, which provides
     * minimal health benefits compared to eating specific plants.
     * </p>
     */
    public void graze() {
        // Don't eat if dead
        if (isDead) {
            return;
        }

        System.out.println(getDescription() + " is grazing on grass.");
        eat();
        // Start eating animation
        isEating = true;
        isEatingPlant = false; // Just grass
        eatingDuration = 5; // Animation will last for 5 update cycles

        // Determine health increase based on what we're eating
        healthIncreaseAmount = 0; // Default small health increase for grass

        // Check if already at max health
        if (health >= maxHealth) {
            System.out.println("MAX HEALTH: " + getDescription() + " already at maximum health (" + maxHealth + ")");
            return; // Already at max health, no need to increase
        }

        // Increase health when eating (but not beyond max health)
        int oldHealth = health;
        health = Math.min(maxHealth, health + healthIncreaseAmount);

        // Log the health increase
        System.out.println("HEALTH INCREASE: " + getDescription() + " health changed from "
                + oldHealth + " to " + health + " (+" + healthIncreaseAmount + ")");

        if (health >= maxHealth) {
            System.out.println("MAX HEALTH REACHED: " + getDescription() + " is now at maximum health!");
        }
    }

    /**
     * Makes the herbivore eat a specific plant type.
     * <p>
     * Initiates the eating animation and provides health benefits based on the
     * type of plant being consumed. Different plant types provide different
     * amounts of health regeneration.
     * </p>
     *
     * @param plantType The character code representing the plant type ('p' for plant, 
     *                  'b' for bush, 'h' for shrub)
     */
    public void grazePlant(char plantType) {
        // Don't eat if dead
        if (isDead) {
            return;
        }

        String plantName = "";
        switch (plantType) {
            case 'p':
                plantName = "plant";
                break;
            case 'b':
                plantName = "bush";
                break;
            case 'h':
                plantName = "shrub";
                break;
            default:
                plantName = "vegetation";
        }

        System.out.println(getDescription() + " is eating a " + plantName + ".");
        eat();

        // Start eating animation
        isEating = true;
        isEatingPlant = true; // This is a plant, not just grass
        eatingDuration = 8; // Animation will last longer for plants

        // Determine health increase based on plant type
        switch (plantType) {
            case 'p': // Plant
                healthIncreaseAmount = 15;
                break;
            case 'b': // Bush
                healthIncreaseAmount = 20;
                break;
            case 'h': // Shrub
                healthIncreaseAmount = 25;
                break;
            default:
//                healthIncreaseAmount = 10;
        }

        // Check if already at max health
        if (health >= maxHealth) {
            System.out.println("MAX HEALTH: " + getDescription() + " already at maximum health (" + maxHealth + ")");
            return; // Already at max health, no need to increase
        }

        // Increase health when eating (but not beyond max health)
        int oldHealth = health;
        health = Math.min(maxHealth, health + healthIncreaseAmount);

        // Log the health increase
        System.out.println("HEALTH INCREASE: " + getDescription() + " health changed from "
                + oldHealth + " to " + health + " (+" + (health - oldHealth) + ")");

        if (health >= maxHealth) {
            System.out.println("MAX HEALTH REACHED: " + getDescription() + " is now at maximum health!");
        }
    }

    /**
     * Checks if the herbivore can eat at its current position on the grid.
     * <p>
     * Examines the cell type at the herbivore's current position to determine
     * if it contains edible vegetation (plant, bush, or shrub). If edible vegetation
     * is found, the herbivore will automatically start grazing on it.
     * </p>
     *
     * @param grid The game grid to check for edible vegetation
     * @return true if the herbivore was able to eat vegetation, false otherwise
     */
    public boolean canEatAt(Grid grid) {
        // Don't eat if dead
        if (isDead) {
            return false;
        }

        if (position != null) {
            Cell cell = grid.getCell(position.x, position.y);
            if (cell != null) {
                char cellType = cell.getCellType();
                // Check if the cell contains plants or bushes or shrubs
                if (cellType == 'p' || cellType == 'b' || cellType == 'h') {
                    // This is plant vegetation - call specialized method
                    grazePlant(cellType);
                    return true;
                }
                return false; // Not a plant/bush/shrub
            }
        }
        return false;
    }

    /**
     * Creates a new herbivore of the same type at the same position (reproduction).
     * <p>
     * This method is used when animals reproduce to create offspring.
     * A dead herbivore cannot reproduce.
     * </p>
     *
     * @return A new Herbivore instance, or null if this herbivore is dead
     */
    @Override
    public Animal reproduce() {
        // Don't reproduce if dead
        if (isDead) {
            return null;
        }
        return new Herbivore(position, type);
    }

    /**
     * Gets a description of the herbivore.
     *
     * @return The type identifier of the herbivore
     */
    @Override
    public String getDescription() {
        return type;
    }

    /**
     * Checks if the herbivore is currently eating.
     *
     * @return true if the herbivore is in eating mode, false otherwise
     */
    public boolean isEating() {
        return isEating;
    }

    /**
     * Checks if the herbivore is eating a plant rather than grass.
     *
     * @return true if the herbivore is eating a plant/bush/shrub, false if eating grass or not eating
     */
    public boolean isEatingPlant() {
        return isEatingPlant;
    }

    /**
     * Gets the amount of health increase from the last eating action.
     *
     * @return The health increase amount
     */
    public int getHealthIncreaseAmount() {
        return healthIncreaseAmount;
    }

    /**
     * Checks if the herbivore is at maximum health.
     *
     * @return true if the herbivore is at maximum health, false otherwise
     */
    public boolean isAtMaxHealth() {
        return health >= maxHealth;
    }

    /**
     * Decreases the herbivore's health by the specified amount.
     * <p>
     * Useful for testing or simulating damage from environmental factors.
     * If health drops to zero or below, the herbivore will die.
     * </p>
     *
     * @param amount The amount to decrease health by
     */
    public void decreaseHealth(int amount) {
        // Don't decrease if dead
        if (isDead) {
            return;
        }

        int oldHealth = this.health;
        this.health = Math.max(0, this.health - amount);

        // Check if health dropped to zero
        if (this.health <= 0) {
            this.health = 0;
            isDead = true;
            System.out.println(getDescription() + " has died!");
        }

        System.out.println("HEALTH DECREASED: " + getDescription() + " health changed from "
                + oldHealth + " to " + this.health
                + " (-" + amount + ")");
    }

    /**
     * Gets the current health of the herbivore.
     *
     * @return The herbivore's health value
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * Sets the health of the herbivore with proper bounds checking.
     * <p>
     * Ensures health stays within the valid range (0 to maxHealth) and
     * logs significant health changes. If health is set to zero or below,
     * the herbivore will die.
     * </p>
     *
     * @param health The new health value
     */
    public void setHealth(int health) {
        int oldHealth = this.health;
        this.health = Math.min(maxHealth, Math.max(0, health));

        // Check if health dropped to zero
        if (this.health <= 0) {
            this.health = 0;
            isDead = true;
            System.out.println(getDescription() + " has died!");
        }

        // Log significant health changes
        if (this.health != oldHealth) {
            System.out.println("HEALTH CHANGE: " + getDescription() + " health changed from "
                    + oldHealth + " to " + this.health
                    + " (" + (this.health - oldHealth > 0 ? "+" : "")
                    + (this.health - oldHealth) + ")");
        }
    }
}
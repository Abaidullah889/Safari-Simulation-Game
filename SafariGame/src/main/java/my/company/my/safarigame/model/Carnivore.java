package my.company.my.safarigame.model;

/**
 * Represents a carnivorous animal in the safari game.
 * <p>
 * Carnivores are specialized animals that can hunt herbivores for food and health benefits.
 * They have attack capabilities and can stalk and kill other animals. Carnivores have
 * unique hunting mechanics, attack power, and health regeneration from successful hunts.
 * </p>
 */
public class Carnivore extends Animal {

    /** Flag indicating if the carnivore is currently hunting. */
    protected boolean isHunting = false;
    
    /** Duration of the current hunting activity. */
    protected int huntingDuration = 0;
    
    /** Damage dealt when attacking herbivores. */
    protected int attackPower = 20;
    
    /** Health gained from a successful hunt. */
    protected int healthIncreaseAmount = 0;
    
    /** Flag indicating if the carnivore is at maximum health. */
    protected boolean atMaxHealth = false;

    /**
     * Constructs a new Carnivore at the specified position with the given type.
     *
     * @param position The initial position of the carnivore
     * @param type The type identifier for the carnivore
     */
    public Carnivore(Coordinate position, String type) {
        super(position, type);
        this.price = 150.0;
        this.atMaxHealth = (health >= maxHealth);
    }

    /**
     * Hunt nearby herbivores.
     * <p>
     * Initiates a hunting attack on the target herbivore, dealing damage and potentially
     * killing it. The carnivore gains health proportional to the damage dealt, with a
     * bonus for successful kills.
     * </p>
     *
     * @param target The herbivore to hunt
     * @return true if the hunt resulted in a kill, false otherwise
     */
    public boolean hunt(Herbivore target) {
        System.out.println(getDescription() + " is hunting a " + target.getDescription() + ".");
        eat();

        // Start hunting animation
        isHunting = true;
        huntingDuration = 5;

        // Record health before change
        int oldHealth = this.health;

        // Attack the herbivore and reduce its health
        int damage = attackPower;
        int targetOldHealth = target.getHealth();
        boolean killed = target.takeDamage(damage);

        // Calculate how much health we'll gain (proportional to damage dealt)
        // Only gain 50% of damage dealt as health
        healthIncreaseAmount = Math.min(damage / 2, 15);

        // If killed, get more health
        if (killed) {
            // Bonus health for killing prey
            healthIncreaseAmount += 10;
            System.out.println(getDescription() + " killed a " + target.getDescription() + " and gained extra health!");
        }

        // Update max health status
        atMaxHealth = (health >= maxHealth);

        // Don't increase health if already at max
        if (atMaxHealth) {
            System.out.println("MAX HEALTH: " + getDescription() + " already at maximum health (" + maxHealth + ")");
        } else {
            // Increase carnivore health from successful hunt
            health = Math.min(maxHealth, health + healthIncreaseAmount);

            System.out.println("HEALTH INCREASE: " + getDescription() + " health changed from "
                    + oldHealth + " to " + health + " (+" + healthIncreaseAmount + ")");

            // Check if we reached max health with this increase
            atMaxHealth = (health >= maxHealth);
            if (atMaxHealth) {
                System.out.println("MAX HEALTH REACHED: " + getDescription() + " is now at maximum health!");
            }
        }

        // Log the attack result
        if (killed) {
            System.out.println("KILL: " + getDescription() + " killed " + target.getDescription()
                    + " (damage: " + damage + ")");
        } else {
            System.out.println("ATTACK: " + getDescription() + " attacked " + target.getDescription()
                    + " (damage: " + damage + ", herbivore health: " + targetOldHealth + " -> " + target.getHealth() + ")");
        }

        return killed;
    }

    /**
     * Searches for a herbivore nearby that can be hunted.
     * <p>
     * Scans the surrounding cells in the game grid to find a viable herbivore target
     * for hunting. Only considers live herbivores within the immediate vicinity.
     * </p>
     *
     * @param grid The game grid representing the world layout
     * @param map The safari map containing all animals
     * @return The first herbivore found within hunting range, or null if none is found
     */
    public Herbivore findNearbyHerbivore(Grid grid, SafariMap map) {
        if (position == null) {
            return null;
        }

        // Define search radius (1 cell in all directions)
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        // Check surrounding cells
        for (int i = 0; i < dx.length; i++) {
            int checkX = position.getX() + dx[i];
            int checkY = position.getY() + dy[i];

            // Ensure coordinates are within bounds
            if (checkX >= 0 && checkX < grid.getRows() && checkY >= 0 && checkY < grid.getColumns()) {
                char cellType = grid.getCellType(checkX, checkY);

                // Check if cell contains a herbivore (cow or deer)
                if (cellType == 'c' || cellType == 'd') {
                    // Find the actual herbivore object at this location
                    for (LandScapeObject obj : map.getLandscapeObjects()) {
                        if (obj instanceof Herbivore) {
                            Herbivore herbivore = (Herbivore) obj;
                            if (herbivore.getPosition().getX() == checkX
                                    && herbivore.getPosition().getY() == checkY
                                    && !herbivore.isDead()) {
                                return herbivore;
                            }
                        }
                    }
                }
            }
        }

        return null; // No herbivore found nearby
    }

    /**
     * Handles the carnivore drinking from a water area.
     * <p>
     * Overrides the base Animal class method to provide carnivore-specific behavior
     * when drinking water. Carnivores get less health benefit from water compared
     * to hunting.
     * </p>
     *
     * @param waterArea The water area the carnivore is drinking from
     */
    @Override
    public void drinkFromWaterArea(WaterArea waterArea) {
        super.drinkFromWaterArea(waterArea);

        // Add any carnivore-specific drinking behavior here
        // For example, carnivores might recover less health from drinking
        if (waterArea != null && !isDead()) {
            // Less health increase for carnivores since they prefer blood
            health = Math.min(maxHealth, health + 3);
        }
    }

    /**
     * Updates the carnivore's lifecycle status.
     * <p>
     * Extends the base Animal updateLifecycle method with carnivore-specific behavior,
     * particularly handling the hunting animation duration.
     * </p>
     */
    @Override
    public void updateLifecycle() {
        // Don't update if dead
        if (isDead()) {
            return;
        }

        super.updateLifecycle();

        // Any additional carnivore-specific update logic
        if (isHunting && huntingDuration > 0) {
            huntingDuration--;
            if (huntingDuration <= 0) {
                isHunting = false;
            }
        }
    }

    /**
     * Checks if the carnivore is dead.
     *
     * @return true if the carnivore's health is zero or below, false otherwise
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Makes the carnivore drink from water, reducing thirst more than regular drinking.
     */
    public void drinkFromWater() {
        thirst = Math.max(0, thirst - 30);  // More thirst reduction from water than regular drink()
        System.out.println(getDescription() + " drank from water. Thirst reduced to " + thirst);
    }

    /**
     * Creates a new carnivore of the same type at the same position (reproduction).
     *
     * @return A new Carnivore instance
     */
    @Override
    public Animal reproduce() {
        return new Carnivore(position, type);
    }

    /**
     * Gets a description of the carnivore.
     *
     * @return The type identifier of the carnivore
     */
    @Override
    public String getDescription() {
        return type;
    }

    /**
     * Checks if the carnivore is currently hunting.
     *
     * @return true if the carnivore is in hunting mode, false otherwise
     */
    public boolean isHunting() {
        return isHunting;
    }

    /**
     * Gets the attack power of the carnivore.
     *
     * @return The amount of damage dealt when attacking
     */
    public int getAttackPower() {
        return attackPower;
    }

    /**
     * Sets the attack power of the carnivore.
     *
     * @param attackPower The new attack power value
     */
    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    /**
     * Gets the amount of health increase from the last successful hunt.
     *
     * @return The health increase amount
     */
    public int getHealthIncreaseAmount() {
        return healthIncreaseAmount;
    }

    /**
     * Checks if the carnivore is at maximum health.
     *
     * @return true if the carnivore is at maximum health, false otherwise
     */
    public boolean isAtMaxHealth() {
        return atMaxHealth;
    }

    /**
     * Gets the current health of the carnivore.
     *
     * @return The carnivore's health value
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * Sets the health of the carnivore with proper bounds checking.
     * <p>
     * Ensures health stays within the valid range (0 to maxHealth) and
     * logs significant health changes.
     * </p>
     *
     * @param health The new health value
     */
    public void setHealth(int health) {
        int oldHealth = this.health;
        this.health = Math.min(maxHealth, Math.max(0, health));

        // Update max health status
        atMaxHealth = (this.health >= maxHealth);

        // Log significant health changes
        if (this.health != oldHealth) {
            System.out.println("HEALTH CHANGE: " + getDescription() + " health changed from "
                    + oldHealth + " to " + this.health
                    + " (" + (this.health - oldHealth > 0 ? "+" : "")
                    + (this.health - oldHealth) + ")");
        }
    }

    /**
     * Decreases the carnivore's health by the specified amount.
     * <p>
     * Useful for testing or simulating damage from environmental factors.
     * </p>
     *
     * @param amount The amount to decrease health by
     */
    public void decreaseHealth(int amount) {
        int oldHealth = this.health;
        this.health = Math.max(0, this.health - amount);

        // Update max health status
        atMaxHealth = (this.health >= maxHealth);

        System.out.println("HEALTH DECREASED: " + getDescription() + " health changed from "
                + oldHealth + " to " + this.health
                + " (-" + amount + ")");
    }

    /**
     * Increases the carnivore's health by the specified amount.
     * <p>
     * Used to simulate health gained from hunting or other healing effects.
     * </p>
     *
     * @param amount The amount to increase health by
     */
    public void increaseHealth(int amount) {
        int oldHealth = this.health;
        this.health = Math.min(maxHealth, this.health + amount);

        // Update max health status
        atMaxHealth = (this.health >= maxHealth);

        System.out.println("HEALTH INCREASED: " + getDescription() + " health changed from "
                + oldHealth + " to " + this.health
                + " (+" + amount + ")");
    }
}
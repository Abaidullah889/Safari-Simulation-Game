package my.company.my.safarigame.model;

/**
 * Abstract class representing an animal in the safari game.
 * <p>
 * Animals have properties such as health, hunger, thirst, and age, and can move,
 * eat, drink, and reproduce. They can also interact with water sources in the environment.
 * </p>
 * <p>
 * This class extends {@link LandScapeObject} and implements {@link TradeableItem},
 * making animals placeable in the safari landscape and able to be traded.
 * </p>
 */
public abstract class Animal extends LandScapeObject implements TradeableItem {

    /** Current age of the animal. */
    protected int age = 0;
    
    /** Current health of the animal, starting at 60% of maximum health. */
    public int health = 60; 
    
    /** Current hunger level of the animal. */
    protected int hunger = 0;
    
    /** Current thirst level of the animal. */
    public int thirst = 20;
    
    /** Maximum age the animal can reach before dying of old age. */
    protected int lifespan = 7;
    
    /** Flag indicating if the animal is scheduled for removal from the game. */
    public boolean isBeingRemoved = false;

    /** Rate at which thirst increases per lifecycle update. */
    public int thirstIncreaseRate = 3;
    
    /** Amount by which thirst decreases when drinking. */
    public int thirstDecreaseAmount = 20;
    
    /** Counter for the drinking animation. */
    private int drinkingAnimationCounter = 0;
    
    /** Duration of the drinking animation in game cycles. */
    private static final int DRINKING_ANIMATION_DURATION = 20;

    /** Movement speed of the animal. */
    protected int speed = 1;
    
    /** Maximum possible health for the animal. */
    protected int maxHealth = 100;
    
    /** Price of the animal when traded. */
    protected double price;
    
    /** Current position of the animal in the game world. */
    protected Coordinate position;
    
    /** Type identifier for the animal. */
    protected String type;

    /** Flag indicating if the animal is currently drinking. */
    public boolean isDrinking = false;
    
    /** How long the animal will continue drinking. */
    public int drinkingDuration = 2;
    
    /** Last known location of a water source the animal has visited. */
    public Coordinate lastWaterSourceLocation = null;
    
    /** Threshold at which the animal begins to feel thirsty. */
    protected int thirstThreshold = 40;

    /** Flag indicating if the animal is currently moving toward water. */
    protected boolean isMovingToWater = false;

    /** Flag indicating if the animal has a location tracking chip. */
    protected boolean hasLocationChip = false;

    /**
     * Constructs a new Animal at the specified position with the given type.
     *
     * @param position The initial position of the animal
     * @param type The type identifier for the animal
     */
    public Animal(Coordinate position, String type) {
        super(position, type);
        this.type = type;
        this.position = position;
    }

    /**
     * Sets the health of the animal.
     *
     * @param health The new health value
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Checks if the animal is alive.
     *
     * @return true if the animal is alive (not too old and has health), false otherwise
     */
    public boolean isAlive() {
        return age < lifespan && health > 0;
    }

    /**
     * Checks if the animal is dead.
     *
     * @return true if the animal is dead, false otherwise
     */
    public boolean isDead() {
        return !isAlive();
    }

    /**
     * Checks if the animal has a location tracking chip.
     *
     * @return true if the animal has a tracking chip, false otherwise
     */
    public boolean hasLocationChip() {
        return hasLocationChip;
    }

    /**
     * Sets whether the animal has a location tracking chip.
     *
     * @param hasChip true to give the animal a tracking chip, false to remove it
     */
    public void setLocationChip(boolean hasChip) {
        this.hasLocationChip = hasChip;
    }

    /**
     * Moves the animal to a new destination.
     *
     * @param destination The coordinate to move the animal to
     */
    public void move(Coordinate destination) {
        this.position = destination;
    }

    /**
     * Makes the animal eat, reducing its hunger.
     */
    public void eat() {
        hunger = Math.max(0, hunger - 20);
    }

    /**
     * Makes the animal drink directly, completely eliminating thirst and starting the drinking animation.
     */
    public void drinkDirectly() {
        this.thirst = 0;  // Reset thirst completely
        this.isDrinking = true;
        this.drinkingDuration = 5;
        System.out.println(getDescription() + " is drinking water. Thirst reduced to 0.");
    }

    /**
     * Updates the animal's lifecycle, handling aging, hunger, thirst, and health changes.
     * Inherited from LandScapeObject.
     */
    @Override
    public void update() {
        updateLifecycle();
    }

    /**
     * Gets the price of the animal when traded.
     * Implemented from TradeableItem interface.
     *
     * @return The price of the animal
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * Gets a description of the animal.
     * Implemented from TradeableItem interface.
     *
     * @return A description string of the animal
     */
    @Override
    public String getDescription() {
        return "Generic Animal";
    }

    /**
     * Gets the current position of the animal.
     *
     * @return The coordinate representing the animal's position
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Gets the current health of the animal.
     *
     * @return The animal's health value
     */
    public int getHealth() {
        return health;
    }

    /**
     * Gets the current age of the animal.
     *
     * @return The animal's age
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the current hunger level of the animal.
     *
     * @return The animal's hunger value
     */
    public int getHunger() {
        return hunger;
    }

    /**
     * Gets the current thirst level of the animal.
     *
     * @return The animal's thirst value
     */
    public int getThirst() {
        return thirst;
    }

    /**
     * Gets the lifespan of the animal.
     *
     * @return The maximum age the animal can reach
     */
    public int getLifespan() {
        return lifespan;
    }

    /**
     * Gets the movement speed of the animal.
     *
     * @return The animal's speed value
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Checks if the animal has a location tracking chip.
     * Alternative getter for hasLocationChip.
     *
     * @return true if the animal has a tracking chip, false otherwise
     */
    public boolean isHasLocationChip() {
        return hasLocationChip;
    }

    /**
     * Gets the maximum possible health of the animal.
     *
     * @return The animal's maximum health value
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Gets the health percentage of the animal, from 0 to 100.
     *
     * @return The animal's health as a percentage of its maximum health
     */
    public int getHealthPercentage() {
        return (health * 100) / maxHealth;
    }

    /**
     * Abstract method for animal reproduction.
     * Subclasses must implement this method to create a new animal of the same type.
     *
     * @return A new animal of the same type
     */
    public abstract Animal reproduce();

    /**
     * Sets the position of the animal to the specified coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public void setPosition(int x, int y) {
        this.position = new Coordinate(x, y);
    }

    /**
     * Makes the animal drink, reducing its thirst.
     */
    public void drink() {
        thirst = Math.max(0, thirst - 20);
    }

    /**
     * Updates the animal's lifecycle status, including age, hunger, thirst, and health.
     * This method is called during each game update cycle.
     */
    public void updateLifecycle() {
        age++;
        hunger += 5;

        // Slower thirst increase
        thirst += thirstIncreaseRate;
        thirst = Math.min(thirst, 100);

        // Handle continuous drinking
        if (isDrinking) {
            // Slower thirst reduction during drinking
            thirst = Math.max(0, thirst - 5); // Slower reduction

            // Increase health slightly during drinking
            health = Math.min(maxHealth, health + 1); // Slower health recovery

            // Reduce drinking duration
            drinkingDuration--;
            drinkingAnimationCounter = DRINKING_ANIMATION_DURATION; // Reset animation timer

            // Stop drinking when thirst is completely satisfied or duration ends
            if (thirst <= 0 || drinkingDuration <= 0) {
                isDrinking = false;
                drinkingDuration = 0;
                System.out.println(getDescription() + " finished drinking. Thirst: " + thirst);
            }
        } else if (drinkingAnimationCounter > 0) {
            // Keep animation going for a bit after drinking stops
            drinkingAnimationCounter--;
        }

        // Enhanced health reduction based on thirst - more gradual
        if (thirst > 90) {
            // Critical thirst - severe health penalty
            health -= 10;
            System.out.println(getDescription() + " is severely dehydrated and losing health rapidly!");
        } else if (thirst > 70) {
            // High thirst - moderate health penalty
            health -= 3;
            System.out.println(getDescription() + " is thirsty and losing health.");
        } else if (thirst > 50) {
            // Moderate thirst - mild penalty
            health -= 1;
        }

        // Health reduction from hunger (existing code)
        if (hunger > 80) {
            health -= 5; // Reduced penalty
        }

        // Ensure health doesn't go below 0
        health = Math.max(0, health);
    }

    /**
     * Starts continuous drinking from a water area.
     *
     * @param waterArea The water area the animal is drinking from
     */
    public void startContinuousDrinking(WaterArea waterArea) {
        if (waterArea == null) {
            return;
        }

        // Set drinking flag
        isDrinking = true;
        drinkingDuration = 10; // Longer duration for continuous drinking
        isMovingToWater = false; // No longer moving to water

        // Remember water source location
        lastWaterSourceLocation = waterArea.getPosition();

        System.out.println(getDescription() + " started continuous drinking at " + lastWaterSourceLocation);
    }

    /**
     * Checks if the animal should seek water based on its thirst level.
     *
     * @return true if the animal is thirsty and not already drinking or moving to water, false otherwise
     */
    public boolean shouldSeekWater() {
        return isThirsty() && !isDrinking && !isMovingToWater;
    }

    /**
     * Attempts to move the animal toward a known water source.
     *
     * @return true if the animal has a known water source to move toward, false otherwise
     */
    public boolean moveTowardKnownWaterSource() {
        if (lastWaterSourceLocation == null) {
            return false; // No known water source
        }

        // Animal remembers water location and is heading there
        isMovingToWater = true;
        System.out.println(getDescription() + " remembers water at " + lastWaterSourceLocation
                + " and is moving there. Current thirst: " + thirst);
        return true;
    }

    /**
     * Cancels the animal's water-seeking behavior.
     */
    public void cancelWaterSeeking() {
        isMovingToWater = false;
    }

    /**
     * Checks if the animal is currently moving toward water.
     *
     * @return true if the animal is moving toward water, false otherwise
     */
    public boolean isMovingToWater() {
        return isMovingToWater;
    }

    /**
     * Makes the animal drink from a specific water area.
     *
     * @param waterArea The water area to drink from
     */
    public void drinkFromWaterArea(WaterArea waterArea) {
        if (waterArea == null) {
            return;
        }

        // Record old thirst for logging
        int oldThirst = thirst;

        // Set drinking flag to stop movement
        isDrinking = true;
        drinkingDuration = 15; // Longer drinking duration for more benefit
        drinkingAnimationCounter = DRINKING_ANIMATION_DURATION;

        // Significantly reduce thirst - but at a reasonable rate
        thirst = Math.max(0, thirst - 15); // Initial big reduction

        // Remember water source location
        lastWaterSourceLocation = waterArea.getPosition();

        // Reset the isMovingToWater flag
        isMovingToWater = false;

        System.out.println(getDescription() + " started drinking at " + lastWaterSourceLocation
                + ". Thirst reduced from " + oldThirst + " to " + thirst);
    }

    /**
     * Checks if the animal is currently in a drinking animation.
     *
     * @return true if the animal is drinking or still in the drinking animation, false otherwise
     */
    public boolean isInDrinkingAnimation() {
        return isDrinking || drinkingAnimationCounter > 0;
    }

    /**
     * Gets the current value of the drinking animation counter.
     *
     * @return The drinking animation counter value
     */
    public int getDrinkingAnimationCounter() {
        return drinkingAnimationCounter;
    }

    /**
     * Checks if the animal is currently drinking.
     *
     * @return true if the animal is drinking, false otherwise
     */
    public boolean isDrinking() {
        return isDrinking;
    }

    /**
     * Gets the last known location of a water source the animal has visited.
     *
     * @return The coordinate of the last known water source, or null if none exists
     */
    public Coordinate getLastWaterSourceLocation() {
        return lastWaterSourceLocation;
    }

    /**
     * Gets the thirst threshold at which the animal begins to feel thirsty.
     *
     * @return The thirst threshold value
     */
    public int getThirstThreshold() {
        return thirstThreshold;
    }

    /**
     * Sets the thirst threshold for the animal.
     *
     * @param threshold The new thirst threshold value
     */
    public void setThirstThreshold(int threshold) {
        this.thirstThreshold = threshold;
    }

    /**
     * Checks if the animal is currently thirsty.
     *
     * @return true if the animal's thirst is above its threshold, false otherwise
     */
    public boolean isThirsty() {
        return thirst > thirstThreshold;
    }

    /**
     * Finds the nearest water source on the safari map.
     *
     * @param map The safari map containing landscape objects
     * @return The coordinate of the nearest water source, or null if none exists
     */
    public Coordinate findNearestWaterSource(SafariMap map) {
        if (map == null) {
            return null;
        }

        Coordinate currentPos = getPosition();
        Coordinate nearestWater = null;
        double minDistance = Double.MAX_VALUE;

        for (LandScapeObject obj : map.getLandscapeObjects()) {
            if (obj instanceof WaterArea) {
                Coordinate waterPos = obj.getPosition();
                double distance = calculateDistance(currentPos, waterPos);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestWater = waterPos;
                }
            }
        }

        return nearestWater;
    }

    /**
     * Helper method to calculate the Euclidean distance between two coordinates.
     *
     * @param pos1 The first coordinate
     * @param pos2 The second coordinate
     * @return The distance between the two coordinates
     */
    private double calculateDistance(Coordinate pos1, Coordinate pos2) {
        return Math.sqrt(
                Math.pow(pos1.x - pos2.x, 2)
                + Math.pow(pos1.y - pos2.y, 2)
        );
    }
}
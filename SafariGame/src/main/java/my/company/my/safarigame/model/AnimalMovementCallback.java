package my.company.my.safarigame.model;

/**
 * Interface for handling animal movement events in the safari game.
 * <p>
 * This callback interface allows components to be notified when an animal
 * changes its position. Implementations can define custom behavior in response
 * to animal movement events, such as updating the UI, tracking movement patterns,
 * or triggering other game mechanics.
 * </p>
 */
public interface AnimalMovementCallback {
    
    /**
     * Called when an animal moves to a new position.
     * <p>
     * This method is invoked after an animal has moved, providing both the
     * animal reference and its new position.
     * </p>
     *
     * @param animal The animal that has moved
     * @param newPosition The new coordinate position of the animal
     */
    void onAnimalMoved(Animal animal, Coordinate newPosition);
}
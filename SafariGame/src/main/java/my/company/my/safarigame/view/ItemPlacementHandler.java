package my.company.my.safarigame.view;

import my.company.my.safarigame.model.TradeableItem;

/**
 * Interface for handling item placement from inventory to map.
 * <p>
 * This interface defines the contract for components that need to handle
 * the placement of items from the player's inventory onto the game map.
 * It serves as a bridge between the inventory view and the map view,
 * allowing for decoupled communication between these components.
 * </p>
 * <p>
 * When a player selects an item in the inventory and chooses to place it,
 * the inventory view calls the implementation of this interface to initiate
 * the placement process. The implementation (typically in the map view or
 * controller) then handles the actual placement logic, such as enabling
 * placement mode and tracking where the player wants to position the item.
 * </p>
 * <p>
 * This pattern allows the inventory view to remain independent of the specific
 * placement implementation details, following good software design principles.
 * </p>
 */
public interface ItemPlacementHandler {
    
    /**
     * Handles the placement of a tradeable item on the game map.
     * <p>
     * This method is called when a player selects an item from their inventory
     * and indicates they want to place it on the map. The implementing class
     * should handle the placement process, which typically involves:
     * </p>
     * <ol>
     *   <li>Enabling a placement mode in the map view</li>
     *   <li>Tracking the selected item</li>
     *   <li>Waiting for the player to click on a valid map location</li>
     *   <li>Placing the item at the selected location</li>
     *   <li>Removing the item from the player's inventory</li>
     * </ol>
     *
     * @param item The tradeable item selected for placement
     */
    void handleItemPlacement(TradeableItem item);
}
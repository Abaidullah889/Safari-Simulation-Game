package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.Coordinate;
import my.company.my.safarigame.model.Plant;
import my.company.my.safarigame.model.Player;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

    private Player player;
    private Plant plantItem;

    @Before
    public void setUp() {
        player = new Player("Alice");
        plantItem = new Plant(new Coordinate(0, 0), "Shrub", 50.0);
    }

    @Test
    public void testPlayerInitialization() {
        assertEquals("Alice", player.getName());
        assertEquals(1000, player.getCapital(), 0.001);
    }

    @Test
    public void testBuyItem() {
        player.buyItem(plantItem);
        assertTrue(player.getInventory().contains(plantItem));
        assertEquals(950.0, player.getCapital(), 0.001);
    }

    @Test
    public void testSellItem() {
        player.buyItem(plantItem);
        player.sellItem(plantItem);
        assertFalse(player.getInventory().contains(plantItem));
        assertEquals(1000.0, player.getCapital(), 0.001);
    }

    @Test
    public void testRemoveInventoryItem() {
        player.addItemToInventory(plantItem);
        boolean removed = player.removeInventoryItem(plantItem);
        assertTrue(removed);
        assertFalse(player.getInventory().contains(plantItem));
    }

    @Test
    public void testUpdateCapital() {
        player.updateCapital(100);
        assertEquals(1100.0, player.getCapital(), 0.001);
    }
}

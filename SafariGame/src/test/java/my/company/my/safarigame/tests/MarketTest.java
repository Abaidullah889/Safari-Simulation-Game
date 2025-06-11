package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.Coordinate;
import my.company.my.safarigame.model.Market;
import my.company.my.safarigame.model.Plant;
import my.company.my.safarigame.model.Player;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MarketTest {

    private Market market;
    private Player player;
    private Plant plantItem;

    @Before
    public void setUp() {
        market = new Market();
        player = new Player("Bob");
        plantItem = new Plant(new Coordinate(1, 1), "Bush", 30.0);
    }

    @Test
    public void testAddItemToMarket() {
        market.addItem(plantItem);
        assertTrue(market.listAvailableItems().contains(plantItem));
    }

    @Test
    public void testRemoveItemFromMarket() {
        market.addItem(plantItem);
        market.removeItem(plantItem);
        assertFalse(market.listAvailableItems().contains(plantItem));
    }

    @Test
    public void testBuyItemFromMarket() {
        market.addItem(plantItem);
        market.buy(plantItem, player);
        assertTrue(player.getInventory().contains(plantItem));
    }

    @Test
    public void testSellItemToMarket() {
        player.addItemToInventory(plantItem);
        market.sell(plantItem, player);
        assertFalse(player.getInventory().contains(plantItem));
    }
}

package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.Coordinate;
import my.company.my.safarigame.model.Plant;
import my.company.my.safarigame.model.SafariMap;
import my.company.my.safarigame.model.Jeep;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SafariMapTest {

    private SafariMap map;

    @Before
    public void setUp() {
        map = new SafariMap("/grids/grid1.txt", 48); // Using your default settings
    }

    @Test
    public void testGridIsLoaded() {
        assertNotNull(map.getGrid());
    }

    @Test
    public void testAddLandscapeObject() {
        Plant plant = new Plant(new Coordinate(1, 1), "Bush", 10.0);
        map.addLandscapeObject(plant);
        assertTrue(map.getLandscapeObjects().contains(plant));
    }

    @Test
    public void testAddJeep() {
        Jeep jeep = new Jeep(new Coordinate(2, 2));
        map.addJeep(jeep);
        assertTrue(map.getJeeps().contains(jeep));
    }

    @Test
    public void testSetEntranceAndExit() {
        Coordinate entrance = new Coordinate(0, 0);
        Coordinate exit = new Coordinate(49, 49);
        map.setEntrance(entrance);
        map.setExit(exit);
        assertEquals(entrance, map.getEntrance());
        assertEquals(exit, map.getExit());
    }
}

package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.Coordinate;
import my.company.my.safarigame.model.Tourist;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TouristTest {

    private Tourist tourist;

    @Before
    public void setUp() {
        tourist = new Tourist(new Coordinate(7, 7));
    }

    @Test
    public void testTouristInitialization() {
        assertEquals(7, tourist.getPosition().getX());
        assertEquals(7, tourist.getPosition().getY());
        assertEquals(100, tourist.satisfaction);
        assertFalse(tourist.isRiding());
    }

    @Test
    public void testUpdateSatisfaction() {
        int oldSatisfaction = tourist.satisfaction;
        tourist.updateSatisfaction();
        assertEquals(oldSatisfaction - 1, tourist.satisfaction);
    }

    @Test
    public void testSetPosition() {
        Coordinate newPosition = new Coordinate(10, 10);
        tourist.setPosition(newPosition);
        assertEquals(newPosition, tourist.getPosition());
    }
}

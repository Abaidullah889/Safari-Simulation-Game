package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.Coordinate;
import my.company.my.safarigame.model.Jeep;
import my.company.my.safarigame.model.Tourist;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class JeepTest {

    private Jeep jeep;
    private Tourist tourist;

    @Before
    public void setUp() {
        jeep = new Jeep(new Coordinate(3, 3));
        tourist = new Tourist(new Coordinate(5, 5));
    }

    @Test
    public void testJeepInitialization() {
        assertEquals(3, jeep.getPosition().getX());
        assertEquals(3, jeep.getPosition().getY());
        assertEquals(0, jeep.getCurrentPassengers().size());
    }

    @Test
    public void testMoveTo() {
        Coordinate newDestination = new Coordinate(6, 6);
        jeep.moveTo(newDestination);
        assertEquals(newDestination, jeep.getPosition());
    }

    @Test
    public void testPickUpTourist() {
        jeep.pickUp(tourist);
        assertTrue(jeep.getCurrentPassengers().contains(tourist));
    }

    @Test
    public void testDropOffTourist() {
        jeep.pickUp(tourist);
        jeep.dropOff(tourist);
        assertFalse(jeep.getCurrentPassengers().contains(tourist));
    }

    @Test
    public void testJeepCapacityLimit() {
        for (int i = 0; i < 5; i++) { // Try to add 5 tourists to test capacity
            jeep.pickUp(new Tourist(new Coordinate(i, i)));
        }
        assertEquals(4, jeep.getCurrentPassengers().size()); // Capacity is 4
    }
}

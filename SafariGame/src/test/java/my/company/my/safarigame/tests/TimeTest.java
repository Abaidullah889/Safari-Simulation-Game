package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.Time;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TimeTest {

    private Time time;

    @Before
    public void setUp() {
        time = new Time(1, 1); // Day 1, Month 1
    }

    @Test
    public void testInitialTime() {
        assertEquals(1, time.getDay());
        assertEquals(1, time.getMonth());
        assertEquals(8, time.getHour());
    }

    @Test
    public void testAdvanceTimeByHour() {
        time.advanceTime(1); // advance by 1 hour
        assertEquals(9, time.getHour());
    }

    @Test
    public void testAdvanceTimeByDay() {
        time.advanceTime(2); // advance by 6 hours
        assertEquals(14, time.getHour());
    }

    @Test
    public void testAdvanceTimeByWeek() {
        int oldDay = time.getDay();
        time.advanceTime(3); // advance by 7 days
        assertEquals(oldDay + 7, time.getDay());
    }

    @Test
    public void testIsDaytime() {
        assertTrue(time.isDaytime()); // 8 AM should be daytime
        time.advanceTimeByHours(12); // Move to 8 PM
        assertFalse(time.isDaytime());
    }
}

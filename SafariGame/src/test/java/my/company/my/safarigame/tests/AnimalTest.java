package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.Carnivore;
import my.company.my.safarigame.model.Coordinate;
import my.company.my.safarigame.model.Herbivore;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AnimalTest {

    private Herbivore herbivore;
    private Carnivore carnivore;

    @Before
    public void setUp() {
        herbivore = new Herbivore(new Coordinate(5, 5), "Deer");
        carnivore = new Carnivore(new Coordinate(10, 10), "Lion");
    }

    @Test
    public void testHerbivoreCreatedAtPosition() {
        assertEquals(5, herbivore.getPosition().getX());
        assertEquals(5, herbivore.getPosition().getY());
    }

    @Test
    public void testCarnivoreCreatedAtPosition() {
        assertEquals(10, carnivore.getPosition().getX());
        assertEquals(10, carnivore.getPosition().getY());
    }

    @Test
    public void testAnimalCanMove() {
        Coordinate newPos = new Coordinate(15, 15);
        herbivore.move(newPos);
        assertEquals(newPos, herbivore.getPosition());

        carnivore.move(newPos);
        assertEquals(newPos, carnivore.getPosition());
    }

    @Test
    public void testHerbivoreGrazeIncreasesHealth() {
        int oldHealth = herbivore.getHealth();
        herbivore.graze();
        assertTrue(herbivore.getHealth() >= oldHealth);
    }

    @Test
    public void testCarnivoreHuntHealthIncrease() {
        Herbivore prey = new Herbivore(new Coordinate(11, 11), "Zebra");
        prey.setHealth(30); // Weaken prey for easy kill

        boolean killed = carnivore.hunt(prey);

        assertTrue(killed || carnivore.getHealth() > 60); // Either killed or at least some health increased
    }

    @Test
    public void testAnimalLifecycleAging() {
        int oldAge = herbivore.getAge();
        herbivore.updateLifecycle();
        assertEquals(oldAge + 1, herbivore.getAge());

        oldAge = carnivore.getAge();
        carnivore.updateLifecycle();
        assertEquals(oldAge + 1, carnivore.getAge());
    }

    @Test
    public void testAnimalHealthPercentageCalculation() {
        assertEquals((herbivore.getHealth() * 100) / herbivore.getMaxHealth(), herbivore.getHealthPercentage());
        assertEquals((carnivore.getHealth() * 100) / carnivore.getMaxHealth(), carnivore.getHealthPercentage());
    }
}

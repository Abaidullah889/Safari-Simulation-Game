package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.SafariGameModel;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SafariGameModelTest {

    private SafariGameModel model;

    @Before
    public void setUp() {
        model = new SafariGameModel();
    }

    @Test
    public void testGameStartsCorrectly() {
        model.startGame();
        assertEquals("Running", model.getGameState());
    }

    @Test
    public void testGamePausesCorrectly() {
        model.pauseGame();
        assertEquals("Paused", model.getGameState());
    }

    @Test
    public void testGameEndsCorrectly() {
        model.endGame();
        assertEquals("Ended", model.getGameState());
    }

    @Test
    public void testIncreaseScore() {
        int initialScore = model.getScore();
        model.increaseScore(10);
        assertEquals(initialScore + 10, model.getScore());
    }

    @Test
    public void testSetPlayerName() {
        model.setPlayer("John");
        assertEquals("John", model.getPlayer().getName());
    }
}

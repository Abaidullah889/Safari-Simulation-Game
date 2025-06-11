package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.Cell;
import my.company.my.safarigame.model.Grid;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class GridTest {

    private Grid grid;

    @Before
    public void setUp() {
        grid = new Grid(10, 10, 48);
    }

    @Test
    public void testGridInitialization() {
        assertEquals(10, grid.getRows());
        assertEquals(10, grid.getColumns());
        assertNotNull(grid.getCell(0, 0));
    }

    @Test
    public void testSetAndGetCellType() {
        grid.setCellType(2, 2, 'r');
        assertEquals('r', grid.getCellType(2, 2));
    }

    @Test
    public void testCellOccupation() {
        assertFalse(grid.isOccupied(5, 5));
        grid.getCell(5, 5).setOccupied(true);
        assertTrue(grid.isOccupied(5, 5));
    }

    @Test
    public void testGetNeighbors() {
        Cell centerCell = grid.getCell(5, 5);
        assertEquals(4, grid.getNeighbors(centerCell).size());
    }

    @Test
    public void testClearCell() {
        grid.setCellType(3, 3, 'w');
        grid.getCell(3, 3).setOccupied(true);

        grid.clearCell(3, 3);
        assertFalse(grid.getCell(3, 3).isOccupied());
        assertEquals('-', grid.getCell(3, 3).getCellType());
    }
}

package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObstaclesTest {

    private Obstacles obstacles;
    private FreeCellFinder finder;

    @BeforeEach
    void setUp() {
        obstacles = new Obstacles();
        finder = new FreeCellFinder(20, 20, new Random(3));
    }

    @Test
    void spawnAddsTheRequestedAmountOnTheGrid() {
        assertEquals(10, obstacles.spawn(10, finder, cell -> false));
        assertEquals(10, obstacles.count());
        obstacles.getPositions().forEach(p -> assertTrue(p.isInside(20, 20)));
    }

    @Test
    void spawnNeverUsesForbiddenCells() {
        obstacles.spawn(40, finder, cell -> cell.y() == 0);
        obstacles.getPositions().forEach(p -> assertFalse(p.y() == 0));
    }

    @Test
    void spawnNeverExceedsTheLimit() {
        obstacles.spawn(500, finder, cell -> false);
        assertEquals(Obstacles.MAX_OBSTACLES, obstacles.count());
    }

    @Test
    void spawnStopsWhenNoCellIsFree() {
        FreeCellFinder tiny = new FreeCellFinder(2, 2, new Random(0));
        assertEquals(4, obstacles.spawn(10, tiny, cell -> false));
    }

    @Test
    void addingTheSameCellTwiceKeepsOneObstacle() {
        obstacles.add(new Position(1, 1));
        obstacles.add(new Position(1, 1));
        assertEquals(1, obstacles.count());
    }

    @Test
    void positionsCannotBeModifiedFromOutside() {
        assertThrows(UnsupportedOperationException.class, () -> obstacles.getPositions().add(new Position(0, 0)));
    }

    @Test
    void wallsBlockCellsButDoNotCountTowardsTheSpawnLimit() {
        java.util.Set<Position> layout = new java.util.HashSet<>();
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 4; y++) {
                layout.add(new Position(x, y));
            }
        }
        obstacles.setWalls(layout);
        assertTrue(obstacles.contains(new Position(5, 2)));
        obstacles.spawn(500, finder, cell -> false);
        assertEquals(Obstacles.MAX_OBSTACLES, obstacles.spawnedCount());
        assertEquals(80 + Obstacles.MAX_OBSTACLES, obstacles.count());
        obstacles.getSpawned().forEach(p -> assertFalse(layout.contains(p)));
    }

    @Test
    void clearRemovesEverything() {
        obstacles.add(new Position(4, 4));
        obstacles.clear();
        assertFalse(obstacles.contains(new Position(4, 4)));
    }
}

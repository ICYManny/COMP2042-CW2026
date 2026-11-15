package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PositionTest {

    @Test
    void stepMovesOneCellInEachDirection() {
        Position p = new Position(5, 5);
        assertEquals(new Position(5, 4), p.step(Direction.UP));
        assertEquals(new Position(5, 6), p.step(Direction.DOWN));
        assertEquals(new Position(4, 5), p.step(Direction.LEFT));
        assertEquals(new Position(6, 5), p.step(Direction.RIGHT));
    }

    @Test
    void cornersAreInsideAndOneBeyondIsOutside() {
        assertTrue(new Position(0, 0).isInside(20, 20));
        assertTrue(new Position(19, 19).isInside(20, 20));
        assertFalse(new Position(20, 5).isInside(20, 20));
        assertFalse(new Position(5, -1).isInside(20, 20));
    }

    @Test
    void wrapBringsPositionBackOnOppositeSide() {
        assertEquals(new Position(19, 3), new Position(-1, 3).wrap(20, 20));
        assertEquals(new Position(0, 3), new Position(20, 3).wrap(20, 20));
        assertEquals(new Position(7, 19), new Position(7, -1).wrap(20, 20));
    }

    @Test
    void wrapLeavesPositionsOnTheGridUnchanged() {
        assertEquals(new Position(4, 9), new Position(4, 9).wrap(20, 20));
    }

    @Test
    void distanceCountsHorizontalPlusVerticalSteps() {
        assertEquals(7, new Position(1, 1).distanceTo(new Position(4, 5)));
        assertEquals(0, new Position(3, 3).distanceTo(new Position(3, 3)));
    }

    @Test
    void positionsWithSameCoordinatesAreEqual() {
        assertEquals(new Position(2, 8), new Position(2, 8));
        assertEquals(new Position(2, 8).hashCode(), new Position(2, 8).hashCode());
    }
}

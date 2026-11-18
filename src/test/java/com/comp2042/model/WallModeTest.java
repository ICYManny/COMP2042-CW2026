package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WallModeTest {

    private static final int SIZE = 20;

    @Test
    void cellsOnTheBoardAreUnchangedInEveryMode() {
        Position inside = new Position(7, 3);
        for (WallMode mode : WallMode.values()) {
            assertEquals(Optional.of(inside), mode.resolve(inside, SIZE, SIZE));
        }
    }

    @Test
    void wrapAllWrapsEveryEdge() {
        assertEquals(Optional.of(new Position(19, 5)), WallMode.WRAP_ALL.resolve(new Position(-1, 5), SIZE, SIZE));
        assertEquals(Optional.of(new Position(5, 0)), WallMode.WRAP_ALL.resolve(new Position(5, 20), SIZE, SIZE));
    }

    @Test
    void wrapSidesWrapsLeftAndRight() {
        assertEquals(Optional.of(new Position(0, 5)), WallMode.WRAP_SIDES.resolve(new Position(20, 5), SIZE, SIZE));
        assertEquals(Optional.of(new Position(19, 5)), WallMode.WRAP_SIDES.resolve(new Position(-1, 5), SIZE, SIZE));
    }

    @Test
    void wrapSidesHasSolidTopAndBottom() {
        assertTrue(WallMode.WRAP_SIDES.resolve(new Position(5, -1), SIZE, SIZE).isEmpty());
        assertTrue(WallMode.WRAP_SIDES.resolve(new Position(5, 20), SIZE, SIZE).isEmpty());
    }

    @Test
    void solidStopsAtEveryEdge() {
        assertTrue(WallMode.SOLID.resolve(new Position(-1, 5), SIZE, SIZE).isEmpty());
        assertTrue(WallMode.SOLID.resolve(new Position(20, 5), SIZE, SIZE).isEmpty());
        assertTrue(WallMode.SOLID.resolve(new Position(5, -1), SIZE, SIZE).isEmpty());
        assertTrue(WallMode.SOLID.resolve(new Position(5, 20), SIZE, SIZE).isEmpty());
    }
}

package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FreeCellFinderTest {

    @Test
    void returnsEmptyWhenEveryCellIsBlocked() {
        FreeCellFinder finder = new FreeCellFinder(4, 4, new Random(0));
        assertTrue(finder.find(cell -> true).isEmpty());
    }

    @Test
    void findsTheOnlyFreeCell() {
        Position only = new Position(2, 3);
        FreeCellFinder finder = new FreeCellFinder(4, 4, new Random(0));
        assertEquals(Optional.of(only), finder.find(cell -> !cell.equals(only)));
    }

    @Test
    void neverReturnsABlockedOrOffGridCell() {
        FreeCellFinder finder = new FreeCellFinder(10, 8, new Random(7));
        for (int i = 0; i < 1000; i++) {
            Position p = finder.find(cell -> cell.x() < 5).orElseThrow();
            assertTrue(p.isInside(10, 8));
            assertFalse(p.x() < 5);
        }
    }

    @Test
    void sameSeedGivesSameCells() {
        FreeCellFinder a = new FreeCellFinder(20, 20, new Random(99));
        FreeCellFinder b = new FreeCellFinder(20, 20, new Random(99));
        for (int i = 0; i < 20; i++) {
            assertEquals(a.find(cell -> false), b.find(cell -> false));
        }
    }
}

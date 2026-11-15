package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectionTest {

    @Test
    void oppositePairsAreCorrect() {
        assertEquals(Direction.DOWN, Direction.UP.opposite());
        assertEquals(Direction.UP, Direction.DOWN.opposite());
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite());
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
    }

    @Test
    void oppositeOfOppositeIsItself() {
        for (Direction d : Direction.values()) {
            assertEquals(d, d.opposite().opposite());
        }
    }

    @Test
    void isOppositeOnlyForTheReverseDirection() {
        assertTrue(Direction.UP.isOpposite(Direction.DOWN));
        assertFalse(Direction.UP.isOpposite(Direction.LEFT));
        assertFalse(Direction.UP.isOpposite(Direction.UP));
    }

    @Test
    void anglesMatchSpriteRotation() {
        assertEquals(0, Direction.UP.getAngle());
        assertEquals(90, Direction.RIGHT.getAngle());
        assertEquals(180, Direction.DOWN.getAngle());
        assertEquals(270, Direction.LEFT.getAngle());
    }

    @Test
    void offsetsAreUnitSteps() {
        for (Direction d : Direction.values()) {
            assertEquals(1, Math.abs(d.getDx()) + Math.abs(d.getDy()));
        }
    }
}

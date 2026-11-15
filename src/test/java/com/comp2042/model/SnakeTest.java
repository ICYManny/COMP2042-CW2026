package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeTest {

    private Snake snake;

    @BeforeEach
    void setUp() {
        snake = new Snake(new Position(10, 10), Direction.LEFT, 3);
    }

    @Test
    void bodyTrailsBehindTheHead() {
        assertEquals(List.of(new Position(10, 10), new Position(11, 10), new Position(12, 10)), snake.getSegments());
        assertEquals(new Position(12, 10), snake.getTail());
    }

    @Test
    void lengthBelowOneIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Snake(new Position(0, 0), Direction.UP, 0));
    }

    @Test
    void moveWithoutGrowingKeepsLength() {
        snake.moveTo(snake.nextHead(), false);
        assertEquals(3, snake.length());
        assertEquals(new Position(9, 10), snake.getHead());
        assertEquals(new Position(11, 10), snake.getTail());
    }

    @Test
    void moveWhileGrowingAddsASegment() {
        snake.moveTo(snake.nextHead(), true);
        assertEquals(4, snake.length());
        assertEquals(new Position(12, 10), snake.getTail());
    }

    @Test
    void cannotReverseIntoItself() {
        assertFalse(snake.turn(Direction.RIGHT));
        assertEquals(Direction.LEFT, snake.getDirection());
    }

    @Test
    void canTurnSideways() {
        assertTrue(snake.turn(Direction.UP));
        assertEquals(new Position(10, 9), snake.nextHead());
    }

    @Test
    void twoQuickTurnsCannotReverseBeforeMoving() {
        // moving LEFT: UP is fine, but RIGHT is still a reversal because the snake has not moved up yet
        assertTrue(snake.turn(Direction.UP));
        assertFalse(snake.turn(Direction.RIGHT));
    }

    @Test
    void turnIsJudgedAgainstLastMoveNotLastKey() {
        snake.turn(Direction.UP);
        snake.moveTo(snake.nextHead(), false);
        assertTrue(snake.turn(Direction.RIGHT));
    }

    @Test
    void movingIntoTheTailIsSafeUnlessGrowing() {
        Position tail = snake.getTail();
        assertFalse(snake.collidesWithBody(tail, false));
        assertTrue(snake.collidesWithBody(tail, true));
    }

    @Test
    void movingIntoAMiddleSegmentCollides() {
        assertTrue(snake.collidesWithBody(new Position(11, 10), false));
    }

    @Test
    void occupiesReportsEverySegment() {
        assertTrue(snake.occupies(new Position(12, 10)));
        assertFalse(snake.occupies(new Position(13, 10)));
    }

    @Test
    void getSegmentsReturnsACopy() {
        snake.getSegments().clear();
        assertEquals(3, snake.length());
    }
}

package com.comp2042.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * The snake's body: an ordered list of cells from head to tail plus the direction it is
 * heading.
 * <p>
 * The snake only knows about itself. Walls, obstacles, coins and dying are game rules
 * and live in {@link GameModel}, so this class has a single responsibility.
 */
public class Snake {

    private final Deque<Position> segments = new ArrayDeque<>();
    private Direction direction;
    private Direction lastMovedDirection;

    /**
     * Creates a straight snake.
     *
     * @param head      position of the head
     * @param direction direction the snake faces; the body trails behind it
     * @param length    number of segments including the head, at least 1
     * @throws IllegalArgumentException if length is less than 1
     */
    public Snake(Position head, Direction direction, int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Snake length must be at least 1");
        }
        this.direction = direction;
        this.lastMovedDirection = direction;
        Position segment = head;
        for (int i = 0; i < length; i++) {
            segments.addLast(segment);
            segment = segment.step(direction.opposite());
        }
    }

    /**
     * Changes the direction the snake will move on its next step.
     * <p>
     * A turn is checked against the direction the snake last actually moved, not the last
     * key pressed. Otherwise pressing two keys quickly within one step (for example UP then
     * LEFT while moving RIGHT) would reverse the snake into itself.
     *
     * @param newDirection requested direction
     * @return true if the turn was accepted, false if it would reverse the snake
     */
    public boolean turn(Direction newDirection) {
        if (newDirection.isOpposite(lastMovedDirection) && segments.size() > 1) {
            return false;
        }
        direction = newDirection;
        return true;
    }

    /** @return the cell the head will enter next, before any wrapping is applied */
    public Position nextHead() {
        return getHead().step(direction);
    }

    /**
     * Moves the head to a new cell.
     *
     * @param newHead cell the head moves into (already wrapped if needed)
     * @param grow    true to keep the tail, making the snake one segment longer
     */
    public void moveTo(Position newHead, boolean grow) {
        segments.addFirst(newHead);
        if (!grow) {
            segments.removeLast();
        }
        lastMovedDirection = direction;
    }

    /**
     * Checks whether moving into a cell would hit the snake's own body.
     * The tail is ignored unless the snake is growing, because it moves out of the way.
     *
     * @param cell    cell the head is about to enter
     * @param growing true if the snake grows on this step
     * @return true if the head would collide with the body
     */
    public boolean collidesWithBody(Position cell, boolean growing) {
        Iterator<Position> it = segments.iterator();
        int index = 0;
        int last = segments.size() - 1;
        while (it.hasNext()) {
            Position segment = it.next();
            boolean isMovingTail = index == last && !growing;
            if (!isMovingTail && segment.equals(cell)) {
                return true;
            }
            index++;
        }
        return false;
    }

    /**
     * @param cell a grid cell
     * @return true if any segment of the snake is on that cell
     */
    public boolean occupies(Position cell) {
        return segments.contains(cell);
    }

    /** @return the head position */
    public Position getHead() {
        return segments.peekFirst();
    }

    /** @return the tail position */
    public Position getTail() {
        return segments.peekLast();
    }

    /** @return a copy of all segments, ordered from head to tail */
    public List<Position> getSegments() {
        return new ArrayList<>(segments);
    }

    /** @return number of segments including the head */
    public int length() {
        return segments.size();
    }

    /** @return the direction the snake will move next */
    public Direction getDirection() {
        return direction;
    }
}

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

    /** How many turns can be typed ahead of the snake's movement. */
    public static final int MAX_QUEUED_TURNS = 2;
    /** The snake can never be shorter than this. */
    public static final int MIN_LENGTH = 2;

    private final Deque<Position> segments = new ArrayDeque<>();
    private final Deque<Direction> queuedTurns = new ArrayDeque<>();
    private Direction direction;
    private Position previousTail;

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
        Position segment = head;
        for (int i = 0; i < length; i++) {
            segments.addLast(segment);
            segment = segment.step(direction.opposite());
        }
        previousTail = getTail();
    }

    /**
     * Queues a turn to happen on a coming step.
     * <p>
     * Up to {@value #MAX_QUEUED_TURNS} turns are remembered, so pressing two keys quickly
     * within one step (for example UP then LEFT while moving RIGHT) performs both turns on
     * the next two steps instead of dropping the second key. Each turn is checked against
     * the direction the snake will be facing when it happens, so a queued turn can never
     * reverse the snake into itself.
     *
     * @param newDirection requested direction
     * @return true if the turn was queued; false if it would reverse the snake, does not
     *         change direction, or the queue is full
     */
    public boolean turn(Direction newDirection) {
        Direction facing = queuedTurns.isEmpty() ? direction : queuedTurns.peekLast();
        boolean reverses = newDirection.isOpposite(facing) && segments.size() > 1;
        if (reverses || newDirection == facing || queuedTurns.size() >= MAX_QUEUED_TURNS) {
            return false;
        }
        queuedTurns.addLast(newDirection);
        return true;
    }

    /** {@return the cell the head will enter next (including any queued turn), before wrapping} */
    public Position nextHead() {
        return getHead().step(nextDirection());
    }

    private Direction nextDirection() {
        return queuedTurns.isEmpty() ? direction : queuedTurns.peekFirst();
    }

    /**
     * Moves the head to a new cell, performing the next queued turn if there is one.
     *
     * @param newHead cell the head moves into (already wrapped if needed)
     * @param grow    true to keep the tail, making the snake one segment longer
     */
    public void moveTo(Position newHead, boolean grow) {
        direction = nextDirection();
        queuedTurns.pollFirst();
        Position oldTail = getTail();
        segments.addFirst(newHead);
        if (!grow) {
            segments.removeLast();
        }
        previousTail = grow ? getTail() : oldTail;
    }

    /**
     * Removes segments from the tail, never leaving fewer than {@value #MIN_LENGTH}.
     *
     * @param amount number of segments to remove
     * @return number of segments actually removed
     */
    public int shrink(int amount) {
        int removed = 0;
        while (removed < amount && segments.size() > MIN_LENGTH) {
            segments.removeLast();
            removed++;
        }
        previousTail = getTail();
        return removed;
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
     * Returns true if any segment of the snake is on that cell.
     *
     * @param cell a grid cell
     * @return true if any segment of the snake is on that cell
     */
    public boolean occupies(Position cell) {
        return segments.contains(cell);
    }

    /** {@return the head position} */
    public Position getHead() {
        return segments.peekFirst();
    }

    /** {@return the tail position} */
    public Position getTail() {
        return segments.peekLast();
    }

    /** {@return a copy of all segments, ordered from head to tail} */
    public List<Position> getSegments() {
        return new ArrayList<>(segments);
    }

    /** {@return number of segments including the head} */
    public int length() {
        return segments.size();
    }

    /**
     * Where the tail was before the last move. The view slides the tail from here to
     * {@link #getTail()} so movement looks continuous. Equal to the tail after growing.
     *
     * @return the tail position before the last move
     */
    public Position getPreviousTail() {
        return previousTail;
    }

    /** {@return the direction the snake last moved in (queued turns not included)} */
    public Direction getDirection() {
        return direction;
    }
}

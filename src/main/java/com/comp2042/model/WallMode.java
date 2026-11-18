package com.comp2042.model;

import java.util.Optional;

/**
 * What happens when the snake reaches the edge of the board.
 * <p>
 * Each difficulty picks one of these rules, and GameModel asks the rule where the head
 * ends up instead of checking difficulty flags itself (Strategy pattern). A new wall rule
 * only needs a new constant here.
 */
public enum WallMode {
    /** Every edge wraps round to the opposite side. */
    WRAP_ALL(true, true),
    /** The left and right edges wrap; the top and bottom edges are solid. */
    WRAP_SIDES(true, false),
    /** Every edge is solid. */
    SOLID(false, false);

    private final boolean wrapsHorizontally;
    private final boolean wrapsVertically;

    WallMode(boolean wrapsHorizontally, boolean wrapsVertically) {
        this.wrapsHorizontally = wrapsHorizontally;
        this.wrapsVertically = wrapsVertically;
    }

    /**
     * Works out where the head lands when it moves into a cell.
     *
     * @param cell   the cell the head is moving into, possibly off the board
     * @param width  board width in cells
     * @param height board height in cells
     * @return the cell on the board the head ends up in, or empty if it hit a solid wall
     */
    public Optional<Position> resolve(Position cell, int width, int height) {
        boolean offSide = cell.x() < 0 || cell.x() >= width;
        boolean offTopOrBottom = cell.y() < 0 || cell.y() >= height;
        if ((offSide && !wrapsHorizontally) || (offTopOrBottom && !wrapsVertically)) {
            return Optional.empty();
        }
        return Optional.of(cell.wrap(width, height));
    }

    /** @return true if the left and right edges wrap */
    public boolean wrapsHorizontally() {
        return wrapsHorizontally;
    }

    /** @return true if the top and bottom edges wrap */
    public boolean wrapsVertically() {
        return wrapsVertically;
    }
}

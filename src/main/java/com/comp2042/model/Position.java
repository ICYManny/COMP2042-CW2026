package com.comp2042.model;

/**
 * An immutable cell coordinate on the game grid.
 * <p>
 * Replaces {@code java.awt.Point}, which is mutable (a shared Point could be changed by
 * any class holding it, which is why the old code copied it defensively everywhere) and
 * pulled the AWT desktop toolkit into a JavaFX game.
 *
 * @param x column, starting at 0 on the left
 * @param y row, starting at 0 at the top
 */
public record Position(int x, int y) {

    /**
     * @param direction direction to move in
     * @return the neighbouring position one cell away in that direction (may be off the grid)
     */
    public Position step(Direction direction) {
        return new Position(x + direction.getDx(), y + direction.getDy());
    }

    /**
     * @param width  grid width in cells
     * @param height grid height in cells
     * @return true if this position lies on a grid of the given size
     */
    public boolean isInside(int width, int height) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Wraps a position that has left the grid back in on the opposite side.
     *
     * @param width  grid width in cells
     * @param height grid height in cells
     * @return the equivalent position on the grid
     */
    public Position wrap(int width, int height) {
        return new Position(Math.floorMod(x, width), Math.floorMod(y, height));
    }

    /**
     * @param other another position
     * @return the number of horizontal plus vertical steps between the two positions
     */
    public int distanceTo(Position other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
}

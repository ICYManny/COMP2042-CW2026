package com.comp2042;

/**
 * The four directions the snake can move in.
 * <p>
 * Each constant knows its grid offset and the rotation (in degrees, clockwise from "up")
 * that sprites need when facing that way, which replaces the char codes 'U', 'D', 'L', 'R'
 * and the scattered rotation numbers used before.
 */
public enum Direction {
    UP(0, -1, 0),
    RIGHT(1, 0, 90),
    DOWN(0, 1, 180),
    LEFT(-1, 0, 270);

    private final int dx;
    private final int dy;
    private final int angle;

    Direction(int dx, int dy, int angle) {
        this.dx = dx;
        this.dy = dy;
        this.angle = angle;
    }

    /** @return the change in x when moving one cell in this direction */
    public int getDx() {
        return dx;
    }

    /** @return the change in y when moving one cell in this direction */
    public int getDy() {
        return dy;
    }

    /** @return the sprite rotation in degrees for this direction */
    public int getAngle() {
        return angle;
    }

    /** @return the direction pointing the opposite way */
    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    /**
     * @param other another direction
     * @return true if {@code other} points exactly the opposite way
     */
    public boolean isOpposite(Direction other) {
        return opposite() == other;
    }
}

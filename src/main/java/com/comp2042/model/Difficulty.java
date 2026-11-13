package com.comp2042.model;

/**
 * Difficulty presets, replacing the old char codes 'E', 'N' and 'H'.
 * <p>
 * A typo in a char (for example 'n') silently left the speed at 0; an enum makes invalid
 * values impossible and keeps each preset's settings together.
 */
public enum Difficulty {
    EASY(8.5, true),
    NORMAL(8.5, false),
    HARD(13, false);

    private final double speed;
    private final boolean wrapsAtBorders;

    Difficulty(double speed, boolean wrapsAtBorders) {
        this.speed = speed;
        this.wrapsAtBorders = wrapsAtBorders;
    }

    /** @return snake moves per second */
    public double getSpeed() {
        return speed;
    }

    /** @return true if the snake reappears on the opposite side instead of dying at a wall */
    public boolean wrapsAtBorders() {
        return wrapsAtBorders;
    }
}

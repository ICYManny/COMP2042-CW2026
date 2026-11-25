package com.comp2042.model;

/**
 * Temporary effects that coins can give the snake. Each lasts a fixed number of seconds
 * of play time (time spent paused does not count).
 */
public enum Effect {
    /** The snake moves at 60% speed. */
    SLOW("Slow", 8),
    /** The snake passes through walls and obstacles. */
    GHOST("Ghost", 6),
    /** Coins are worth double points. */
    DOUBLE_POINTS("x2 Points", 10),
    /** The snake moves 50% faster (a penalty from the rainbow coin). */
    SPEED_BOOST("Speed!", 5);

    private final String displayName;
    private final double durationSeconds;

    Effect(String displayName, double durationSeconds) {
        this.displayName = displayName;
        this.durationSeconds = durationSeconds;
    }

    /** {@return the name shown on screen} */
    public String getDisplayName() {
        return displayName;
    }

    /** {@return how long the effect lasts once collected} */
    public double getDurationSeconds() {
        return durationSeconds;
    }
}

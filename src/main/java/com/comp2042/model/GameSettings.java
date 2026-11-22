package com.comp2042.model;

/**
 * The options chosen before a game starts. Immutable, so a running game can never have
 * its settings changed underneath it.
 *
 * @param width           grid width in cells
 * @param height          grid height in cells
 * @param powerUpsEnabled whether special coins can appear
 * @param difficulty      speed and wall behaviour
 * @param startLevel      number of the level to start on, from 1
 */
public record GameSettings(int width, int height, boolean powerUpsEnabled, Difficulty difficulty, int startLevel) {

    /**
     * Validates the settings.
     *
     * @throws IllegalArgumentException if the grid is too small to play on or difficulty is null
     */
    public GameSettings {
        if (width < 5 || height < 5) {
            throw new IllegalArgumentException("Grid must be at least 5x5, was " + width + "x" + height);
        }
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty must not be null");
        }
        if (startLevel < 1) {
            throw new IllegalArgumentException("Start level must be at least 1, was " + startLevel);
        }
    }

    /**
     * Settings that start on the first level.
     *
     * @param width           grid width in cells
     * @param height          grid height in cells
     * @param powerUpsEnabled whether special coins can appear
     * @param difficulty      speed and wall behaviour
     */
    public GameSettings(int width, int height, boolean powerUpsEnabled, Difficulty difficulty) {
        this(width, height, powerUpsEnabled, difficulty, 1);
    }
}

package com.comp2042.model;

/**
 * The options chosen before a game starts. Immutable, so a running game can never have
 * its settings changed underneath it.
 *
 * @param width           grid width in cells
 * @param height          grid height in cells
 * @param powerUpsEnabled whether special coins can appear
 * @param difficulty      speed and wall behaviour
 */
public record GameSettings(int width, int height, boolean powerUpsEnabled, Difficulty difficulty) {

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
    }
}

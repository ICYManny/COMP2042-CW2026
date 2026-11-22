package com.comp2042.model.level;

import com.comp2042.model.Position;

import java.util.Set;

/**
 * One playable level: its wall layout, how many coins clear it, and how much faster the
 * snake moves on it.
 *
 * @param number          position in the level list, starting at 1
 * @param name            name shown to the player
 * @param coinsToClear    coins needed to finish the level; 0 means the level never ends
 * @param speedMultiplier factor applied to the difficulty's speed, 1.0 for no change
 * @param walls           cells that are permanently blocked
 */
public record Level(int number, String name, int coinsToClear, double speedMultiplier, Set<Position> walls) {

    /**
     * Validates the level and makes the wall set read-only.
     *
     * @throws IllegalArgumentException if the number, coin target or speed is invalid
     */
    public Level {
        if (number < 1) {
            throw new IllegalArgumentException("Level number must be at least 1");
        }
        if (coinsToClear < 0) {
            throw new IllegalArgumentException("Coins to clear must not be negative");
        }
        if (speedMultiplier <= 0) {
            throw new IllegalArgumentException("Speed multiplier must be positive");
        }
        walls = Set.copyOf(walls);
    }

    /**
     * A level with no walls and no target: the classic game, played until the board is
     * full or the snake crashes.
     *
     * @return the endless level
     */
    public static Level endless() {
        return new Level(1, "Endless", 0, 1.0, Set.of());
    }

    /** @return true if the level never ends by collecting coins */
    public boolean isEndless() {
        return coinsToClear == 0;
    }

    /** @return the text shown in the level drop-down, e.g. "2. Pillars" */
    @Override
    public String toString() {
        return number + ". " + name;
    }
}

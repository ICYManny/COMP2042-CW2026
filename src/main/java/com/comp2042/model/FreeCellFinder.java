package com.comp2042.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Picks a random empty cell on the grid.
 * <p>
 * Coins and obstacles used to pick random cells in a do/while loop until they hit a free
 * one. That loop never ends when the board is full (the game froze when the player was
 * about to win) and the code was duplicated. This class lists the free cells and picks
 * one, returning nothing when there are none. The random generator is passed in so tests
 * can use a fixed seed.
 */
public class FreeCellFinder {

    private final int width;
    private final int height;
    private final Random random;

    /**
     * @param width  grid width in cells
     * @param height grid height in cells
     * @param random source of randomness
     */
    public FreeCellFinder(int width, int height, Random random) {
        this.width = width;
        this.height = height;
        this.random = random;
    }

    /**
     * @param blocked test returning true for cells that may not be used
     * @return a random cell for which {@code blocked} is false, or empty if every cell is blocked
     */
    public Optional<Position> find(Predicate<Position> blocked) {
        List<Position> free = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position cell = new Position(x, y);
                if (!blocked.test(cell)) {
                    free.add(cell);
                }
            }
        }
        if (free.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(free.get(random.nextInt(free.size())));
    }
}

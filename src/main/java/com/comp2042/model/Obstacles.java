package com.comp2042.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The blocks on the board that kill the snake on contact.
 * <p>
 * Renamed from {@code Obstacle} because one instance holds every obstacle on the board.
 * Positions are kept in a set, so the same cell can never be added twice.
 */
public class Obstacles {

    /** Upper limit so random obstacles can never fill the board. */
    public static final int MAX_OBSTACLES = 50;

    private final Set<Position> positions = new LinkedHashSet<>();

    /**
     * Adds obstacles on random free cells, stopping early if the limit is reached or no
     * free cell is left.
     *
     * @param amount     number of obstacles to add
     * @param freeCells  used to pick random cells
     * @param forbidden  cells where an obstacle may not go (snake, coin, safe zone)
     * @return number of obstacles actually added
     */
    public int spawn(int amount, FreeCellFinder freeCells, Predicate<Position> forbidden) {
        int added = 0;
        while (added < amount && positions.size() < MAX_OBSTACLES) {
            Optional<Position> cell = freeCells.find(forbidden.or(positions::contains));
            if (cell.isEmpty()) {
                break;
            }
            positions.add(cell.get());
            added++;
        }
        return added;
    }

    /**
     * @param cell a grid cell
     * @return true if an obstacle is on that cell
     */
    public boolean contains(Position cell) {
        return positions.contains(cell);
    }

    /** @return a read-only view of every obstacle position */
    public Set<Position> getPositions() {
        return Collections.unmodifiableSet(positions);
    }

    /** @return number of obstacles on the board */
    public int count() {
        return positions.size();
    }

    /** Removes every obstacle. */
    public void clear() {
        positions.clear();
    }
}

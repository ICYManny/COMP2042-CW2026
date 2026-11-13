package com.comp2042.model;

import com.comp2042.model.coin.Coin;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * The blocks on the board that kill the snake on contact.
 * <p>
 * Renamed from {@code Obstacle} because one instance holds every obstacle on the board.
 * Positions are kept in a set, so the same cell can never be added twice.
 */
public class Obstacles {

    private static final int MAX_OBSTACLES = 50;
    private static final int SAFE_DISTANCE = 3;

    private final Set<Position> positions = new LinkedHashSet<>();
    private final int width;
    private final int height;

    /**
     * @param width  grid width in cells
     * @param height grid height in cells
     */
    public Obstacles(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Adds random obstacles on free cells, keeping a safe zone around the snake's head.
     *
     * @param amount number of obstacles to add
     * @param snake  the snake, whose cells are avoided
     * @param coin   the current coin, whose cell is avoided
     */
    public void spawn(int amount, Snake snake, Coin coin) {
        if (positions.size() > MAX_OBSTACLES) {
            return;
        }
        Random rnd = new Random();
        for (int i = 0; i < amount; i++) {
            Position p;
            do {
                p = new Position(rnd.nextInt(width), rnd.nextInt(height));
            } while (coin.getPosition().equals(p) || snake.occupies(p) || positions.contains(p)
                    || snake.getHead().distanceTo(p) < SAFE_DISTANCE);
            positions.add(p);
        }
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

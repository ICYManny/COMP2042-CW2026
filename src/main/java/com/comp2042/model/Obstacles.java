package com.comp2042.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The blocks on the board that kill the snake on contact.
 * <p>
 * There are two kinds: fixed walls that belong to the level layout, and spawned obstacles
 * added during play (by the silver coin). They are stored separately so restarting a
 * level can remove the spawned ones while keeping the walls, and so the spawn limit only
 * counts spawned obstacles. Renamed from {@code Obstacle} because one instance holds every
 * obstacle on the board.
 */
public class Obstacles {

    /** Upper limit so spawned obstacles can never fill the board. */
    public static final int MAX_OBSTACLES = 50;

    private final Set<Position> walls = new LinkedHashSet<>();
    private final Set<Position> spawned = new LinkedHashSet<>();

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
        while (added < amount && spawned.size() < MAX_OBSTACLES) {
            Optional<Position> cell = freeCells.find(forbidden.or(this::contains));
            if (cell.isEmpty()) {
                break;
            }
            spawned.add(cell.get());
            added++;
        }
        return added;
    }

    /**
     * Places a spawned obstacle on a specific cell.
     *
     * @param cell where to put the obstacle
     */
    public void add(Position cell) {
        spawned.add(cell);
    }

    /**
     * Replaces the level's fixed walls.
     *
     * @param layout the wall cells of the level
     */
    public void setWalls(Set<Position> layout) {
        walls.clear();
        walls.addAll(layout);
    }

    /**
     * @param cell a grid cell
     * @return true if a wall or spawned obstacle is on that cell
     */
    public boolean contains(Position cell) {
        return walls.contains(cell) || spawned.contains(cell);
    }

    /** @return a read-only view of the level's fixed walls */
    public Set<Position> getWalls() {
        return Collections.unmodifiableSet(walls);
    }

    /** @return a read-only view of the obstacles spawned during play */
    public Set<Position> getSpawned() {
        return Collections.unmodifiableSet(spawned);
    }

    /** @return a read-only set of every blocked cell, walls and spawned obstacles together */
    public Set<Position> getPositions() {
        Set<Position> all = new LinkedHashSet<>(walls);
        all.addAll(spawned);
        return Collections.unmodifiableSet(all);
    }

    /** @return total number of blocked cells */
    public int count() {
        return walls.size() + spawned.size();
    }

    /** @return number of obstacles spawned during play */
    public int spawnedCount() {
        return spawned.size();
    }

    /** Removes every wall and spawned obstacle. */
    public void clear() {
        walls.clear();
        spawned.clear();
    }
}

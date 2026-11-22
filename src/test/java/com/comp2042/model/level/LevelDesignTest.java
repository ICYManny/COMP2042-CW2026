package com.comp2042.model.level;

import com.comp2042.model.Direction;
import com.comp2042.model.Position;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks that every bundled level can actually be played to the end. */
class LevelDesignTest {

    private static final int SIZE = LevelLoader.LEVEL_SIZE;

    @Test
    void everyEmptyCellIsReachableFromTheStart() {
        // even with solid outer walls (Hard), no coin may spawn somewhere the snake cannot reach
        for (Level level : LevelLoader.loadDefaultLevels()) {
            Position start = new Position(SIZE / 2, SIZE / 2);
            Set<Position> reached = new HashSet<>();
            Deque<Position> toVisit = new ArrayDeque<>();
            reached.add(start);
            toVisit.add(start);
            while (!toVisit.isEmpty()) {
                Position cell = toVisit.poll();
                for (Direction d : Direction.values()) {
                    Position next = cell.step(d);
                    if (next.isInside(SIZE, SIZE) && !level.walls().contains(next) && reached.add(next)) {
                        toVisit.add(next);
                    }
                }
            }
            assertEquals(SIZE * SIZE - level.walls().size(), reached.size(), level + " has unreachable cells");
        }
    }

    @Test
    void thereIsRoomForEveryCoinTheLevelAsksFor() {
        for (Level level : LevelLoader.loadDefaultLevels()) {
            int free = SIZE * SIZE - level.walls().size();
            assertTrue(free > level.coinsToClear() + 10, level + " is too crowded");
        }
    }
}

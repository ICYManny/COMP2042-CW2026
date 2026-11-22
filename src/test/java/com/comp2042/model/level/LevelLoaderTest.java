package com.comp2042.model.level;

import com.comp2042.model.Position;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelLoaderTest {

    private static List<String> validFile() {
        List<String> lines = new ArrayList<>(List.of("# a comment", "name: Test", "coins: 5", "speed: 1.5"));
        lines.addAll(Collections.nCopies(20, "...................."));
        return lines;
    }

    @Test
    void parsesHeaderAndEmptyGrid() {
        Level level = LevelLoader.parse(3, validFile());
        assertEquals(3, level.number());
        assertEquals("Test", level.name());
        assertEquals(5, level.coinsToClear());
        assertEquals(1.5, level.speedMultiplier(), 1e-9);
        assertTrue(level.walls().isEmpty());
    }

    @Test
    void wallsAreReadAtTheRightCoordinates() {
        List<String> lines = validFile();
        lines.set(4, "#..................#");
        Level level = LevelLoader.parse(1, lines);
        assertEquals(2, level.walls().size());
        assertTrue(level.walls().contains(new Position(0, 0)));
        assertTrue(level.walls().contains(new Position(19, 0)));
    }

    @Test
    void wrongNumberOfRowsIsRejected() {
        List<String> lines = validFile();
        lines.remove(lines.size() - 1);
        assertThrows(IllegalArgumentException.class, () -> LevelLoader.parse(1, lines));
    }

    @Test
    void wrongRowWidthIsRejected() {
        List<String> lines = validFile();
        lines.set(6, "...");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> LevelLoader.parse(1, lines));
        assertTrue(error.getMessage().contains("row 3"), error.getMessage());
    }

    @Test
    void unknownCharacterIsRejected() {
        List<String> lines = validFile();
        lines.set(4, "....X...............");
        assertThrows(IllegalArgumentException.class, () -> LevelLoader.parse(1, lines));
    }

    @Test
    void missingHeaderIsRejected() {
        List<String> lines = validFile();
        lines.set(2, "points: 5");
        assertThrows(IllegalArgumentException.class, () -> LevelLoader.parse(1, lines));
    }

    @Test
    void nonNumericCoinsAreRejected() {
        List<String> lines = validFile();
        lines.set(2, "coins: many");
        assertThrows(IllegalArgumentException.class, () -> LevelLoader.parse(1, lines));
    }

    @Test
    void wallOnTheStartingCellsIsRejected() {
        List<String> lines = validFile();
        lines.set(4 + 10, "........#...........");
        assertThrows(IllegalArgumentException.class, () -> LevelLoader.parse(1, lines));
    }

    @Test
    void defaultLevelsLoadInOrder() {
        List<Level> levels = LevelLoader.loadDefaultLevels();
        assertTrue(levels.size() >= 5, "expected at least 5 levels, found " + levels.size());
        for (int i = 0; i < levels.size(); i++) {
            assertEquals(i + 1, levels.get(i).number());
            assertTrue(levels.get(i).coinsToClear() > 0);
        }
    }

    @Test
    void laterLevelsAreNotEasier() {
        List<Level> levels = LevelLoader.loadDefaultLevels();
        for (int i = 1; i < levels.size(); i++) {
            assertTrue(levels.get(i).coinsToClear() >= levels.get(i - 1).coinsToClear());
            assertTrue(levels.get(i).speedMultiplier() >= levels.get(i - 1).speedMultiplier());
        }
    }

    @Test
    void invalidLevelValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Level(0, "x", 1, 1, java.util.Set.of()));
        assertThrows(IllegalArgumentException.class, () -> new Level(1, "x", -1, 1, java.util.Set.of()));
        assertThrows(IllegalArgumentException.class, () -> new Level(1, "x", 1, 0, java.util.Set.of()));
    }

    @Test
    void endlessLevelNeverEnds() {
        assertTrue(Level.endless().isEndless());
        assertTrue(Level.endless().walls().isEmpty());
    }
}

package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameSettingsTest {

    @Test
    void tooSmallGridIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GameSettings(4, 20, false, Difficulty.HARD));
        assertThrows(IllegalArgumentException.class, () -> new GameSettings(20, 0, false, Difficulty.HARD));
    }

    @Test
    void missingDifficultyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GameSettings(20, 20, false, null));
    }

    @Test
    void startLevelBelowOneIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GameSettings(20, 20, false, Difficulty.HARD, 0));
    }

    @Test
    void shortConstructorStartsOnLevelOne() {
        org.junit.jupiter.api.Assertions.assertEquals(1, new GameSettings(20, 20, false, Difficulty.HARD).startLevel());
    }

    @Test
    void smallestAllowedGridIsAccepted() {
        assertDoesNotThrow(() -> new GameSettings(5, 5, true, Difficulty.HARD));
    }
}

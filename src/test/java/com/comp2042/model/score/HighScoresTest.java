package com.comp2042.model.score;

import com.comp2042.model.Difficulty;
import com.comp2042.model.GameMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighScoresTest {

    private HighScores highScores;

    @BeforeEach
    void setUp() {
        highScores = new HighScores(new InMemoryScoreStore());
    }

    @Test
    void unplayedCombinationHasNoBest() {
        assertEquals(0, highScores.best(GameMode.ENDLESS, Difficulty.HARD));
    }

    @Test
    void higherScoreBecomesTheBest() {
        assertTrue(highScores.submit(GameMode.ENDLESS, Difficulty.HARD, 40));
        assertEquals(40, highScores.best(GameMode.ENDLESS, Difficulty.HARD));
    }

    @Test
    void lowerOrEqualScoreDoesNotReplaceTheBest() {
        highScores.submit(GameMode.ENDLESS, Difficulty.HARD, 40);
        assertFalse(highScores.submit(GameMode.ENDLESS, Difficulty.HARD, 25));
        assertFalse(highScores.submit(GameMode.ENDLESS, Difficulty.HARD, 40));
        assertEquals(40, highScores.best(GameMode.ENDLESS, Difficulty.HARD));
    }

    @Test
    void zeroIsNeverARecord() {
        assertFalse(highScores.submit(GameMode.LEVELS, Difficulty.EASY, 0));
    }

    @Test
    void eachModeAndDifficultyHasItsOwnBest() {
        highScores.submit(GameMode.ENDLESS, Difficulty.HARD, 90);
        assertEquals(0, highScores.best(GameMode.ENDLESS, Difficulty.EASY));
        assertEquals(0, highScores.best(GameMode.LEVELS, Difficulty.HARD));
    }

    @Test
    void scoresSurviveANewHighScoresObjectOnTheSameStore() {
        ScoreStore store = new InMemoryScoreStore();
        new HighScores(store).submit(GameMode.LEVELS, Difficulty.MEDIUM, 55);
        assertEquals(55, new HighScores(store).best(GameMode.LEVELS, Difficulty.MEDIUM));
    }
}

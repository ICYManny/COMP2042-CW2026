package com.comp2042.model.score;

import com.comp2042.model.Difficulty;
import com.comp2042.model.GameMode;

/**
 * The best score for each combination of game mode and difficulty, so an Easy Endless
 * score never competes with a Hard Levels score.
 */
public class HighScores {

    private final ScoreStore store;

    /**
     * Creates the high score table.
     *
     * @param store where the scores are kept
     */
    public HighScores(ScoreStore store) {
        this.store = store;
    }

    /**
     * Returns the best score for a mode and difficulty.
     *
     * @param mode       the game mode
     * @param difficulty the difficulty
     * @return the best score so far, or 0 if the combination has never been played
     */
    public int best(GameMode mode, Difficulty difficulty) {
        return store.load(key(mode, difficulty));
    }

    /**
     * Records a finished score if it beats the saved best.
     *
     * @param mode       the game mode
     * @param difficulty the difficulty
     * @param score      the score reached
     * @return true if the score is a new best
     */
    public boolean submit(GameMode mode, Difficulty difficulty, int score) {
        if (score <= best(mode, difficulty)) {
            return false;
        }
        store.save(key(mode, difficulty), score);
        return true;
    }

    private static String key(GameMode mode, Difficulty difficulty) {
        return mode.name() + "_" + difficulty.name();
    }
}

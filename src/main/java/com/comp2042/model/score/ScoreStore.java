package com.comp2042.model.score;

/**
 * Somewhere high scores can be kept between runs of the game.
 * <p>
 * {@link HighScores} only talks to this interface, so the game saves through
 * {@link PreferencesScoreStore} while tests use {@link InMemoryScoreStore} and never touch
 * the player's real scores (Dependency Inversion).
 */
public interface ScoreStore {

    /**
     * Reads a saved score.
     *
     * @param key which score to read
     * @return the saved score, or 0 if none has been saved
     */
    int load(String key);

    /**
     * Saves a score, replacing any previous value for the key.
     *
     * @param key   which score to save
     * @param score the score
     */
    void save(String key, int score);
}

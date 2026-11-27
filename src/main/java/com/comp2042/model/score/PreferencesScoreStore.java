package com.comp2042.model.score;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Saves high scores with Java's built-in {@link Preferences}, which stores small values
 * per user (the registry on Windows, a file in the home folder on macOS and Linux). No
 * file path is hard-coded, and if saving is not allowed the scores are simply kept in
 * memory for this session instead of crashing the game.
 */
public class PreferencesScoreStore implements ScoreStore {

    private static final String NODE = "com/comp2042/snake/highscores";

    private final ScoreStore fallback = new InMemoryScoreStore();
    private Preferences preferences;

    /** Opens the high score storage for the current user. */
    public PreferencesScoreStore() {
        try {
            preferences = Preferences.userRoot().node(NODE);
        } catch (RuntimeException exception) {
            System.err.println("High scores will not be saved: " + exception.getMessage());
            preferences = null;
        }
    }

    @Override
    public int load(String key) {
        if (preferences == null) {
            return fallback.load(key);
        }
        try {
            return preferences.getInt(key, 0);
        } catch (RuntimeException exception) {
            return fallback.load(key);
        }
    }

    @Override
    public void save(String key, int score) {
        fallback.save(key, score);
        if (preferences == null) {
            return;
        }
        try {
            preferences.putInt(key, score);
            preferences.flush();
        } catch (RuntimeException | BackingStoreException exception) {
            System.err.println("Could not save high score: " + exception.getMessage());
        }
    }
}

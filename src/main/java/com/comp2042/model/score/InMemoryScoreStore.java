package com.comp2042.model.score;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps scores only while the program runs. Used by tests, and as a fallback when the
 * computer does not allow saving.
 */
public class InMemoryScoreStore implements ScoreStore {

    private final Map<String, Integer> scores = new HashMap<>();

    /** Creates an empty store. */
    public InMemoryScoreStore() {
    }

    @Override
    public int load(String key) {
        return scores.getOrDefault(key, 0);
    }

    @Override
    public void save(String key, int score) {
        scores.put(key, score);
    }
}

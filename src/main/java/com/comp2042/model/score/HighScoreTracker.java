package com.comp2042.model.score;

import com.comp2042.model.GameListener;
import com.comp2042.model.GameModel;
import com.comp2042.model.GameSettings;

/**
 * Follows one game's score against the saved best for its mode and difficulty.
 * <p>
 * It listens to the game (Observer pattern) and saves the score as soon as the game ends.
 * The controller also calls {@link #finishRun()} when the player restarts or leaves a game
 * part way through, so a good score is never lost.
 */
public class HighScoreTracker implements GameListener {

    private final GameModel model;
    private final HighScores highScores;
    private int bestBeforeRun;

    /**
     * Creates a tracker for a game and starts its first run.
     *
     * @param model      the game to follow
     * @param highScores the saved high scores
     */
    public HighScoreTracker(GameModel model, HighScores highScores) {
        this.model = model;
        this.highScores = highScores;
        startRun();
    }

    /** Remembers the saved best at the start of a run, to tell later whether it was beaten. */
    public final void startRun() {
        bestBeforeRun = savedBest();
    }

    /** Saves the current score if it is a new best. Safe to call more than once. */
    public void finishRun() {
        GameSettings settings = model.getSettings();
        highScores.submit(settings.mode(), settings.difficulty(), model.getScore());
    }

    /**
     * Saves the score when the game ends.
     *
     * @param won true if the player won
     */
    @Override
    public void onGameOver(boolean won) {
        finishRun();
    }

    /** {@return the best score to show: the saved best, or the current score once it is higher} */
    public int getBest() {
        return Math.max(savedBest(), model.getScore());
    }

    /** {@return true if this run's score has beaten the best saved before the run began} */
    public boolean isNewBest() {
        return model.getScore() > bestBeforeRun;
    }

    private int savedBest() {
        GameSettings settings = model.getSettings();
        return highScores.best(settings.mode(), settings.difficulty());
    }
}

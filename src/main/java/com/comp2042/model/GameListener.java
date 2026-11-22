package com.comp2042.model;

import com.comp2042.model.coin.Coin;
import com.comp2042.model.level.Level;

/**
 * Receives notifications when something important happens in a {@link GameModel}
 * (Observer pattern).
 * <p>
 * The model announces events without knowing who is listening, so controllers, sound or
 * future features (achievements, statistics) can react without the model depending on
 * them. Both methods have empty defaults so listeners only implement what they need.
 */
public interface GameListener {

    /**
     * Called after the snake eats a coin and its effect has been applied.
     *
     * @param coin the coin that was eaten
     */
    default void onCoinEaten(Coin coin) {
    }

    /**
     * Called when the coin target of a level is reached and another level follows.
     *
     * @param level the level that was just cleared
     */
    default void onLevelComplete(Level level) {
    }

    /**
     * Called once when the game ends.
     *
     * @param won true if the player won, false if the snake crashed
     */
    default void onGameOver(boolean won) {
    }
}

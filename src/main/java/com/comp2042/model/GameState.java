package com.comp2042.model;

/**
 * The stages a game moves through.
 * <pre>
 * READY --start--> PLAYING --pause--> PAUSED --start--> PLAYING
 *                  PLAYING --crash--> GAME_OVER
 *                  PLAYING --board full--> WON
 * any state --reset--> READY
 * </pre>
 * Keeping the state in one enum replaces separate flags (dead, a won check, the
 * controller's own running flag) that could disagree with each other.
 */
public enum GameState {
    /** Waiting for the first steering key. */
    READY,
    /** The snake is moving. */
    PLAYING,
    /** Frozen until the player resumes. */
    PAUSED,
    /** The snake crashed. */
    GAME_OVER,
    /** The player filled the board. */
    WON;

    /** @return true if the game has finished, won or lost */
    public boolean isFinished() {
        return this == GAME_OVER || this == WON;
    }
}

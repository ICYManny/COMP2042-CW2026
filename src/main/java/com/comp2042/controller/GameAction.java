package com.comp2042.controller;

/**
 * Commands the player can give during a game, other than steering.
 */
public enum GameAction {
    /** Pause or resume the game. */
    PAUSE,
    /** Start the game again from the beginning. */
    RESTART,
    /** Switch to fullscreen. */
    FULLSCREEN_ON,
    /** Leave fullscreen. */
    FULLSCREEN_OFF,
    /** Return to the main menu. */
    MENU,
    /** Move on from the current screen: next level, resume, or retry after game over. */
    CONTINUE
}

package com.comp2042.controller;

import com.comp2042.app.ViewManager;
import com.comp2042.model.Difficulty;
import com.comp2042.model.GameSettings;

/**
 * Handles the main menu buttons defined in MainMenu.fxml.
 */
public class MainMenuController {

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final boolean POWER_UPS = true;
    private static final Difficulty DIFFICULTY = Difficulty.NORMAL;

    private final ViewManager viewManager;

    /**
     * @param viewManager used to switch to the game screen
     */
    public MainMenuController(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    /** Starts a game. Called by the Start button. */
    public void handleStart() {
        viewManager.showGame(new GameSettings(GRID_WIDTH, GRID_HEIGHT, POWER_UPS, DIFFICULTY));
    }
}

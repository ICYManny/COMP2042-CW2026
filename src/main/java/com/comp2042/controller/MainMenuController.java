package com.comp2042.controller;

import com.comp2042.app.ViewManager;
import com.comp2042.model.Difficulty;
import com.comp2042.model.GameSettings;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 * Handles the main menu defined in MainMenu.fxml: reads the player's choices and starts
 * a game with them.
 */
public class MainMenuController {

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final boolean POWER_UPS = true;

    private final ViewManager viewManager;

    @FXML
    private ChoiceBox<Difficulty> difficultyChoice;

    @FXML
    private Label difficultyInfo;

    /**
     * @param viewManager used to switch to the game screen and to recall the last choices
     */
    public MainMenuController(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    /** Fills the menu controls. Called by the FXML loader after the fields are injected. */
    @FXML
    private void initialize() {
        difficultyChoice.getItems().setAll(Difficulty.values());
        difficultyChoice.valueProperty().addListener((observable, oldValue, newValue) -> showDescription(newValue));
        difficultyChoice.setValue(viewManager.getLastSettings().difficulty());
        showDescription(difficultyChoice.getValue());
    }

    private void showDescription(Difficulty difficulty) {
        difficultyInfo.setText(difficulty == null ? "" : difficulty.getDescription());
    }

    /** Starts a game with the chosen options. Called by the Start button. */
    @FXML
    private void handleStart() {
        viewManager.showGame(new GameSettings(GRID_WIDTH, GRID_HEIGHT, POWER_UPS, difficultyChoice.getValue()));
    }

    /** Closes the game. Called by the Quit button. */
    @FXML
    private void handleQuit() {
        Platform.exit();
    }
}

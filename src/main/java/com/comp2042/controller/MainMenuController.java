package com.comp2042.controller;

import com.comp2042.app.ViewManager;
import com.comp2042.model.Difficulty;
import com.comp2042.model.GameMode;
import com.comp2042.model.GameSettings;
import com.comp2042.model.level.Level;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 * Handles the main menu defined in MainMenu.fxml: reads the player's choices and starts
 * a game with them.
 */
public class MainMenuController {

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;

    private final ViewManager viewManager;

    @FXML
    private ChoiceBox<GameMode> modeChoice;

    @FXML
    private ChoiceBox<Difficulty> difficultyChoice;

    @FXML
    private Label bestScoresLabel;

    @FXML
    private Label difficultyInfo;

    @FXML
    private CheckBox powerUpsCheck;

    @FXML
    private ChoiceBox<Level> levelChoice;

    /**
     * Creates a MainMenuController.
     *
     * @param viewManager used to switch to the game screen and to recall the last choices
     */
    public MainMenuController(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    /** Fills the menu controls. Called by the FXML loader after the fields are injected. */
    @FXML
    private void initialize() {
        GameSettings last = viewManager.getLastSettings();
        difficultyChoice.getItems().setAll(Difficulty.values());
        difficultyChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            showDescription(newValue);
            showBestScores(newValue);
        });
        difficultyChoice.setValue(last.difficulty());
        showDescription(difficultyChoice.getValue());
        showBestScores(difficultyChoice.getValue());
        powerUpsCheck.setSelected(viewManager.getLastSettings().powerUpsEnabled());
        levelChoice.getItems().setAll(viewManager.getLevels());
        int lastLevel = Math.min(viewManager.getLastSettings().startLevel(), viewManager.getLevels().size());
        levelChoice.setValue(viewManager.getLevels().get(lastLevel - 1));

        modeChoice.getItems().setAll(GameMode.values());
        // levels only matter in Levels mode
        modeChoice.valueProperty().addListener((observable, oldValue, newValue) ->
                levelChoice.setDisable(newValue == GameMode.ENDLESS));
        modeChoice.setValue(last.mode());
        levelChoice.setDisable(last.mode() == GameMode.ENDLESS);
    }

    /** Shows the best score of every game mode for the chosen difficulty, e.g. "Best (Medium): Levels 120   Endless 85". */
    private void showBestScores(Difficulty difficulty) {
        if (difficulty == null) {
            bestScoresLabel.setText("");
            return;
        }
        StringBuilder text = new StringBuilder("Best (" + difficulty + "):");
        for (GameMode mode : GameMode.values()) {
            text.append("   ").append(mode).append(' ').append(viewManager.getHighScores().best(mode, difficulty));
        }
        bestScoresLabel.setText(text.toString());
    }

    private void showDescription(Difficulty difficulty) {
        difficultyInfo.setText(difficulty == null ? "" : difficulty.getDescription());
    }

    /** Starts a game with the chosen options. Called by the Start button. */
    @FXML
    private void handleStart() {
        viewManager.showGame(new GameSettings(GRID_WIDTH, GRID_HEIGHT, powerUpsCheck.isSelected(),
                difficultyChoice.getValue(), levelChoice.getValue().number(), modeChoice.getValue()));
    }

    /** Closes the game. Called by the Quit button. */
    @FXML
    private void handleQuit() {
        Platform.exit();
    }
}

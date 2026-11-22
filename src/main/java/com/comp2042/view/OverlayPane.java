package com.comp2042.view;

import com.comp2042.model.GameModel;
import com.comp2042.model.level.Level;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * The message panel shown over the board when the game is not running: a start prompt,
 * the pause menu, or the end-of-game screen with Retry and Main Menu buttons.
 */
public class OverlayPane {

    private static final String PANEL_STYLE = "-fx-background-color: rgba(254, 255, 228, 0.88);"
            + "-fx-background-radius: 8; -fx-border-color: #2e7d32; -fx-border-width: 2; -fx-border-radius: 8;"
            + "-fx-padding: 16;";

    private final VBox panel = new VBox(10);
    private final Label title = new Label();
    private final Label detail = new Label();
    private final Button retryButton = makeButton("Retry");
    private final Button resumeButton = makeButton("Resume");
    private final Button nextButton = makeButton("Next");
    private final Button menuButton = makeButton("Menu");
    private final HBox buttons = new HBox(12);

    /** Builds the panel, hidden until {@link #show(GameModel)} is called. */
    public OverlayPane() {
        title.setId("overlayTitle");
        detail.setId("overlayDetail");
        buttons.setAlignment(Pos.CENTER);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle(PANEL_STYLE);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        panel.getChildren().addAll(title, detail, buttons);
        panel.setVisible(false);
    }

    /**
     * A button that does not take keyboard focus. Focused buttons consume Space, Enter and
     * Tab, which would stop those keys reaching the game.
     */
    private static Button makeButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("startAndQuitBtn");
        button.setFocusTraversable(false);
        button.setPrefSize(115, 40);
        return button;
    }

    /**
     * Sets what the buttons do.
     *
     * @param onRetry     starts a new game
     * @param onResume    resumes a paused game
     * @param onNextLevel starts the next level
     * @param onMenu      returns to the main menu
     */
    public void setActions(Runnable onRetry, Runnable onResume, Runnable onNextLevel, Runnable onMenu) {
        retryButton.setOnAction(event -> onRetry.run());
        resumeButton.setOnAction(event -> onResume.run());
        nextButton.setOnAction(event -> onNextLevel.run());
        menuButton.setOnAction(event -> onMenu.run());
    }

    /**
     * Shows the right message for the game's state, or hides the panel while playing.
     *
     * @param model the game to describe
     */
    public void show(GameModel model) {
        Level level = model.getLevel();
        String score = "Score: " + model.getScore();
        switch (model.getState()) {
            case READY -> display(level.isEndless() ? "Ready?" : "Level " + level.number() + ": " + level.name(),
                    level.isEndless() ? "Press W, A, S or D to start"
                            : "Eat " + level.coinsToClear() + " coins. Press W, A, S or D to start");
            case PAUSED -> display("Paused", score + "   (Enter or P to resume)", resumeButton, menuButton);
            case LEVEL_COMPLETE -> display("Level Clear!", score + "   (Enter for the next level)", nextButton, menuButton);
            case GAME_OVER -> display("Game Over", score + "   (Enter to retry)", retryButton, menuButton);
            case WON -> display("You Won!", score, retryButton, menuButton);
            default -> panel.setVisible(false);
        }
    }

    private void display(String heading, String message, Node... actions) {
        title.setText(heading);
        detail.setText(message);
        buttons.getChildren().setAll(actions);
        buttons.setVisible(actions.length > 0);
        buttons.setManaged(actions.length > 0);
        panel.setVisible(true);
    }

    /** @return the node to place over the board */
    public Node getNode() {
        return panel;
    }
}

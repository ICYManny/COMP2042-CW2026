package com.comp2042.controller;

import com.comp2042.app.Main;
import com.comp2042.model.Direction;
import com.comp2042.model.GameListener;
import com.comp2042.model.GameModel;
import com.comp2042.view.GameView;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Connects keyboard input and the game clock to the model and view.
 * <p>
 * The controller holds no game rules: it turns key presses into model calls, asks the
 * model to advance on every tick of the timeline, and tells the view to redraw.
 */
public class GameController {

    private final GameModel model;
    private final GameView view;
    private final Stage stage;
    private final Timeline timeline;

    /**
     * @param model the game being played
     * @param view  the view drawing that game
     * @param stage the window showing the game, used for fullscreen
     */
    public GameController(GameModel model, GameView view, Stage stage) {
        this.model = model;
        this.view = view;
        this.stage = stage;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> step()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setRate(model.getSpeed());
        model.addListener(new GameListener() {
            @Override
            public void onGameOver(boolean won) {
                timeline.stop();
            }
        });
    }

    private void step() {
        model.tick();
        timeline.setRate(model.getSpeed());
        view.render();
    }

    /**
     * Handles a key press in the game window.
     *
     * @param event the key event
     */
    public void handle(KeyEvent event) {
        KeyCode code = event.getCode();
        Direction direction = directionFor(code);
        if (direction != null) {
            model.turn(direction);
            if (!model.isOver()) {
                timeline.play();
            }
        } else if (code == KeyCode.P) {
            timeline.stop();
        } else if (code == KeyCode.TAB) {
            reset();
        } else if (code == KeyCode.PLUS || code == KeyCode.EQUALS || code == KeyCode.ADD) {
            setFullScreen(true);
        } else if (code == KeyCode.MINUS || code == KeyCode.SUBTRACT) {
            setFullScreen(false);
        } else if (code == KeyCode.ESCAPE) {
            returnToMenu();
        }
    }

    private Direction directionFor(KeyCode code) {
        return switch (code) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
            default -> null;
        };
    }

    private void reset() {
        timeline.stop();
        model.reset();
        timeline.setRate(model.getSpeed());
        view.render();
    }

    private void setFullScreen(boolean fullScreen) {
        stage.setFullScreen(fullScreen);
        view.setFullScreen(fullScreen);
    }

    private void returnToMenu() {
        timeline.stop();
        Main.getViewManager().getPrimaryStage().show();
        Main.getViewManager().getGameStage().close();
    }
}

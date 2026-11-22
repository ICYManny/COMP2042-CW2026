package com.comp2042.controller;

import com.comp2042.model.Direction;
import com.comp2042.model.GameModel;
import com.comp2042.view.GameView;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Connects keyboard input and the game clock to the model and view.
 * <p>
 * The controller holds no game rules: it turns key presses into model calls, feeds the
 * model the time that has passed on every frame of the {@link GameLoop}, and tells the
 * view to redraw.
 */
public class GameController {

    private final GameModel model;
    private final GameView view;
    private final Stage stage;
    private final Runnable exitToMenu;
    private final KeyBindings keyBindings = KeyBindings.defaults();
    private final GameLoop loop;

    /**
     * Creates the controller and starts drawing the game. The snake waits for the first
     * steering key before it moves.
     *
     * @param model      the game being played
     * @param view       the view drawing that game
     * @param stage      the window showing the game, used for fullscreen
     * @param exitToMenu action that shows the main menu
     */
    public GameController(GameModel model, GameView view, Stage stage, Runnable exitToMenu) {
        this.model = model;
        this.view = view;
        this.stage = stage;
        this.exitToMenu = exitToMenu;
        view.setOverlayActions(this::reset, model::start, model::nextLevel, this::returnToMenu);
        loop = new GameLoop(this::frame);
        loop.start();
    }

    private void frame(double elapsedSeconds) {
        model.update(elapsedSeconds);
        view.render();
    }

    /**
     * Handles a key press in the game window: steering keys turn the snake (and start the
     * game on the first press), other bound keys trigger their action.
     *
     * @param event the key event
     */
    public void handle(KeyEvent event) {
        KeyCode code = event.getCode();
        keyBindings.directionFor(code).ifPresent(this::steer);
        keyBindings.actionFor(code).ifPresent(this::perform);
    }

    private void steer(Direction direction) {
        model.turn(direction);
        model.start();
    }

    private void perform(GameAction action) {
        switch (action) {
            case PAUSE -> model.togglePause();
            case RESTART -> reset();
            case FULLSCREEN_ON -> setFullScreen(true);
            case FULLSCREEN_OFF -> setFullScreen(false);
            case MENU -> returnToMenu();
            case CONTINUE -> continueGame();
        }
    }

    /** Enter moves the game on from whichever screen is showing. */
    private void continueGame() {
        switch (model.getState()) {
            case LEVEL_COMPLETE -> model.nextLevel();
            case PAUSED -> model.start();
            case GAME_OVER, WON -> reset();
            default -> {
                // nothing to continue while ready or playing
            }
        }
    }

    private void reset() {
        model.reset();
    }

    private void setFullScreen(boolean fullScreen) {
        stage.setFullScreen(fullScreen);
        view.setFullScreen(fullScreen);
    }

    private void returnToMenu() {
        loop.stop();
        exitToMenu.run();
    }
}

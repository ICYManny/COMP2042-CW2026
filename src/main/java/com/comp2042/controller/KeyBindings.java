package com.comp2042.controller;

import com.comp2042.model.Direction;

import javafx.scene.input.KeyCode;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Maps keyboard keys to snake directions and game actions.
 * <p>
 * Keeping every control in one table means changing the controls (for example from the
 * arrow keys to WASD) is a one-line change here, instead of editing switch statements in
 * the controller.
 */
public class KeyBindings {

    private final Map<KeyCode, Direction> directions = new EnumMap<>(KeyCode.class);
    private final Map<KeyCode, GameAction> actions = new EnumMap<>(KeyCode.class);

    /**
     * Creates the default controls: W A S D to steer, P to pause, R to restart,
     * + / - to enter and leave fullscreen, Escape for the menu, Enter to continue.
     *
     * @return the default key bindings
     */
    public static KeyBindings defaults() {
        KeyBindings bindings = new KeyBindings();
        bindings.bind(KeyCode.W, Direction.UP);
        bindings.bind(KeyCode.A, Direction.LEFT);
        bindings.bind(KeyCode.S, Direction.DOWN);
        bindings.bind(KeyCode.D, Direction.RIGHT);
        bindings.bind(KeyCode.P, GameAction.PAUSE);
        bindings.bind(KeyCode.R, GameAction.RESTART);
        bindings.bind(KeyCode.PLUS, GameAction.FULLSCREEN_ON);
        bindings.bind(KeyCode.EQUALS, GameAction.FULLSCREEN_ON);
        bindings.bind(KeyCode.ADD, GameAction.FULLSCREEN_ON);
        bindings.bind(KeyCode.MINUS, GameAction.FULLSCREEN_OFF);
        bindings.bind(KeyCode.SUBTRACT, GameAction.FULLSCREEN_OFF);
        bindings.bind(KeyCode.ESCAPE, GameAction.MENU);
        bindings.bind(KeyCode.ENTER, GameAction.CONTINUE);
        return bindings;
    }

    /**
     * Makes a key steer the snake. Replaces any previous binding for that key.
     *
     * @param key       the key
     * @param direction direction the key steers in
     */
    public void bind(KeyCode key, Direction direction) {
        actions.remove(key);
        directions.put(key, direction);
    }

    /**
     * Makes a key trigger an action. Replaces any previous binding for that key.
     *
     * @param key    the key
     * @param action action the key triggers
     */
    public void bind(KeyCode key, GameAction action) {
        directions.remove(key);
        actions.put(key, action);
    }

    /**
     * @param key a pressed key
     * @return the direction bound to the key, if any
     */
    public Optional<Direction> directionFor(KeyCode key) {
        return Optional.ofNullable(directions.get(key));
    }

    /**
     * @param key a pressed key
     * @return the action bound to the key, if any
     */
    public Optional<GameAction> actionFor(KeyCode key) {
        return Optional.ofNullable(actions.get(key));
    }
}

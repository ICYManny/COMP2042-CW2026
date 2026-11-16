package com.comp2042.controller;

import com.comp2042.model.Direction;

import javafx.scene.input.KeyCode;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyBindingsTest {

    private final KeyBindings bindings = KeyBindings.defaults();

    @Test
    void wasdSteersTheSnake() {
        assertEquals(Optional.of(Direction.UP), bindings.directionFor(KeyCode.W));
        assertEquals(Optional.of(Direction.LEFT), bindings.directionFor(KeyCode.A));
        assertEquals(Optional.of(Direction.DOWN), bindings.directionFor(KeyCode.S));
        assertEquals(Optional.of(Direction.RIGHT), bindings.directionFor(KeyCode.D));
    }

    @Test
    void arrowKeysNoLongerSteer() {
        assertTrue(bindings.directionFor(KeyCode.UP).isEmpty());
        assertTrue(bindings.directionFor(KeyCode.LEFT).isEmpty());
    }

    @Test
    void actionKeys() {
        assertEquals(Optional.of(GameAction.PAUSE), bindings.actionFor(KeyCode.P));
        assertEquals(Optional.of(GameAction.RESTART), bindings.actionFor(KeyCode.R));
        assertEquals(Optional.of(GameAction.MENU), bindings.actionFor(KeyCode.ESCAPE));
        assertEquals(Optional.of(GameAction.FULLSCREEN_ON), bindings.actionFor(KeyCode.EQUALS));
        assertEquals(Optional.of(GameAction.FULLSCREEN_OFF), bindings.actionFor(KeyCode.MINUS));
    }

    @Test
    void steeringKeysHaveNoActionAndViceVersa() {
        assertTrue(bindings.actionFor(KeyCode.W).isEmpty());
        assertTrue(bindings.directionFor(KeyCode.P).isEmpty());
    }

    @Test
    void rebindingAKeyReplacesItsOldMeaning() {
        bindings.bind(KeyCode.P, Direction.UP);
        assertEquals(Optional.of(Direction.UP), bindings.directionFor(KeyCode.P));
        assertTrue(bindings.actionFor(KeyCode.P).isEmpty());
    }

    @Test
    void unboundKeyDoesNothing() {
        assertTrue(bindings.directionFor(KeyCode.TAB).isEmpty());
        assertTrue(bindings.actionFor(KeyCode.TAB).isEmpty());
    }
}

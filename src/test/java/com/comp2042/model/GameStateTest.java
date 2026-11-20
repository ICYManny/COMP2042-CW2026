package com.comp2042.model;

import com.comp2042.model.coin.BasicCoin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateTest {

    private GameModel game;

    @BeforeEach
    void setUp() {
        game = new GameModel(new GameSettings(20, 20, false, Difficulty.HARD), new Random(2));
        game.placeCoin(new BasicCoin(new Position(0, 0)));
    }

    @Test
    void newGameIsReady() {
        assertEquals(GameState.READY, game.getState());
    }

    @Test
    void startBeginsPlaying() {
        game.start();
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void pauseAndResume() {
        game.start();
        game.togglePause();
        assertEquals(GameState.PAUSED, game.getState());
        game.togglePause();
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void startAlsoResumesFromPause() {
        game.start();
        game.togglePause();
        game.start();
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void cannotPauseBeforeStarting() {
        game.togglePause();
        assertEquals(GameState.READY, game.getState());
    }

    @Test
    void crashingEndsTheGame() {
        game.start();
        for (int i = 0; i < 11; i++) {
            game.tick();
        }
        assertEquals(GameState.GAME_OVER, game.getState());
        assertTrue(game.getState().isFinished());
    }

    @Test
    void finishedGameCannotBeRestartedOrPausedWithoutReset() {
        for (int i = 0; i < 11; i++) {
            game.tick();
        }
        game.start();
        game.togglePause();
        assertEquals(GameState.GAME_OVER, game.getState());
    }

    @Test
    void resetReturnsToReady() {
        for (int i = 0; i < 11; i++) {
            game.tick();
        }
        game.reset();
        assertEquals(GameState.READY, game.getState());
        assertFalse(game.isOver());
    }
}

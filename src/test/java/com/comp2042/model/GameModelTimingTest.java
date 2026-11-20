package com.comp2042.model;

import com.comp2042.model.coin.BasicCoin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameModelTimingTest {

    private GameModel game;
    private double stepTime;

    @BeforeEach
    void setUp() {
        game = new GameModel(new GameSettings(20, 20, false, Difficulty.EASY), new Random(4));
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        stepTime = 1.0 / game.getSpeed();
        game.start();
    }

    @Test
    void updateDoesNothingBeforeTheGameStarts() {
        GameModel waiting = new GameModel(new GameSettings(20, 20, false, Difficulty.EASY), new Random(4));
        waiting.update(5);
        assertEquals(new Position(10, 10), waiting.getSnake().getHead());
    }

    @Test
    void updateDoesNothingWhilePaused() {
        game.togglePause();
        game.update(stepTime * 3);
        assertEquals(new Position(10, 10), game.getSnake().getHead());
    }

    @Test
    void oneStepTimeMovesExactlyOneCell() {
        game.update(stepTime * 1.01);
        assertEquals(new Position(9, 10), game.getSnake().getHead());
    }

    @Test
    void smallUpdatesAddUp() {
        for (int i = 0; i < 10; i++) {
            game.update(stepTime / 10 * 1.001);
        }
        assertEquals(new Position(9, 10), game.getSnake().getHead());
    }

    @Test
    void lessThanOneStepDoesNotMove() {
        game.update(stepTime * 0.5);
        assertEquals(new Position(10, 10), game.getSnake().getHead());
        assertEquals(0.5, game.getStepProgress(), 1e-6);
    }

    @Test
    void progressStaysBetweenZeroAndOne() {
        for (int i = 0; i < 100; i++) {
            game.update(0.013);
            double progress = game.getStepProgress();
            assertTrue(progress >= 0 && progress <= 1, "progress was " + progress);
        }
    }

    @Test
    void longStallIsCapped() {
        game.update(10);
        int moved = 10 - game.getSnake().getHead().x();
        assertTrue(moved <= Math.ceil(0.25 * game.getSpeed()), "moved " + moved + " cells");
    }

    @Test
    void zeroOrNegativeTimeChangesNothing() {
        game.update(0);
        game.update(-1);
        assertEquals(new Position(10, 10), game.getSnake().getHead());
        assertEquals(0, game.getStepProgress(), 1e-9);
    }
}

package com.comp2042.model;

import com.comp2042.model.coin.BasicCoin;
import com.comp2042.model.coin.ShrinkCoin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** How power-up effects change the rules of a running game. */
class PowerUpTest {

    private GameModel game;

    @BeforeEach
    void setUp() {
        game = new GameModel(new GameSettings(20, 20, true, Difficulty.HARD), new Random(5));
        game.placeCoin(new BasicCoin(new Position(0, 0)));
    }

    @Test
    void ghostPassesThroughObstacles() {
        game.getObstacles().add(new Position(9, 10));
        game.activateEffect(Effect.GHOST);
        game.tick();
        assertFalse(game.isDead());
        assertEquals(new Position(9, 10), game.getSnake().getHead());
    }

    @Test
    void ghostPassesThroughSolidWalls() {
        game.activateEffect(Effect.GHOST);
        assertEquals(WallMode.WRAP_ALL, game.getWallMode());
        for (int i = 0; i < 11; i++) {
            game.tick();
        }
        assertFalse(game.isDead());
    }

    @Test
    void ghostWearsOffDuringPlay() {
        game.activateEffect(Effect.GHOST);
        game.start();
        double time = 0;
        while (time < Effect.GHOST.getDurationSeconds() + 0.1) {
            game.update(0.05);
            time += 0.05;
        }
        assertFalse(game.isEffectActive(Effect.GHOST));
    }

    @Test
    void effectsDoNotWearOffWhilePaused() {
        game.activateEffect(Effect.GHOST);
        game.start();
        game.togglePause();
        game.update(0.2);
        assertEquals(Effect.GHOST.getDurationSeconds(), game.getActiveEffects().get(Effect.GHOST), 1e-9);
    }

    @Test
    void doublePointsDoublesTheScore() {
        game.activateEffect(Effect.DOUBLE_POINTS);
        game.placeCoin(new BasicCoin(new Position(9, 10)));
        game.tick();
        assertEquals(10, game.getScore());
    }

    @Test
    void slowReducesSpeedAndExpires() {
        double normal = game.getSpeed();
        game.activateEffect(Effect.SLOW);
        assertEquals(normal * 0.6, game.getSpeed(), 1e-9);
    }

    @Test
    void shrinkKeepsTheScore() {
        for (int x = 9; x >= 6; x--) {
            game.placeCoin(new BasicCoin(new Position(x, 10)));
            game.tick();
        }
        int scoreBefore = game.getScore();
        int lengthBefore = game.getSnake().length();
        game.placeCoin(new ShrinkCoin(new Position(5, 10)));
        game.tick();
        assertEquals(lengthBefore + 1 - 3, game.getSnake().length());
        assertEquals(scoreBefore + 5, game.getScore());
    }

    @Test
    void resetClearsEffects() {
        game.activateEffect(Effect.SLOW);
        game.reset();
        assertTrue(game.getActiveEffects().isEmpty());
    }
}

package com.comp2042.model;

import com.comp2042.model.coin.BasicCoin;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DifficultyTest {

    private static GameModel play(Difficulty difficulty, Direction direction, int steps) {
        GameModel game = new GameModel(new GameSettings(20, 20, false, difficulty), new Random(6));
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        game.turn(direction);
        for (int i = 0; i < steps; i++) {
            game.tick();
        }
        return game;
    }

    @Test
    void harderDifficultiesAreFaster() {
        assertTrue(Difficulty.EASY.getSpeed() < Difficulty.MEDIUM.getSpeed());
        assertTrue(Difficulty.MEDIUM.getSpeed() < Difficulty.HARD.getSpeed());
    }

    @Test
    void eachDifficultyHasItsWallRule() {
        assertEquals(WallMode.WRAP_ALL, Difficulty.EASY.getWallMode());
        assertEquals(WallMode.WRAP_SIDES, Difficulty.MEDIUM.getWallMode());
        assertEquals(WallMode.SOLID, Difficulty.HARD.getWallMode());
    }

    @Test
    void easySurvivesTheTopWall() {
        assertFalse(play(Difficulty.EASY, Direction.UP, 12).isDead());
    }

    @Test
    void mediumWrapsThroughTheSideWall() {
        GameModel game = play(Difficulty.MEDIUM, Direction.LEFT, 12);
        assertFalse(game.isDead());
        assertEquals(new Position(18, 10), game.getSnake().getHead());
    }

    @Test
    void mediumDiesAtTheTopWall() {
        assertTrue(play(Difficulty.MEDIUM, Direction.UP, 11).isDead());
    }

    @Test
    void hardDiesAtTheSideWall() {
        assertTrue(play(Difficulty.HARD, Direction.LEFT, 11).isDead());
    }

    @Test
    void menuNamesAreReadable() {
        assertEquals("Easy", Difficulty.EASY.toString());
        assertEquals("Hard", Difficulty.HARD.toString());
    }

    @Test
    void gameStartsAtTheDifficultySpeed() {
        GameModel game = new GameModel(new GameSettings(20, 20, false, Difficulty.HARD));
        assertEquals(Difficulty.HARD.getSpeed(), game.getSpeed(), 1e-9);
    }
}

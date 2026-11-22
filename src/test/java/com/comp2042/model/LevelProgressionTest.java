package com.comp2042.model;

import com.comp2042.model.coin.BasicCoin;
import com.comp2042.model.level.Level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelProgressionTest {

    private static final Position WALL = new Position(3, 3);
    private static final List<Level> LEVELS = List.of(
            new Level(1, "One", 2, 1.0, Set.of()),
            new Level(2, "Two", 2, 1.5, Set.of(WALL)));

    private GameModel game;

    @BeforeEach
    void setUp() {
        game = new GameModel(new GameSettings(20, 20, false, Difficulty.HARD), new Random(1), LEVELS);
    }

    /** Places a coin directly in front of the snake and steps onto it. */
    private void eatCoinAhead() {
        game.placeCoin(new BasicCoin(game.getSnake().nextHead()));
        game.tick();
    }

    @Test
    void startsOnTheFirstLevel() {
        assertEquals(1, game.getLevel().number());
        assertEquals(2, game.getLevelCount());
        assertTrue(game.hasNextLevel());
    }

    @Test
    void reachingTheCoinTargetCompletesTheLevel() {
        eatCoinAhead();
        assertEquals(1, game.getCoinsThisLevel());
        assertFalse(game.getState() == GameState.LEVEL_COMPLETE);
        eatCoinAhead();
        assertEquals(GameState.LEVEL_COMPLETE, game.getState());
        assertNull(game.getCoin());
        assertFalse(game.isOver());
    }

    @Test
    void snakeDoesNotMoveWhileLevelIsComplete() {
        eatCoinAhead();
        eatCoinAhead();
        Position head = game.getSnake().getHead();
        game.tick();
        game.start();
        game.update(1);
        assertEquals(head, game.getSnake().getHead());
    }

    @Test
    void nextLevelLoadsItsWallsAndKeepsTheScore() {
        eatCoinAhead();
        eatCoinAhead();
        int score = game.getScore();
        game.nextLevel();
        assertEquals(2, game.getLevel().number());
        assertEquals(GameState.READY, game.getState());
        assertEquals(score, game.getScore());
        assertEquals(0, game.getCoinsThisLevel());
        assertEquals(2, game.getSnake().length());
        assertTrue(game.getObstacles().contains(WALL));
    }

    @Test
    void levelSpeedMultiplierApplies() {
        double firstLevelSpeed = game.getSpeed();
        eatCoinAhead();
        eatCoinAhead();
        game.nextLevel();
        assertEquals(firstLevelSpeed * 1.5, game.getSpeed(), 1e-9);
    }

    @Test
    void clearingTheLastLevelWins() {
        List<Boolean> endings = new ArrayList<>();
        List<Level> cleared = new ArrayList<>();
        game.addListener(new GameListener() {
            @Override
            public void onLevelComplete(Level level) {
                cleared.add(level);
            }

            @Override
            public void onGameOver(boolean won) {
                endings.add(won);
            }
        });
        eatCoinAhead();
        eatCoinAhead();
        game.nextLevel();
        eatCoinAhead();
        eatCoinAhead();
        assertEquals(GameState.WON, game.getState());
        assertEquals(List.of(LEVELS.get(0)), cleared);
        assertEquals(List.of(true), endings);
    }

    @Test
    void nextLevelIsIgnoredUnlessALevelWasCompleted() {
        game.nextLevel();
        assertEquals(1, game.getLevel().number());
    }

    @Test
    void resetGoesBackToTheStartLevelWithNoScore() {
        eatCoinAhead();
        eatCoinAhead();
        game.nextLevel();
        game.reset();
        assertEquals(1, game.getLevel().number());
        assertEquals(0, game.getScore());
        assertFalse(game.getObstacles().contains(WALL));
    }

    @Test
    void canStartOnALaterLevel() {
        GameModel later = new GameModel(new GameSettings(20, 20, false, Difficulty.HARD, 2), new Random(1), LEVELS);
        assertEquals(2, later.getLevel().number());
        assertFalse(later.hasNextLevel());
    }

    @Test
    void startLevelBeyondTheListIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameModel(new GameSettings(20, 20, false, Difficulty.HARD, 3), new Random(1), LEVELS));
    }

    @Test
    void levelThatDoesNotFitTheGridIsRejected() {
        List<Level> big = List.of(new Level(1, "Big", 1, 1, Set.of(new Position(30, 30))));
        assertThrows(IllegalArgumentException.class,
                () -> new GameModel(new GameSettings(20, 20, false, Difficulty.HARD), new Random(1), big));
    }

    @Test
    void endlessGameNeverCompletesALevel() {
        GameModel endless = new GameModel(new GameSettings(20, 20, false, Difficulty.EASY), new Random(1));
        for (int i = 0; i < 30; i++) {
            endless.placeCoin(new BasicCoin(endless.getSnake().nextHead()));
            endless.tick();
            if (i % 5 == 4) {
                endless.turn(i % 10 == 4 ? Direction.UP : Direction.LEFT);
            }
        }
        assertFalse(endless.getState() == GameState.LEVEL_COMPLETE);
    }

    @Test
    void wallsKillTheSnake() {
        GameModel walled = new GameModel(new GameSettings(20, 20, false, Difficulty.EASY), new Random(1),
                List.of(new Level(1, "Wall", 5, 1, Set.of(new Position(6, 10)))));
        walled.placeCoin(new BasicCoin(new Position(0, 0)));
        for (int i = 0; i < 4; i++) {
            walled.tick();
        }
        assertTrue(walled.isDead());
    }
}

package com.comp2042.model;

import com.comp2042.model.coin.BasicCoin;
import com.comp2042.model.coin.Coin;
import com.comp2042.model.coin.GoldCoin;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameModelTest {

    private static GameModel newGame(Difficulty difficulty) {
        return new GameModel(new GameSettings(20, 20, false, difficulty), new Random(42));
    }

    private static void tick(GameModel game, int times) {
        for (int i = 0; i < times; i++) {
            game.tick();
        }
    }

    @Test
    void newGameStartsInTheCentreFacingLeft() {
        GameModel game = newGame(Difficulty.NORMAL);
        assertEquals(new Position(10, 10), game.getSnake().getHead());
        assertEquals(Direction.LEFT, game.getSnake().getDirection());
        assertEquals(0, game.getScore());
        assertFalse(game.isOver());
    }

    @Test
    void firstCoinIsNeverOnTheSnake() {
        for (int seed = 0; seed < 50; seed++) {
            GameModel game = new GameModel(new GameSettings(20, 20, false, Difficulty.NORMAL), new Random(seed));
            assertFalse(game.getSnake().occupies(game.getCoin().getPosition()));
        }
    }

    @Test
    void tickMovesTheSnakeOneCell() {
        GameModel game = newGame(Difficulty.NORMAL);
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        game.tick();
        assertEquals(new Position(9, 10), game.getSnake().getHead());
    }

    @Test
    void hittingAWallKillsTheSnakeWhenWallsAreSolid() {
        GameModel game = newGame(Difficulty.NORMAL);
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        tick(game, 11);
        assertTrue(game.isDead());
        assertEquals(new Position(0, 10), game.getSnake().getHead());
    }

    @Test
    void snakeWrapsAroundOnEasy() {
        GameModel game = newGame(Difficulty.EASY);
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        tick(game, 11);
        assertFalse(game.isDead());
        assertEquals(new Position(19, 10), game.getSnake().getHead());
    }

    @Test
    void eatingACoinAddsPointsAndGrowsTheSnake() {
        GameModel game = newGame(Difficulty.NORMAL);
        game.placeCoin(new GoldCoin(new Position(9, 10)));
        game.tick();
        assertEquals(15, game.getScore());
        assertEquals(1, game.getCoinsEaten());
        assertEquals(3, game.getSnake().length());
        assertNotNull(game.getCoin());
        assertFalse(game.getSnake().occupies(game.getCoin().getPosition()));
    }

    @Test
    void hittingAnObstacleKillsTheSnake() {
        GameModel game = newGame(Difficulty.EASY);
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        game.getObstacles().add(new Position(9, 10));
        game.tick();
        assertTrue(game.isDead());
    }

    @Test
    void wrappingOntoAnObstacleKillsTheSnake() {
        GameModel game = newGame(Difficulty.EASY);
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        game.getObstacles().add(new Position(19, 10));
        tick(game, 11);
        assertTrue(game.isDead());
    }

    @Test
    void runningIntoItselfKillsTheSnake() {
        GameModel game = newGame(Difficulty.NORMAL);
        // grow to length 5 by eating coins placed in its path
        for (int x = 9; x >= 7; x--) {
            game.placeCoin(new BasicCoin(new Position(x, 10)));
            game.tick();
        }
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        game.turn(Direction.UP);
        game.tick();
        game.turn(Direction.RIGHT);
        game.tick();
        game.turn(Direction.DOWN);
        game.tick();
        assertTrue(game.isDead());
    }

    @Test
    void nothingMovesAfterTheGameIsOver() {
        GameModel game = newGame(Difficulty.NORMAL);
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        tick(game, 11);
        Position head = game.getSnake().getHead();
        tick(game, 3);
        assertEquals(head, game.getSnake().getHead());
        assertFalse(game.turn(Direction.UP));
    }

    @Test
    void resetRestoresTheStartingState() {
        GameModel game = newGame(Difficulty.NORMAL);
        game.placeCoin(new GoldCoin(new Position(9, 10)));
        game.tick();
        game.getObstacles().add(new Position(1, 1));
        game.reset();
        assertEquals(0, game.getScore());
        assertEquals(2, game.getSnake().length());
        assertEquals(0, game.getObstacles().count());
        assertFalse(game.isOver());
    }

    @Test
    void fillingTheBoardWinsInsteadOfFreezing() {
        // 5x5 board: block every cell except the snake and one coin cell in front of it
        GameModel game = new GameModel(new GameSettings(5, 5, false, Difficulty.NORMAL), new Random(1));
        Position coinCell = new Position(1, 2);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                Position p = new Position(x, y);
                if (!game.getSnake().occupies(p) && !p.equals(coinCell)) {
                    game.getObstacles().add(p);
                }
            }
        }
        game.placeCoin(new BasicCoin(coinCell));
        game.tick();
        assertTrue(game.isWon());
        assertNull(game.getCoin());
    }

    @Test
    void listenersHearAboutCoinsAndTheEndOfTheGame() {
        GameModel game = newGame(Difficulty.NORMAL);
        List<Coin> eaten = new ArrayList<>();
        List<Boolean> endings = new ArrayList<>();
        game.addListener(new GameListener() {
            @Override
            public void onCoinEaten(Coin coin) {
                eaten.add(coin);
            }

            @Override
            public void onGameOver(boolean won) {
                endings.add(won);
            }
        });
        Coin gold = new GoldCoin(new Position(9, 10));
        game.placeCoin(gold);
        game.tick();
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        tick(game, 15);
        assertEquals(List.of(gold), eaten);
        assertEquals(List.of(false), endings);
    }
}

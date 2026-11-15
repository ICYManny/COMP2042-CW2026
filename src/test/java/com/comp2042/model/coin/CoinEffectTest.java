package com.comp2042.model.coin;

import com.comp2042.model.Difficulty;
import com.comp2042.model.GameModel;
import com.comp2042.model.GameSettings;
import com.comp2042.model.Position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoinEffectTest {

    private GameModel game;

    @BeforeEach
    void setUp() {
        game = new GameModel(new GameSettings(20, 20, true, Difficulty.NORMAL), new Random(8));
    }

    @Test
    void pointValues() {
        Position p = new Position(0, 0);
        assertEquals(5, new BasicCoin(p).getPoints());
        assertEquals(15, new GoldCoin(p).getPoints());
        assertEquals(5, new BlueCoin(p).getPoints());
        assertEquals(5, new RainbowCoin(p).getPoints());
        assertEquals(5, new SilverCoin(p).getPoints());
    }

    @Test
    void rainbowCoinSpeedsTheSnakeUp() {
        double before = game.getSpeed();
        new RainbowCoin(new Position(0, 0)).applyEffect(game);
        assertEquals(before + 5, game.getSpeed(), 1e-9);
    }

    @Test
    void silverCoinAddsObstacles() {
        new SilverCoin(new Position(0, 0)).applyEffect(game);
        assertTrue(game.getObstacles().count() >= 3);
        game.getObstacles().getPositions()
                .forEach(p -> assertTrue(game.getSnake().getHead().distanceTo(p) >= 3));
    }

    @Test
    void blueCoinLetsTheSnakeWrapOnSolidWalls() {
        new BlueCoin(new Position(0, 0)).applyEffect(game);
        for (int i = 0; i < 11; i++) {
            game.tick();
        }
        assertEquals(false, game.isDead());
    }

    @Test
    void spriteKeysAreDistinct() {
        Position p = new Position(0, 0);
        assertEquals(5, java.util.Set.of(new BasicCoin(p).getSpriteKey(), new GoldCoin(p).getSpriteKey(),
                new BlueCoin(p).getSpriteKey(), new RainbowCoin(p).getSpriteKey(),
                new SilverCoin(p).getSpriteKey()).size());
    }
}

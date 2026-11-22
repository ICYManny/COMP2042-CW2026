package com.comp2042.model.coin;

import com.comp2042.model.Difficulty;
import com.comp2042.model.Effect;
import com.comp2042.model.GameModel;
import com.comp2042.model.GameSettings;
import com.comp2042.model.Position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoinEffectTest {

    private static final Position CELL = new Position(0, 0);
    private GameModel game;

    @BeforeEach
    void setUp() {
        game = new GameModel(new GameSettings(20, 20, true, Difficulty.HARD), new Random(8));
    }

    @Test
    void pointValues() {
        assertEquals(5, new BasicCoin(CELL).getPoints());
        assertEquals(15, new GoldCoin(CELL).getPoints());
        assertEquals(5, new GhostCoin(CELL).getPoints());
        assertEquals(5, new SlowCoin(CELL).getPoints());
        assertEquals(5, new ShrinkCoin(CELL).getPoints());
        assertEquals(5, new DoublePointsCoin(CELL).getPoints());
    }

    @Test
    void basicAndGoldCoinsHaveNoEffect() {
        new BasicCoin(CELL).applyEffect(game);
        new GoldCoin(CELL).applyEffect(game);
        assertTrue(game.getActiveEffects().isEmpty());
    }

    @Test
    void slowCoinStartsSlow() {
        new SlowCoin(CELL).applyEffect(game);
        assertTrue(game.isEffectActive(Effect.SLOW));
        assertTrue(game.getSpeed() < Difficulty.HARD.getSpeed());
    }

    @Test
    void ghostCoinStartsGhost() {
        new GhostCoin(CELL).applyEffect(game);
        assertTrue(game.isEffectActive(Effect.GHOST));
    }

    @Test
    void doublePointsCoinStartsDoublePoints() {
        new DoublePointsCoin(CELL).applyEffect(game);
        assertTrue(game.isEffectActive(Effect.DOUBLE_POINTS));
    }

    @Test
    void rainbowCoinSpeedsTheSnakeUp() {
        new RainbowCoin(CELL).applyEffect(game);
        assertTrue(game.isEffectActive(Effect.SPEED_BOOST));
        assertTrue(game.getSpeed() > Difficulty.HARD.getSpeed());
    }

    @Test
    void silverCoinAddsObstaclesAwayFromTheHead() {
        new SilverCoin(CELL).applyEffect(game);
        assertTrue(game.getObstacles().count() >= 3);
        game.getObstacles().getPositions()
                .forEach(p -> assertTrue(game.getSnake().getHead().distanceTo(p) >= 3));
    }

    @Test
    void shrinkCoinNeverMakesTheSnakeTooShort() {
        new ShrinkCoin(CELL).applyEffect(game);
        assertEquals(2, game.getSnake().length());
        assertFalse(game.isOver());
    }

    @Test
    void spriteKeysAreDistinct() {
        Set<String> keys = Stream.of(new BasicCoin(CELL), new GoldCoin(CELL), new GhostCoin(CELL),
                        new RainbowCoin(CELL), new SilverCoin(CELL), new SlowCoin(CELL), new ShrinkCoin(CELL),
                        new DoublePointsCoin(CELL))
                .map(Coin::getSpriteKey).collect(Collectors.toSet());
        assertEquals(8, keys.size());
    }
}

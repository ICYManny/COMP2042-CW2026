package com.comp2042.model.coin;

import com.comp2042.model.Position;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoinFactoryTest {

    private static final Position CELL = new Position(3, 4);

    @Test
    void onlyBasicCoinsWhenSpecialCoinsAreOff() {
        CoinFactory factory = new CoinFactory(new Random(5), false);
        for (int i = 0; i < 200; i++) {
            assertInstanceOf(BasicCoin.class, factory.create(CELL, false));
        }
    }

    @Test
    void everyCoinTypeCanAppear() {
        // regression test: RainbowCoin was unreachable because two branches checked ran == 1
        CoinFactory factory = new CoinFactory(new Random(11), true);
        Set<Class<?>> seen = new HashSet<>();
        for (int i = 0; i < 2000; i++) {
            seen.add(factory.create(CELL, false).getClass());
        }
        assertEquals(Set.of(BasicCoin.class, GoldCoin.class, RainbowCoin.class, BlueCoin.class, SilverCoin.class), seen);
    }

    @Test
    void noSilverCoinWhileObstaclesArePresent() {
        CoinFactory factory = new CoinFactory(new Random(11), true);
        for (int i = 0; i < 2000; i++) {
            assertFalse(factory.create(CELL, true) instanceof SilverCoin);
        }
    }

    @Test
    void coinIsPlacedWhereRequested() {
        CoinFactory factory = new CoinFactory(new Random(1), true);
        assertEquals(CELL, factory.create(CELL, false).getPosition());
    }

    @Test
    void basicCoinsAreTheMostCommon() {
        CoinFactory factory = new CoinFactory(new Random(2), true);
        int basic = 0;
        for (int i = 0; i < 1200; i++) {
            if (factory.create(CELL, false) instanceof BasicCoin) {
                basic++;
            }
        }
        assertTrue(basic > 600, "expected mostly basic coins, got " + basic);
    }
}

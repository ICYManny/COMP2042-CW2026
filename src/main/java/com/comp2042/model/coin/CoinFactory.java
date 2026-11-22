package com.comp2042.model.coin;

import com.comp2042.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Decides which kind of coin appears next and creates it (Factory pattern).
 * <p>
 * Each coin type is registered with a weight: a coin with weight 6 is six times as likely
 * as one with weight 1. Adding a new coin is one more {@code register} line, without
 * changing how coins are chosen (Open/Closed Principle).
 */
public class CoinFactory {

    private final Random random;
    private final boolean specialCoinsEnabled;
    private final List<Entry> entries = new ArrayList<>();
    private int totalWeight;

    private record Entry(int weight, boolean needsEmptyBoard, Function<Position, Coin> creator) {
    }

    /**
     * Creates a factory with the game's standard mix of coins: mostly basic coins, a gold
     * bonus coin, four power-ups (Slow, Shrink, Ghost, x2 Points) and two hazards (Rainbow
     * speed-up and Silver obstacles).
     *
     * @param random              source of randomness
     * @param specialCoinsEnabled false to only ever create basic coins
     */
    public CoinFactory(Random random, boolean specialCoinsEnabled) {
        this.random = random;
        this.specialCoinsEnabled = specialCoinsEnabled;
        register(60, false, BasicCoin::new);
        register(8, false, GoldCoin::new);
        register(6, false, SlowCoin::new);
        register(6, false, ShrinkCoin::new);
        register(6, false, GhostCoin::new);
        register(6, false, DoublePointsCoin::new);
        register(5, false, RainbowCoin::new);
        register(5, true, SilverCoin::new);
    }

    /**
     * Adds a coin type to the mix.
     *
     * @param weight          relative chance of this coin appearing, at least 1
     * @param needsEmptyBoard true if the coin may only appear when there are no obstacles
     * @param creator         makes the coin at a position
     */
    public final void register(int weight, boolean needsEmptyBoard, Function<Position, Coin> creator) {
        if (weight < 1) {
            throw new IllegalArgumentException("Weight must be at least 1");
        }
        entries.add(new Entry(weight, needsEmptyBoard, creator));
        totalWeight += weight;
    }

    /**
     * Creates the next coin. A coin that needs an empty board (silver, which adds
     * obstacles) is replaced by a basic coin while obstacles are present, so obstacles do
     * not pile up.
     *
     * @param position         where the coin goes
     * @param obstaclesPresent true if there are already obstacles on the board
     * @return the new coin
     */
    public Coin create(Position position, boolean obstaclesPresent) {
        if (!specialCoinsEnabled) {
            return new BasicCoin(position);
        }
        int roll = random.nextInt(totalWeight);
        for (Entry entry : entries) {
            roll -= entry.weight();
            if (roll < 0) {
                if (entry.needsEmptyBoard() && obstaclesPresent) {
                    return new BasicCoin(position);
                }
                return entry.creator().apply(position);
            }
        }
        return new BasicCoin(position);
    }
}

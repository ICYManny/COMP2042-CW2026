package com.comp2042.model.coin;

import com.comp2042.model.Position;

import java.util.Random;

/**
 * Decides which kind of coin appears next and creates it (Factory pattern).
 * <p>
 * The odds of each coin type were an if/else chain inside the game logic. Keeping them
 * here means GameModel just asks for "the next coin", and changing the mix of coins or
 * adding a new one only touches this class.
 */
public class CoinFactory {

    private static final int SPECIAL_COIN_ODDS = 12;

    private final Random random;
    private final boolean specialCoinsEnabled;

    /**
     * @param random              source of randomness
     * @param specialCoinsEnabled false to only ever create basic coins
     */
    public CoinFactory(Random random, boolean specialCoinsEnabled) {
        this.random = random;
        this.specialCoinsEnabled = specialCoinsEnabled;
    }

    /**
     * Creates the next coin. Each special coin has a 1 in 12 chance; a silver coin is only
     * offered while the board has no obstacles, so obstacles do not pile up.
     *
     * @param position         where the coin goes
     * @param obstaclesPresent true if there are already obstacles on the board
     * @return the new coin
     */
    public Coin create(Position position, boolean obstaclesPresent) {
        if (!specialCoinsEnabled) {
            return new BasicCoin(position);
        }
        return switch (random.nextInt(SPECIAL_COIN_ODDS)) {
            case 1 -> new GoldCoin(position);
            case 2 -> new RainbowCoin(position);
            case 3 -> new BlueCoin(position);
            case 4 -> obstaclesPresent ? new BasicCoin(position) : new SilverCoin(position);
            default -> new BasicCoin(position);
        };
    }
}

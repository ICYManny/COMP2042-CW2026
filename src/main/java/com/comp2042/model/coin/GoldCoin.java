package com.comp2042.model.coin;

import com.comp2042.model.Obstacles;
import com.comp2042.model.Snake;

/**
 * Worth 15 points, three times a basic coin.
 */
public class GoldCoin extends Coin {

    private static final int POINTS = 15;

    public GoldCoin(int width, int height, Snake snake, Obstacles obstacles) {
        super(width, height, snake, obstacles);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "goldcoin";
    }
}

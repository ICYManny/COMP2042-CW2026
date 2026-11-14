package com.comp2042.model.coin;

import com.comp2042.model.Position;

/**
 * Worth 15 points, three times a basic coin.
 */
public class GoldCoin extends Coin {

    private static final int POINTS = 15;

    public GoldCoin(Position position) {
        super(position);
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

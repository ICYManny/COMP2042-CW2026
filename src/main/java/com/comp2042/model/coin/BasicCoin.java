package com.comp2042.model.coin;

import com.comp2042.model.Position;

/**
 * The ordinary red coin: worth 5 points, no special effect.
 * (Previously the base {@code Coin} class itself.)
 */
public class BasicCoin extends Coin {

    private static final int POINTS = 5;

    public BasicCoin(Position position) {
        super(position);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "redcoin";
    }
}

package com.comp2042.model.coin;

import com.comp2042.model.GameModel;
import com.comp2042.model.Position;

/**
 * Speeds the snake up until the next coin is eaten.
 */
public class RainbowCoin extends Coin {

    private static final int POINTS = 5;
    private static final double SPEED_BONUS = 5;

    public RainbowCoin(Position position) {
        super(position);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "rainbowcoin";
    }

    @Override
    public void applyEffect(GameModel game) {
        game.boostSpeed(SPEED_BONUS);
    }
}

package com.comp2042.model.coin;

import com.comp2042.model.GameModel;
import com.comp2042.model.Position;

/**
 * Lets the snake pass through the outer walls until the next coin is eaten.
 */
public class BlueCoin extends Coin {

    private static final int POINTS = 5;

    public BlueCoin(Position position) {
        super(position);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "bluecoin";
    }

    @Override
    public void applyEffect(GameModel game) {
        game.setIgnoreBorders(true);
    }
}

package com.comp2042.model.coin;

import com.comp2042.model.GameModel;
import com.comp2042.model.Obstacles;
import com.comp2042.model.Snake;

/**
 * Lets the snake pass through the outer walls until the next coin is eaten.
 */
public class BlueCoin extends Coin {

    private static final int POINTS = 5;

    public BlueCoin(int width, int height, Snake snake, Obstacles obstacles) {
        super(width, height, snake, obstacles);
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

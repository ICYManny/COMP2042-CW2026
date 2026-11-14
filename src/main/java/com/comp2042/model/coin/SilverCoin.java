package com.comp2042.model.coin;

import com.comp2042.model.GameModel;
import com.comp2042.model.Obstacles;
import com.comp2042.model.Snake;

/**
 * Scatters new obstacles over the board.
 */
public class SilverCoin extends Coin {

    private static final int POINTS = 5;

    public SilverCoin(int width, int height, Snake snake, Obstacles obstacles) {
        super(width, height, snake, obstacles);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "silvercoin";
    }

    @Override
    public void applyEffect(GameModel game) {
        game.spawnObstacles();
    }
}

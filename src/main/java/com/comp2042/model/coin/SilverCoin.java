package com.comp2042.model.coin;

import com.comp2042.model.Obstacles;
import com.comp2042.model.Snake;


public class SilverCoin extends Coin {

    public SilverCoin(int x, int y, Snake snake, Obstacles obstacles) {
        super(x, y, snake, obstacles);
    }

    public String getSpriteKey() {
        return "silvercoin";
    }

    public int getPoints() {
        int points = 5;
        return points;
    }
}

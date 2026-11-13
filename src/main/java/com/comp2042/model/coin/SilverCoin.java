package com.comp2042.model.coin;

import com.comp2042.model.Obstacle;
import com.comp2042.model.Snake;


public class SilverCoin extends Coin {

    public SilverCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    public String getSpriteKey() {
        return "silvercoin";
    }

    public int getPoints() {
        int points = 5;
        return points;
    }
}

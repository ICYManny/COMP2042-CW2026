package com.comp2042.model.coin;

import com.comp2042.model.Obstacle;
import com.comp2042.model.Snake;


public class BlueCoin extends Coin {

    public BlueCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public String getSpriteKey() {
        return "bluecoin";
    }
}

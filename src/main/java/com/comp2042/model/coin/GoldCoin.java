package com.comp2042.model.coin;

import com.comp2042.model.Obstacles;
import com.comp2042.model.Snake;


public class GoldCoin extends Coin {

    public GoldCoin(int x, int y, Snake snake, Obstacles obstacles) {
        super(x, y, snake, obstacles);
    }

    public String getSpriteKey() {
        return "goldcoin";
    }

    public int getPoints() {
        int points = 15;
        return points;
    }
}

package com.comp2042.model.coin;

import com.comp2042.model.Obstacle;
import com.comp2042.model.Snake;


public class GoldCoin extends Coin {

    public GoldCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    public String getSpriteKey() {
        return "goldcoin";
    }

    public int getPoints() {
        int points = 15;
        return points;
    }
}

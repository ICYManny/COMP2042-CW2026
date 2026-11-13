package com.comp2042.model.coin;

import com.comp2042.model.Obstacles;
import com.comp2042.model.Snake;


public class RainbowCoin extends Coin {

    public RainbowCoin(int x, int y, Snake snake, Obstacles obstacles) {
        super(x, y, snake, obstacles);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public double getSpeedBonus() {
        double speedBonus = 5;
        return speedBonus;
    }

    public String getSpriteKey() {
        return "rainbowcoin";
    }
}

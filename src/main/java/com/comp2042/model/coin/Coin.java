package com.comp2042.model.coin;

import com.comp2042.model.Obstacle;
import com.comp2042.model.Snake;


import java.awt.*;
import java.util.Random;

public class Coin {
    private Point pos;

    public Coin(int x, int y, Snake snake, Obstacle obstacle) {
        int tx;
        int ty;
        do {
            Random rnd = new Random();

            tx = rnd.nextInt(x);
            ty = rnd.nextInt(y);
            pos = new Point(tx, ty);
        } while (snake.getHead().equals(pos) || snake.getBody().contains(pos) || obstacle.getObstacles().contains(pos));
    }

    public Point getPosition() {
        return new Point(pos);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public String getSpriteKey() {
        return "redcoin";
    }
}

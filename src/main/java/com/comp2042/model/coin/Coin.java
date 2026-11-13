package com.comp2042.model.coin;

import com.comp2042.model.Obstacles;
import com.comp2042.model.Position;
import com.comp2042.model.Snake;

import java.util.Random;

public class Coin {
    private final Position position;

    public Coin(int width, int height, Snake snake, Obstacles obstacles) {
        Random rnd = new Random();
        Position candidate;
        do {
            candidate = new Position(rnd.nextInt(width), rnd.nextInt(height));
        } while (snake.occupies(candidate) || obstacles.contains(candidate));
        position = candidate;
    }

    public Position getPosition() {
        return position;
    }

    public int getPoints() {
        return 5;
    }

    public String getSpriteKey() {
        return "redcoin";
    }
}

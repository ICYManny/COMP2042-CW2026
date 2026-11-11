package com.comp2042;

import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Obstacle {
    private Color color = Color.BLACK;
    private ArrayList<Point> obstacles = new ArrayList<>();
    private int x;
    private int y;
    private static final int SAFE_DISTANCE = 3;

    public Obstacle(int x, int y) {
       this.x = x;
       this.y = y;
    }

    public void newObstacles(Snake snake, Coin coin, int amount) {
        if (obstacles.size() > 50) {
            return;
        }
        Random rnd = new Random();
        Point p;
        for (int i=0; i<amount; i++) {
            do {
                p = new Point(rnd.nextInt(x), rnd.nextInt(y));
            } while (coin.getPosition().equals(p) || snake.getBody().contains(p) || snake.getHead().equals(p)
                    || obstacles.contains(p) || tooCloseToHead(snake, p));
            obstacles.add(p);
        }
    }

    /**
     * Checks whether a cell is within the safe zone around the snake's head, so a new
     * obstacle can never appear directly in front of the player and kill them instantly.
     */
    private boolean tooCloseToHead(Snake snake, Point p) {
        Point head = snake.getHead();
        return Math.abs(head.x - p.x) + Math.abs(head.y - p.y) < SAFE_DISTANCE;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Point> getObstacles() {
        return obstacles;
    }

    public void reset() {
        obstacles.clear();
    }
}

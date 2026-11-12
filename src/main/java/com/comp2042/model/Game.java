package com.comp2042.model;

import javafx.scene.paint.Color;

public class Game {
    private static final Color BACKGROUND_COLOR = Color.web("feffe4");
    private static final double NORMAL_SPEED = 8.5;
    private static final double HARD_SPEED = 13;
    private static final int LARGE_GRID_HEIGHT = 50;
    private static final double SMALL_CELL_SIZE = 10;
    private static final double LARGE_CELL_SIZE = 20;
    /** The snake starts with two segments, so two cells can never hold a coin. */
    private static final int STARTING_LENGTH = 2;

    private final int width;
    private final int height;
    private int coinsEaten;
    private int maxCoins;
    private final double cellSize;
    private int score;
    private double speed;
    private final boolean powerUp;
    private boolean noBorder;

    public Game(int x, int y, boolean powerUp, char difficulty) {
        if (difficulty == 'E') {
            speed = NORMAL_SPEED;
            noBorder = true;
        } else if (difficulty == 'N') {
            speed = NORMAL_SPEED;
            noBorder = false;
        } else if (difficulty == 'H') {
            speed = HARD_SPEED;
            noBorder = false;
        }
        this.powerUp = powerUp;
        width = x;
        height = y;
        maxCoins = x * y - STARTING_LENGTH;
        cellSize = height > LARGE_GRID_HEIGHT ? SMALL_CELL_SIZE : LARGE_CELL_SIZE;
    }

    public boolean gameWon() {
        return coinsEaten == maxCoins;
    }

    public void incrementCoinsEaten() {
        coinsEaten++;
    }

    public void reduceMaxCoins(int obstacleCount) {
        this.maxCoins -= (obstacleCount - 1);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resetScore() {
        coinsEaten = 0;
        score = 0;
    }

    public Color getColor() {
        return BACKGROUND_COLOR;
    }

    public double getCellSize() {
        return cellSize;
    }

    public void addPoints(int n) {
        score += n;
    }

    public int getScore() {
        return score;
    }

    public boolean isPowerUp() {
        return powerUp;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isNoBorder() {
        return noBorder;
    }
}

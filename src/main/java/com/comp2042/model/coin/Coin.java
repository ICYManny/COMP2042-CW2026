package com.comp2042.model.coin;

import com.comp2042.model.GameModel;
import com.comp2042.model.Obstacles;
import com.comp2042.model.Position;
import com.comp2042.model.Snake;

import java.util.Random;

/**
 * Something the snake can eat. Every coin is worth points and may also change the game
 * when eaten.
 * <p>
 * Each subclass applies its own effect in {@link #applyEffect(GameModel)}. Previously
 * GameController checked {@code coin.getClass().equals(...)} for every coin type, so
 * adding a coin meant editing the controller. Now a new coin is just a new subclass
 * (Open/Closed Principle).
 */
public abstract class Coin {
    private final Position position;

    protected Coin(int width, int height, Snake snake, Obstacles obstacles) {
        Random rnd = new Random();
        Position candidate;
        do {
            candidate = new Position(rnd.nextInt(width), rnd.nextInt(height));
        } while (snake.occupies(candidate) || obstacles.contains(candidate));
        position = candidate;
    }

    /** @return the cell this coin occupies */
    public Position getPosition() {
        return position;
    }

    /** @return points awarded for eating this coin */
    public abstract int getPoints();

    /** @return name of the image used to draw this coin */
    public abstract String getSpriteKey();

    /**
     * Applies this coin's special effect to the game. Plain coins have no effect.
     *
     * @param game the game in which the coin was eaten
     */
    public void applyEffect(GameModel game) {
    }
}

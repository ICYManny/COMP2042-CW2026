package com.comp2042.model.coin;

import com.comp2042.model.GameModel;
import com.comp2042.model.Position;

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

    /**
     * @param position the cell the coin is placed on
     */
    protected Coin(Position position) {
        this.position = position;
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

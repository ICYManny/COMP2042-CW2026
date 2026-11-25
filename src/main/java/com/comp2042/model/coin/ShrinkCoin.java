package com.comp2042.model.coin;

import com.comp2042.model.GameModel;
import com.comp2042.model.Position;

/**
 * Power-up: removes segments from the snake's tail. The score is not reduced.
 */
public class ShrinkCoin extends Coin {

    private static final int POINTS = 5;
    private static final int SEGMENTS_REMOVED = 3;

    /**
     * Creates a ShrinkCoin on the given cell.
     *
     * @param position the cell the coin is placed on
     */
    public ShrinkCoin(Position position) {
        super(position);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "shrinkcoin";
    }

    @Override
    public void applyEffect(GameModel game) {
        game.shrinkSnake(SEGMENTS_REMOVED);
    }
}

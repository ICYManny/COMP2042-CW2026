package com.comp2042.model.coin;

import com.comp2042.model.Effect;
import com.comp2042.model.GameModel;
import com.comp2042.model.Position;

/**
 * Hazard: speeds the snake up by half for a few seconds.
 */
public class RainbowCoin extends Coin {

    private static final int POINTS = 5;

    /**
     * Creates a RainbowCoin on the given cell.
     *
     * @param position the cell the coin is placed on
     */
    public RainbowCoin(Position position) {
        super(position);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "rainbowcoin";
    }

    @Override
    public void applyEffect(GameModel game) {
        game.activateEffect(Effect.SPEED_BOOST);
    }
}

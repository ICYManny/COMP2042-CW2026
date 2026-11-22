package com.comp2042.model.coin;

import com.comp2042.model.Effect;
import com.comp2042.model.GameModel;
import com.comp2042.model.Position;

/**
 * Power-up: coins are worth double points for a few seconds.
 */
public class DoublePointsCoin extends Coin {

    private static final int POINTS = 5;

    public DoublePointsCoin(Position position) {
        super(position);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "doublecoin";
    }

    @Override
    public void applyEffect(GameModel game) {
        game.activateEffect(Effect.DOUBLE_POINTS);
    }
}

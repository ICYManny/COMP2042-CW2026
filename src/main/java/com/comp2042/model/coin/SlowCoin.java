package com.comp2042.model.coin;

import com.comp2042.model.Effect;
import com.comp2042.model.GameModel;
import com.comp2042.model.Position;

/**
 * Power-up: slows the snake down for a few seconds.
 */
public class SlowCoin extends Coin {

    private static final int POINTS = 5;

    public SlowCoin(Position position) {
        super(position);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "slowcoin";
    }

    @Override
    public void applyEffect(GameModel game) {
        game.activateEffect(Effect.SLOW);
    }
}
